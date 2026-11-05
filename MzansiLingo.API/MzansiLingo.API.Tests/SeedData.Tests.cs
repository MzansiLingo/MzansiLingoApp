using Microsoft.EntityFrameworkCore;
using MzansiLingo.API.Data;
using Xunit;

namespace MzansiLingo.API.Tests;

public class SeedDataTests
{
	private static ApplicationDbContext NewDb()
	{
		var options = new DbContextOptionsBuilder<ApplicationDbContext>()
			.UseInMemoryDatabase(Guid.NewGuid().ToString())
			.Options;
		return new ApplicationDbContext(options);
	}

	private static List<string> WordsOf(ApplicationDbContext db, string title, string language)
	{
		var lesson = db.Lessons.Single(l => l.Title == title && l.Language == language);
		return db.Words.Where(w => w.LessonId == lesson.Id).OrderBy(w => w.Id).Select(w => w.XhosaWord).ToList();
	}

	[Fact]
	public void Ensure_CreatesGreetingsInTheCorrectOrder()
	{
		var db = NewDb();
		SeedData.Ensure(db);

		Assert.Equal(
			new[] { "Molo", "Unjani?", "Ndiphilile", "Enkosi", "Sala kakuhle" },
			WordsOf(db, "Greetings", "isiXhosa"));
	}

	[Fact]
	public void Ensure_CreatesNumbersOneToFive()
	{
		var db = NewDb();
		SeedData.Ensure(db);

		Assert.Equal(
			new[] { "Inye", "Zimbini", "Zintathu", "Zine", "Zintlanu" },
			WordsOf(db, "Numbers 1-5", "isiXhosa"));
	}

	[Fact]
	public void Ensure_RunTwice_DoesNotDuplicateWords()
	{
		var db = NewDb();
		SeedData.Ensure(db);
		SeedData.Ensure(db);

		Assert.Equal(5, WordsOf(db, "Greetings", "isiXhosa").Count);
		Assert.Equal(5, WordsOf(db, "Numbers 1-5", "isiXhosa").Count);
	}

	[Fact]
	public void Ensure_ReplacesOldWrongWords()
	{
		var db = NewDb();

		var greetings = new Lesson { Title = "Greetings", Description = "x", Language = "isiXhosa" };
		db.Lessons.Add(greetings);
		db.SaveChanges();
		db.Words.Add(new Word { LessonId = greetings.Id, XhosaWord = "Molo", EnglishMeaning = "Hello" });
		db.Words.Add(new Word { LessonId = greetings.Id, XhosaWord = "Enkosi", EnglishMeaning = "Thank you" });
		db.Words.Add(new Word { LessonId = greetings.Id, XhosaWord = "Hamba kakuhle", EnglishMeaning = "Go well" });
		db.Words.Add(new Word { LessonId = greetings.Id, XhosaWord = "Unjani?", EnglishMeaning = "How are you?" });
		db.SaveChanges();

		SeedData.Ensure(db);

		Assert.Equal(
			new[] { "Molo", "Unjani?", "Ndiphilile", "Enkosi", "Sala kakuhle" },
			WordsOf(db, "Greetings", "isiXhosa"));
		Assert.Equal(greetings.Id, db.Lessons.Single(l => l.Title == "Greetings" && l.Language == "isiXhosa").Id);
	}
}