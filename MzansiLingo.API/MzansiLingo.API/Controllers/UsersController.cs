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
        // Find the user in the database.
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return NotFound(new
            {
                message = "User not found."
            });
        }

        // Return the user's profile information.
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
        // Find the user in the database.
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return NotFound(new
            {
                message = "User not found."
            });
        }

        // Prevent invalid XP values.
        if (request.Xp <= 0)
        {
            return BadRequest(new
            {
                message = "XP must be greater than zero."
            });
        }

        // Add the earned XP to the user's total.
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
        // Find the user and return their current XP.
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return NotFound(new
            {
                message = "User not found."
            });
        }

        return Ok(new
        {
            userId = user.Id,
            totalXp = user.TotalXp
        });
    }
}

public class AddXpRequest
{
    public int Xp { get; set; }
}