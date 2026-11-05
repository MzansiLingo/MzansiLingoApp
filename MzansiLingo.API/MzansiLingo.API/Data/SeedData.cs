namespace MzansiLingo.API.Data;

public static class SeedData
{
    private record WordSeed(string Xhosa, string English);

    private record LessonSeed(string Title, string Description, string Language, WordSeed[] Words);

    private static readonly LessonSeed[] Lessons =
    {
        new("Greetings", "Say hello in isiXhosa", "isiXhosa", new[]
        {
            new WordSeed("Molo", "Hello"),
            new WordSeed("Unjani?", "How are you?"),
            new WordSeed("Ndiphilile", "I am fine"),
            new WordSeed("Enkosi", "Thank you"),
            new WordSeed("Sala kakuhle", "Goodbye")
        }),
        new("Numbers 1-5", "Count in isiXhosa", "isiXhosa", new[]
        {
            new WordSeed("Inye", "One"),
            new WordSeed("Zimbini", "Two"),
            new WordSeed("Zintathu", "Three"),
            new WordSeed("Zine", "Four"),
            new WordSeed("Zintlanu", "Five")
        }),
        new("Greetings", "Say hello in Afrikaans", "Afrikaans", new[]
        {
            new WordSeed("Hallo", "Hello"),
            new WordSeed("Dankie", "Thank you")
        })
    };

    public static void Ensure(ApplicationDbContext db)
    {
        foreach (var seed in Lessons)
        {
            var lesson = db.Lessons.FirstOrDefault(l => l.Title == seed.Title && l.Language == seed.Language);

            if (lesson == null)
            {
                lesson = new Lesson
                {
                    Title = seed.Title,
                    Description = seed.Description,
                    Language = seed.Language
                };
                db.Lessons.Add(lesson);
                db.SaveChanges();
            }

            var current = db.Words
                .Where(w => w.LessonId == lesson.Id)
                .OrderBy(w => w.Id)
                .ToList();

            var alreadyCorrect =
                current.Count == seed.Words.Length &&
                current.Zip(seed.Words, (c, s) => c.XhosaWord == s.Xhosa && c.EnglishMeaning == s.English)
                       .All(same => same);

            if (alreadyCorrect)
            {
                continue;
            }

            // Replace the words. They are saved one at a time so their IDs follow the lesson order.
            db.Words.RemoveRange(current);
            db.SaveChanges();

            foreach (var word in seed.Words)
            {
                db.Words.Add(new Word
                {
                    LessonId = lesson.Id,
                    XhosaWord = word.Xhosa,
                    EnglishMeaning = word.English
                });
                db.SaveChanges();
            }
        }
    }
}