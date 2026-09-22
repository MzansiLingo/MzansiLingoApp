namespace MzansiLingo.API.Data;

public class Lesson
{
    public int Id { get; set; }

    public string Title { get; set; } = string.Empty;

    public string Description { get; set; } = string.Empty;

    public string Language { get; set; } = string.Empty;
}