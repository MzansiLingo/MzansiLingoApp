using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MzansiLingo.API.Data;

namespace MzansiLingo.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class UsersController : ControllerBase
{
    private readonly ApplicationDbContext _context;

    public UsersController(ApplicationDbContext context)
    {
        _context = context;
    }

    // GET: api/users/1
    [HttpGet("{userId}")]
    public async Task<IActionResult> GetUser(int userId)
    {
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return NotFound(new { message = "User not found." });
        }

        return Ok(new
        {
            id = user.Id,
            fullName = user.FullName,
            email = user.Email,
            username = user.Username,
            totalXp = user.TotalXp,
        });
    }

    // POST: api/users/1/xp
    [HttpPost("{userId}/xp")]
    public async Task<IActionResult> AddXp(
        int userId,
        [FromBody] AddXpRequest request)
    {
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return NotFound(new { message = "User not found." });
        }

        // Prevent invalid XP values.
        if (request.Xp <= 0)
        {
            return BadRequest(new { message = "XP must be greater than zero." });
        }

        user.TotalXp += request.Xp;

        await _context.SaveChangesAsync();

        return Ok(new
        {
            message = "XP added successfully.",
            userId = user.Id,
            totalXp = user.TotalXp
        });
    }

    // GET: api/users/1/xp
    [HttpGet("{userId}/xp")]
    public async Task<IActionResult> GetXp(int userId)
    {
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return NotFound(new { message = "User not found." });
        }

        return Ok(new
        {
            userId = user.Id,
            totalXp = user.TotalXp
        });
    }

    // PUT: api/users/1/settings
    [HttpPut("{userId}/settings")]
    public async Task<IActionResult> UpdateSettings(int userId, [FromBody] UserSettingsDto settings)
    {
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return NotFound(new { message = "User not found." });
        }

        user.PushNotifications = settings.PushNotifications;
        user.DailyReminder = settings.DailyReminder;
        user.DarkMode = settings.DarkMode;
        user.SoundEffects = settings.SoundEffects;
        user.LearningLanguage = settings.LearningLanguage;
        user.OfflineLessons = settings.OfflineLessons;

        await _context.SaveChangesAsync();

        return Ok(settings);
    }
}

public class AddXpRequest
{
    public int Xp { get; set; }
}

public class UserSettingsDto
{
    public bool PushNotifications { get; set; }
    public bool DailyReminder { get; set; }
    public bool DarkMode { get; set; }
    public bool SoundEffects { get; set; }
    public string LearningLanguage { get; set; } = "isiXhosa";
    public bool OfflineLessons { get; set; }
}