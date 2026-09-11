package com.example.mzantsilingo.data.model

data class User(
    val id: String? = null,
    val fullName: String,
    val email: String,
    val username: String,
    val passwordHash: String,
    val totalXp: Int = 0,
    val streakDays: Int = 0
)

data class LoginRequest(
    val email: String,
    val passwordHash: String
)

data class Lesson(
    val id: String,
    val title: String,
    val language: String,
    val wordCount: Int,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = true
)

data class Word(
    val id: String,
    val term: String,
    val translation: String,
    val pronunciation: String
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