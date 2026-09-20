package com.example.mzantsilingo.data.local

import android.content.Context

object UserSession {

    private const val PREF_NAME = "MzansiLingoSession"
    private const val KEY_USER_ID = "user_id"

    fun saveUserId(context: Context, userId: Int) {

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserId(context: Context): Int? {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        if (!preferences.contains(KEY_USER_ID)) {
            return null
        }

        return preferences.getInt(KEY_USER_ID, -1)
    }

    fun clearSession(context: Context) {

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .clear()
            .apply()
    }
}