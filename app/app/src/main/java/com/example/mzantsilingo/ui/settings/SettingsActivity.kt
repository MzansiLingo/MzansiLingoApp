package com.example.mzantsilingo.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.R
import com.example.mzantsilingo.data.local.SettingsPreferences
import com.example.mzantsilingo.data.local.UserSession
import com.example.mzantsilingo.data.model.UserSettings
import com.example.mzantsilingo.data.repository.SettingsRepository
import com.example.mzantsilingo.databinding.ActivitySettingsBinding
import com.example.mzantsilingo.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    // Gives us access to all the views on the settings screen.
    private lateinit var binding: ActivitySettingsBinding

    // Handles saving and loading the user's settings on the device.
    private lateinit var prefs: SettingsPreferences

    // Used to send the user's settings to the backend.
    private val settingsRepository = SettingsRepository()

    // Languages that the user can LEARN.
    private val languages = listOf("isiXhosa", "Afrikaans")

    // Languages the APP can be DISPLAYED in: language tag to its own name.
    private val appLanguages = listOf(
        "en" to "English",
        "xh" to "isiXhosa",
        "af" to "Afrikaans"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SettingsPreferences(this)
        loadCurrentSettings()

        binding.switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.pushNotifications = isChecked
            syncToBackend()
        }

        binding.switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            prefs.dailyReminder = isChecked
            syncToBackend()
        }

        // Turn dark mode on or off when the switch is changed.
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.darkMode = isChecked

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            syncToBackend()
            recreate()
        }

        binding.switchSoundEffects.setOnCheckedChangeListener { _, isChecked ->
            prefs.soundEffects = isChecked
            syncToBackend()
        }

        binding.switchOfflineLessons.setOnCheckedChangeListener { _, isChecked ->
            prefs.offlineLessons = isChecked
            syncToBackend()
        }

        // Open the language pickers.
        binding.rowAppLanguage.setOnClickListener { showAppLanguagePicker() }
        binding.rowLearningLanguage.setOnClickListener { showLanguagePicker() }

        binding.btnLogOut.setOnClickListener { logOut() }
    }

    // Load the saved settings and show them on the screen.
    private fun loadCurrentSettings() {
        binding.switchPushNotifications.isChecked = prefs.pushNotifications
        binding.switchDailyReminder.isChecked = prefs.dailyReminder
        binding.switchDarkMode.isChecked = prefs.darkMode
        binding.switchSoundEffects.isChecked = prefs.soundEffects
        binding.switchOfflineLessons.isChecked = prefs.offlineLessons
        binding.tvLearningLanguageValue.text =
            getString(R.string.language_value_format, prefs.learningLanguage)
        binding.tvAppLanguageValue.text =
            getString(R.string.language_value_format, currentAppLanguageName())
    }

    // The name of the language the app is currently being displayed in.
    private fun currentAppLanguageName(): String {
        val tag = resources.configuration.locales[0].language
        return appLanguages.firstOrNull { it.first == tag }?.second ?: "English"
    }

    // Lets the user change the language of the whole app (English, isiXhosa, Afrikaans).
    private fun showAppLanguagePicker() {
        val names = appLanguages.map { it.second }.toTypedArray()
        val currentTag = resources.configuration.locales[0].language
        val currentIndex = appLanguages.indexOfFirst { it.first == currentTag }.coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_app_language)
            .setSingleChoiceItems(names, currentIndex) { dialog, which ->
                dialog.dismiss()
                // AppCompat saves this choice and recreates the screens in the new language.
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(appLanguages[which].first)
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    // Shows a small list where the user can choose their learning language.
    private fun showLanguagePicker() {
        val currentIndex = languages.indexOf(prefs.learningLanguage).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(languages.toTypedArray(), currentIndex) { dialog, which ->
                val selected = languages[which]
                prefs.learningLanguage = selected
                binding.tvLearningLanguageValue.text =
                    getString(R.string.language_value_format, selected)
                syncToBackend()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    // Sends the current settings from the device to the backend.
    private fun syncToBackend() {
        // The ID is saved in UserSession when the user logs in.
        val userId = UserSession.getUserId(this) ?: return

        val settings = UserSettings(
            pushNotifications = prefs.pushNotifications,
            dailyReminder = prefs.dailyReminder,
            darkMode = prefs.darkMode,
            soundEffects = prefs.soundEffects,
            learningLanguage = prefs.learningLanguage,
            offlineLessons = prefs.offlineLessons
        )

        lifecycleScope.launch {
            settingsRepository.updateSettings(userId.toString(), settings)
        }
    }

    // Clears the session and returns to the login screen.
    private fun logOut() {
        UserSession.clearSession(this)
        prefs.userId = null

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
