using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MzansiLingo.API.Controllers;
using MzansiLingo.API.Data;
using MzansiLingo.API.Models;
using Xunit;

namespace MzansiLingo.API.Tests;

public class ApiTests
{
    private static ApplicationDbContext NewDb()
    {
        var options = new DbContextOptionsBuilder<ApplicationDbContext>()
            .UseInMemoryDatabase(Guid.NewGuid().ToString())
            .Options;
        return new ApplicationDbContext(options);
    }

    private static RegisterRequest NewUser(string email = "thandi@test.com", string username = "thandi") => new()
    {
        FullName = "Thandi Test",
        Email = email,
        Username = username,
        Password = "Password123"
    };

    [Fact]
    public async Task Register_ValidRequest_ReturnsOk()
    {
        var controller = new AuthController(NewDb());
        var result = await controller.Register(NewUser());
        Assert.IsType<OkObjectResult>(result);
    }

    [Fact]
    public async Task Register_DuplicateEmail_ReturnsConflict()
    {
        var controller = new AuthController(NewDb());
        await controller.Register(NewUser());
        var result = await controller.Register(NewUser(username: "other"));
        Assert.IsType<ConflictObjectResult>(result);
    }

    [Fact]
    public async Task Register_MissingFields_ReturnsBadRequest()
    {
        var controller = new AuthController(NewDb());
        var result = await controller.Register(new RegisterRequest());
        Assert.IsType<BadRequestObjectResult>(result);
    }

    [Fact]
    public async Task Login_CorrectPassword_ReturnsOk()
    {
        var controller = new AuthController(NewDb());
        await controller.Register(NewUser());
        var result = await controller.Login(new LoginRequest { Email = "thandi@test.com", Password = "Password123" });
        Assert.IsType<OkObjectResult>(result);
    }

    [Fact]
    public async Task Login_WrongPassword_ReturnsUnauthorized()
    {
        var controller = new AuthController(NewDb());
        await controller.Register(NewUser());
        var result = await controller.Login(new LoginRequest { Email = "thandi@test.com", Password = "wrong" });
        Assert.IsType<UnauthorizedObjectResult>(result);
    }

    [Fact]
    public async Task GetLessons_FiltersByLanguage()
    {
        var db = NewDb();
        db.Lessons.Add(new Lesson { Title = "Greetings", Language = "isiXhosa" });
        db.Lessons.Add(new Lesson { Title = "Groete", Language = "Afrikaans" });
        await db.SaveChangesAsync();

        var result = await new LessonsController(db).GetLessons("isiXhosa");

        var ok = Assert.IsType<OkObjectResult>(result);
        var lessons = Assert.IsAssignableFrom<List<Lesson>>(ok.Value);
        Assert.Single(lessons);
    }
}