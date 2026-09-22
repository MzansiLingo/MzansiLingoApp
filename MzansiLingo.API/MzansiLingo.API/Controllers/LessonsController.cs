using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MzansiLingo.API.Data;

namespace MzansiLingo.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class LessonsController : ControllerBase
{
    private readonly ApplicationDbContext _context;

    public LessonsController(ApplicationDbContext context)
    {
        _context = context;
    }

    // GET: api/lessons?language=isiXhosa
    [HttpGet]
    public async Task<IActionResult> GetLessons(
        [FromQuery] string language = "isiXhosa")
    {
        // Retrieve lessons for the selected language from MySQL.
        var lessons = await _context.Lessons
            .Where(l => l.Language == language)
            .ToListAsync();

        return Ok(lessons);
    }

    // GET: api/lessons/1
    [HttpGet("{id}")]
    public async Task<IActionResult> GetLesson(int id)
    {
        // Find a specific lesson by its ID.
        var lesson = await _context.Lessons
            .FirstOrDefaultAsync(l => l.Id == id);

        if (lesson == null)
        {
            return NotFound(new
            {
                message = "Lesson not found."
            });
        }

        return Ok(lesson);
    }
    // GET: api/lessons/1/words
    [HttpGet("{lessonId}/words")]
    public async Task<IActionResult> GetWords(int lessonId)
    {
        // Retrieve all words belonging to the selected lesson.
        var words = await _context.Words
            .Where(w => w.LessonId == lessonId)
            .ToListAsync();

        return Ok(words);
    }
}