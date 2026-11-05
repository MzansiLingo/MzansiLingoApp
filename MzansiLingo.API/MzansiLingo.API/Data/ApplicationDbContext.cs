using Microsoft.EntityFrameworkCore;
using MzansiLingo.API.Models; // Adjust if your models use another namespace

namespace MzansiLingo.API.Data;

public class ApplicationDbContext : DbContext
{
    public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
        : base(options)
    {
    }

    public DbSet<User> Users { get; set; }
    public DbSet<Lesson> Lessons { get; set; }
    public DbSet<Word> Words { get; set; }

    public DbSet<CompletedLesson> CompletedLessons { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<CompletedLesson>()
            .HasKey(completed => new
            {
                completed.UserId,
                completed.LessonId
            });
    }
}