namespace MzansiLingo.API.Models;

public class CompletedLesson
{
    public int UserId { get; set; }
    public int LessonId { get; set; }

    public DateTime CompletedAt { get; set; } = DateTime.UtcNow;
}