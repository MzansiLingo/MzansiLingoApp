using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MzansiLingo.API.Data;
using MzansiLingo.API.Models;

namespace MzansiLingo.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly ApplicationDbContext _context;
    private readonly PasswordHasher<User> _passwordHasher;

    public AuthController(ApplicationDbContext context)
    {
        _context = context;
        _passwordHasher = new PasswordHasher<User>();
    }

    [HttpPost("register")]
    public async Task<IActionResult> Register(RegisterRequest request)
    {
        // Check that all required fields were provided.
        if (string.IsNullOrWhiteSpace(request.FullName) ||
            string.IsNullOrWhiteSpace(request.Email) ||
            string.IsNullOrWhiteSpace(request.Username) ||
            string.IsNullOrWhiteSpace(request.Password))
        {
            return BadRequest(new
            {
                message = "All fields are required."
            });
        }

        // Check whether the email is already registered.
        if (await _context.Users.AnyAsync(u => u.Email == request.Email))
        {
            return Conflict(new
            {
                message = "An account with this email already exists."
            });
        }

        // Check whether the username is already registered.
        if (await _context.Users.AnyAsync(u => u.Username == request.Username))
        {
            return Conflict(new
            {
                message = "This username is already taken."
            });
        }

        // Create a new user.
        var user = new User
        {
            FullName = request.FullName.Trim(),
            Email = request.Email.Trim(),
            Username = request.Username.Trim()
        };

        // Hash the password on the server before storing it.
        user.PasswordHash = _passwordHasher.HashPassword(
            user,
            request.Password
        );

        // Save the user to MySQL.
        _context.Users.Add(user);
        await _context.SaveChangesAsync();

        // Don't return the password hash to the Android application.
        return Ok(new
        {
            message = "Account created successfully.",
            user = new
            {
                user.Id,
                user.FullName,
                user.Email,
                user.Username
            }
        });
    }
    [HttpPost("login")]
    public async Task<IActionResult> Login(LoginRequest request)
    {
        // Check that the required fields were provided.
        if (string.IsNullOrWhiteSpace(request.Email) ||
            string.IsNullOrWhiteSpace(request.Password))
        {
            return BadRequest(new
            {
                message = "Email and password are required."
            });
        }

        // Find the user by email.
        var user = await _context.Users
            .FirstOrDefaultAsync(u => u.Email == request.Email.Trim());

        // Don't reveal whether the email exists.
        if (user == null)
        {
            return Unauthorized(new
            {
                message = "Invalid email or password."
            });
        }

        // Verify the entered password against the stored password hash.
        var passwordResult = _passwordHasher.VerifyHashedPassword(
            user,
            user.PasswordHash,
            request.Password
        );

        if (passwordResult == PasswordVerificationResult.Failed)
        {
            return Unauthorized(new
            {
                message = "Invalid email or password."
            });
        }

        // Return user information without exposing the password hash.
        return Ok(new
        {
            message = "Login successful.",
            user = new
            {
                user.Id,
                user.FullName,
                user.Email,
                user.Username
            }
        });
    }
}