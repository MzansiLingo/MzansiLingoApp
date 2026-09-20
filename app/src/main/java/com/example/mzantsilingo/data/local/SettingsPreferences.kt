package com.example.mzantsilingo.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Wraps SharedPreferences for settings that need to persist across app
 * restarts and apply immediately (dark mode, learning language).
 */
class SettingsPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("mzantsilingo_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PUSH_NOTIFICATIONS = "push_notifications"
        private const val KEY_DAILY_REMINDER = "daily_reminder"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_SOUND_EFFECTS = "sound_effects"
        private const val KEY_LEARNING_LANGUAGE = "learning_language"
        private const val KEY_OFFLINE_LESSONS = "offline_lessons"
    }

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var pushNotifications: Boolean
        get() = prefs.getBoolean(KEY_PUSH_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_PUSH_NOTIFICATIONS, value).apply()

    var dailyReminder: Boolean
        get() = prefs.getBoolean(KEY_DAILY_REMINDER, true)
        set(value) = prefs.edit().putBoolean(KEY_DAILY_REMINDER, value).apply()

    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var soundEffects: Boolean
        get() = prefs.getBoolean(KEY_SOUND_EFFECTS, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_EFFECTS, value).apply()

    var learningLanguage: String
        get() = prefs.getString(KEY_LEARNING_LANGUAGE, "isiXhosa") ?: "isiXhosa"
        set(value) = prefs.edit().putString(KEY_LEARNING_LANGUAGE, value).apply()

    var offlineLessons: Boolean
        get() = prefs.getBoolean(KEY_OFFLINE_LESSONS, false)
        set(value) = prefs.edit().putBoolean(KEY_OFFLINE_LESSONS, value).apply()

    /** Applies the saved dark mode preference to the whole app. Call from Application.onCreate too. */
    fun applyDarkMode() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}