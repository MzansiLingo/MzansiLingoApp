package com.example.mzantsilingo.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Keeps the app settings saved on the device so they are still there
 * when the user closes and opens the app again.
 */
class SettingsPreferences(context: Context) {

    // Get the SharedPreferences file where the settings will be stored.
    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            "mzantsilingo_settings",
            Context.MODE_PRIVATE
        )

    companion object {

        // Keys used to save and retrieve each setting.
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PUSH_NOTIFICATIONS = "push_notifications"
        private const val KEY_DAILY_REMINDER = "daily_reminder"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_SOUND_EFFECTS = "sound_effects"
        private const val KEY_LEARNING_LANGUAGE = "learning_language"
        private const val KEY_OFFLINE_LESSONS = "offline_lessons"
    }

    // Stores the ID of the currently logged-in user.
    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    // Saves whether push notifications are turned on or off.
    var pushNotifications: Boolean
        get() = prefs.getBoolean(KEY_PUSH_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_PUSH_NOTIFICATIONS, value).apply()

    // Saves whether daily learning reminders are enabled.
    var dailyReminder: Boolean
        get() = prefs.getBoolean(KEY_DAILY_REMINDER, true)
        set(value) = prefs.edit().putBoolean(KEY_DAILY_REMINDER, value).apply()

    // Saves the user's dark mode preference.
    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    // Saves whether sound effects are enabled.
    var soundEffects: Boolean
        get() = prefs.getBoolean(KEY_SOUND_EFFECTS, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_EFFECTS, value).apply()

    // Saves the language the user is currently learning.
    var learningLanguage: String
        get() = prefs.getString(KEY_LEARNING_LANGUAGE, "isiXhosa") ?: "isiXhosa"
        set(value) = prefs.edit().putString(KEY_LEARNING_LANGUAGE, value).apply()

    // Saves whether offline lessons have been enabled.
    var offlineLessons: Boolean
        get() = prefs.getBoolean(KEY_OFFLINE_LESSONS, false)
        set(value) = prefs.edit().putBoolean(KEY_OFFLINE_LESSONS, value).apply()

    // Applies the saved dark mode setting to the app.
    fun applyDarkMode() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}