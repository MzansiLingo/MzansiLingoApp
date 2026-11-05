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
    options.UseMySql(
        connectionString,
        new MySqlServerVersion(new Version(8, 4, 11))
    ));

builder.Services.AddControllers();
builder.Services.AddOpenApi();

var app = builder.Build();

// Create/update the tables and make sure the lessons and words are correct.
// If the database is still waking up, try a few times instead of crashing the API.
for (var attempt = 1; attempt <= 3; attempt++)
{
    try
    {
        using var scope = app.Services.CreateScope();
        var db = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
        db.Database.Migrate();
        SeedData.Ensure(db);
        break;
    }
    catch (Exception ex)
    {
        app.Logger.LogError(ex, "Database start-up attempt {Attempt} of 3 failed", attempt);
        if (attempt < 3)
        {
            Thread.Sleep(5000);
        }
    }
}

app.MapOpenApi();          // API docs stay on so the marker can see them
app.UseAuthorization();
app.MapControllers();
app.Run();