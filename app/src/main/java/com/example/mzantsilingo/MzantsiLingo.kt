package com.example.mzantsilingo

import android.app.Application
import com.example.mzantsilingo.data.local.SettingsPreferences
import com.example.mzantsilingo.data.sync.SyncScheduler

class MzantsiLingo : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply the user's saved dark/light mode preference before any Activity launches
        SettingsPreferences(this).applyDarkMode()

        // Upload any XP that was earned offline as soon as there is a connection.
        SyncScheduler.schedule(this)
    }
}