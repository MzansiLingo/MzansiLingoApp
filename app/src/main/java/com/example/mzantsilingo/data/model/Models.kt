package com.example.mzantsilingo.data.model

data class User(
    val id: Int? = null,
    val fullName: String,
    val email: String,
    val username: String,
    val passwordHash: String,
    val totalXp: Int = 0,
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class Lesson(
    val id: Int,
    val title: String,
    val description: String = "",
    val language: String,
    val wordCount: Int = 0,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false
)

data class Word(
    val id: Int,
    val lessonId: Int,
    val xhosaWord: String,
    val englishMeaning: String
)

data class QuizQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

data class Achievement(
    val id: String,
    val title: String,
    val isUnlocked: Boolean
)

data class UserSettings(
    val pushNotifications: Boolean = true,
    val dailyReminder: Boolean = true,
    val darkMode: Boolean = false,
    val soundEffects: Boolean = true,
    val learningLanguage: String = "isiXhosa",
    val offlineLessons: Boolean = false
)
data class AddXpRequest(
    val xp: Int
)

data class UserXp(
    val userId: Int,
    val totalXp: Int
)