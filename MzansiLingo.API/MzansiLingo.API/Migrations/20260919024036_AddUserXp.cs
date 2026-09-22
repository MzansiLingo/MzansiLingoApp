using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MzansiLingo.API.Migrations
{
    /// <inheritdoc />
    public partial class AddUserXp : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "TotalXp",
                table: "Users",
                type: "int",
                nullable: false,
                defaultValue: 0);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "TotalXp",
                table: "Users");
        }
    }
}
