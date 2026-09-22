namespace MzansiLingo.API.Data;

public class Word
{
    public int Id { get; set; }

    public int LessonId { get; set; }

    public string XhosaWord { get; set; } = string.Empty;

    public string EnglishMeaning { get; set; } = string.Empty;
}