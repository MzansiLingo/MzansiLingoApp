using Microsoft.EntityFrameworkCore;
using MzansiLingo.API.Data;

var builder = WebApplication.CreateBuilder(args);

// Hosts like Railway tell the app which port to use through the PORT variable.
var port = Environment.GetEnvironmentVariable("PORT");
if (!string.IsNullOrEmpty(port))
{
    builder.WebHost.UseUrls($"http://0.0.0.0:{port}");
}

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection")!;

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString)));

builder.Services.AddControllers();
builder.Services.AddOpenApi();

var app = builder.Build();

// Create/update the tables and add starter lessons when the API starts.
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
    db.Database.Migrate();

    if (!db.Lessons.Any())
    {
        var greetings = new Lesson { Title = "Greetings", Description = "Say hello in isiXhosa", Language = "isiXhosa" };
        var numbers = new Lesson { Title = "Numbers 1-5", Description = "Count in isiXhosa", Language = "isiXhosa" };
        var afrikaans = new Lesson { Title = "Greetings", Description = "Say hello in Afrikaans", Language = "Afrikaans" };
        db.Lessons.AddRange(greetings, numbers, afrikaans);
        db.SaveChanges();

        db.Words.AddRange(
            new Word { LessonId = greetings.Id, XhosaWord = "Molo", EnglishMeaning = "Hello" },
            new Word { LessonId = greetings.Id, XhosaWord = "Enkosi", EnglishMeaning = "Thank you" },
            new Word { LessonId = greetings.Id, XhosaWord = "Hamba kakuhle", EnglishMeaning = "Go well" },
            new Word { LessonId = greetings.Id, XhosaWord = "Unjani?", EnglishMeaning = "How are you?" },
            new Word { LessonId = numbers.Id, XhosaWord = "Nye", EnglishMeaning = "One" },
            new Word { LessonId = numbers.Id, XhosaWord = "Mbini", EnglishMeaning = "Two" },
            new Word { LessonId = numbers.Id, XhosaWord = "Ntathu", EnglishMeaning = "Three" },
            new Word { LessonId = numbers.Id, XhosaWord = "Ne", EnglishMeaning = "Four" },
            new Word { LessonId = numbers.Id, XhosaWord = "Ntlanu", EnglishMeaning = "Five" },
            new Word { LessonId = afrikaans.Id, XhosaWord = "Hallo", EnglishMeaning = "Hello" },
            new Word { LessonId = afrikaans.Id, XhosaWord = "Dankie", EnglishMeaning = "Thank you" });
        db.SaveChanges();
    }
}

app.MapOpenApi();          // API docs stay on so the marker can see them
app.UseAuthorization();
app.MapControllers();
app.Run();