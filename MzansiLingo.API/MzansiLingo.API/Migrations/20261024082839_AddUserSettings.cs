using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MzansiLingo.API.Migrations
{
    /// <inheritdoc />
    public partial class AddUserSettings : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "DailyReminder",
                table: "Users",
                type: "tinyint(1)",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<bool>(
                name: "DarkMode",
                table: "Users",
                type: "tinyint(1)",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<string>(
                name: "LearningLanguage",
                table: "Users",
                type: "longtext",
                nullable: false)
                .Annotation("MySql:CharSet", "utf8mb4");

            migrationBuilder.AddColumn<bool>(
                name: "OfflineLessons",
                table: "Users",
                type: "tinyint(1)",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<bool>(
                name: "PushNotifications",
                table: "Users",
                type: "tinyint(1)",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<bool>(
                name: "SoundEffects",
                table: "Users",
                type: "tinyint(1)",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "DailyReminder",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "DarkMode",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "LearningLanguage",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "OfflineLessons",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "PushNotifications",
                table: "Users");

            migrationBuilder.DropColumn(
                name: "SoundEffects",
                table: "Users");
        }
    }
}
