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

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SettingsPreferences
    private val settingsRepository = SettingsRepository()

    private val languages = listOf("isiXhosa", "Afrikaans")

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

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.darkMode = isChecked
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            syncToBackend()
            // Theme change requires the activity to redraw
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

        binding.rowLearningLanguage.setOnClickListener {
            showLanguagePicker()
        }

        binding.btnLogOut.setOnClickListener {
            logOut()
        }
    }

    private fun loadCurrentSettings() {
        binding.switchPushNotifications.isChecked = prefs.pushNotifications
        binding.switchDailyReminder.isChecked = prefs.dailyReminder
        binding.switchDarkMode.isChecked = prefs.darkMode
        binding.switchSoundEffects.isChecked = prefs.soundEffects
        binding.switchOfflineLessons.isChecked = prefs.offlineLessons
        binding.tvLearningLanguageValue.text = getString(R.string.language_value_format, prefs.learningLanguage)
    }

    private fun showLanguagePicker() {
        val currentIndex = languages.indexOf(prefs.learningLanguage).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(languages.toTypedArray(), currentIndex) { dialog, which ->
                val selected = languages[which]
                prefs.learningLanguage = selected
                binding.tvLearningLanguageValue.text = getString(R.string.language_value_format, selected)
                syncToBackend()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun syncToBackend() {
        val userId = prefs.userId ?: return

        val settings = UserSettings(
            pushNotifications = prefs.pushNotifications,
            dailyReminder = prefs.dailyReminder,
            darkMode = prefs.darkMode,
            soundEffects = prefs.soundEffects,
            learningLanguage = prefs.learningLanguage,
            offlineLessons = prefs.offlineLessons
        )

        lifecycleScope.launch {
            settingsRepository.updateSettings(userId, settings)
        }
    }

    private fun logOut() {
        prefs.userId = null
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
