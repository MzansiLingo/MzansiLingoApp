package com.example.mzantsilingo

import android.app.Application
import com.example.mzantsilingo.data.local.SettingsPreferences

class MzantsiLingo : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply the user's saved dark/light mode preference before any Activity launches
        SettingsPreferences(this).applyDarkMode()
    }
}