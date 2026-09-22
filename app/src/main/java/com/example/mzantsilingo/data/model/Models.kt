package com.example.mzantsilingo.data.model

/**
 * Represents a MzansiLingo user account.
 *
 * The model is used to store user information returned by the REST API.
 */
data class User(
    // Unique identifier assigned to the user by the database.
    val id: Int? = null,

    // User's full name.
    val fullName: String,

    // Email address associated with the account.
    val email: String,

    // Username used to identify the user within the application.
    val username: String,

    // Stored password hash returned by the API when applicable.
    val passwordHash: String,

    // Total experience points earned by the user.
    val totalXp: Int = 0,
)

/**
 * Contains the credentials submitted when a user logs into the application.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Represents a language lesson available in MzansiLingo.
 */
data class Lesson(
    // Unique identifier for the lesson.
    val id: Int,

    // Name of the lesson displayed to the user.
    val title: String,

    // Short explanation of what the lesson teaches.
    val description: String = "",

    // Language that the lesson belongs to.
    val language: String,

    // Number of vocabulary words associated with the lesson.
    val wordCount: Int = 0,

    // Indicates whether the user has completed the lesson.
    val isCompleted: Boolean = false,

    // Indicates whether the lesson is currently locked.
    val isLocked: Boolean = false
)

/**
 * Represents a vocabulary word belonging to a lesson.
 */
data class Word(
    // Unique identifier for the vocabulary word.
    val id: Int,

    // ID of the lesson that contains this word.
    val lessonId: Int,

    // The isiXhosa word being taught.
    val xhosaWord: String,

    // English translation or meaning of the word.
    val englishMeaning: String
)

/**
 * Represents a multiple-choice quiz question.
 */
data class QuizQuestion(
    // Unique identifier for the quiz question.
    val id: String,

    // Question or instruction displayed to the learner.
    val prompt: String,

    // Possible answers presented to the learner.
    val options: List<String>,

    // Position of the correct answer within the options list.
    val correctAnswerIndex: Int
)

/**
 * Represents an achievement that can be earned by a user.
 */
data class Achievement(
    // Unique identifier for the achievement.
    val id: String,

    // Name of the achievement displayed to the user.
    val title: String,

    // Indicates whether the user has unlocked the achievement.
    val isUnlocked: Boolean
)

/**
 * Stores the user's application and learning preferences.
 */
data class UserSettings(
    // Controls whether push notifications are enabled.
    val pushNotifications: Boolean = true,

    // Controls whether daily learning reminders are enabled.
    val dailyReminder: Boolean = true,

    // Controls whether dark mode is enabled.
    val darkMode: Boolean = false,

    // Controls whether sound effects are enabled.
    val soundEffects: Boolean = true,

    // Stores the language the user is currently learning.
    val learningLanguage: String = "isiXhosa",

    // Indicates whether the user has enabled offline lessons.
    val offlineLessons: Boolean = false
)

/**
 * Request sent to the API when XP needs to be added to a user's account.
 */
data class AddXpRequest(
    // Amount of experience points to add.
    val xp: Int
)

/**
 * Represents the XP information returned for a user.
 */
data class UserXp(
    // ID of the user associated with the XP information.
    val userId: Int,

    // User's total accumulated experience points.
    val totalXp: Int
)