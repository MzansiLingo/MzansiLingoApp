package com.example.mzantsilingo.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.R
import com.example.mzantsilingo.data.local.SettingsPreferences
import com.example.mzantsilingo.data.model.UserSettings
import com.example.mzantsilingo.data.repository.SettingsRepository
import com.example.mzantsilingo.databinding.ActivitySettingsBinding
import com.example.mzantsilingo.ui.auth.LoginActivity
import kotlinx.coroutines.launch
import com.example.mzantsilingo.data.local.UserSession

class SettingsActivity : AppCompatActivity() {

    // Gives us access to all the views on the settings screen.
    private lateinit var binding: ActivitySettingsBinding

    // Handles saving and loading the user's settings on the device.
    private lateinit var prefs: SettingsPreferences

    // Used to send the user's settings to the backend.
    private val settingsRepository = SettingsRepository()

    // Languages that the user can choose from in the app.
    private val languages = listOf("isiXhosa", "Afrikaans")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the settings screen using View Binding.
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the saved settings from the device.
        prefs = SettingsPreferences(this)
        loadCurrentSettings()

        // Save the notification setting whenever the switch is changed.
        binding.switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.pushNotifications = isChecked
            syncToBackend()
        }

        // Save the daily reminder setting when it is changed.
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

            // Recreate the activity so the new theme can be applied.
            recreate()
        }

        // Save the sound effects setting when it is changed.
        binding.switchSoundEffects.setOnCheckedChangeListener { _, isChecked ->
            prefs.soundEffects = isChecked
            syncToBackend()
        }

        // Save the offline lessons setting when it is changed.
        binding.switchOfflineLessons.setOnCheckedChangeListener { _, isChecked ->
            prefs.offlineLessons = isChecked
            syncToBackend()
        }

        // Open the language picker when the language row is selected.
        binding.rowLearningLanguage.setOnClickListener {
            showLanguagePicker()
        }

        // Log the user out when the logout button is pressed.
        binding.btnLogOut.setOnClickListener {
            logOut()
        }
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
    }

    // Shows a small list where the user can choose their learning language.
    private fun showLanguagePicker() {

        // Find which language is currently selected.
        val currentIndex = languages.indexOf(prefs.learningLanguage).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)

            // Display the available languages as selectable options.
            .setSingleChoiceItems(languages.toTypedArray(), currentIndex) { dialog, which ->

                // Get the language selected by the user.
                val selected = languages[which]

                // Save the selected language locally.
                prefs.learningLanguage = selected

                // Update the language shown on the settings screen.
                binding.tvLearningLanguageValue.text =
                    getString(R.string.language_value_format, selected)

                // Send the updated settings to the backend.
                syncToBackend()

                // Close the language picker.
                dialog.dismiss()
            }

            // Allow the user to close the dialog without making a change.
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