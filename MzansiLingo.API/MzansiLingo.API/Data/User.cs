namespace MzansiLingo.API.Data;

public class User
{
    public int Id { get; set; }

    public string FullName { get; set; } = string.Empty;

    public string Email { get; set; } = string.Empty;

    public string Username { get; set; } = string.Empty;

    public string PasswordHash { get; set; } = string.Empty;

    // Total XP earned by the user from completed lessons.
    public int TotalXp { get; set; } = 0;

    public bool PushNotifications { get; set; } = true;
    public bool DailyReminder { get; set; } = true;
    public bool DarkMode { get; set; } = false;
    public bool SoundEffects { get; set; } = true;
    public string LearningLanguage { get; set; } = "isiXhosa";
    public bool OfflineLessons { get; set; } = false;
}