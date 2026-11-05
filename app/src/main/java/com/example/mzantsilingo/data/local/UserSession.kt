package com.example.mzantsilingo.data.local

import android.content.Context

/**
 * Manages the currently logged-in user's session using SharedPreferences.
 */
object UserSession {

    private const val PREF_NAME = "MzansiLingoSession"
    private const val KEY_USER_ID = "user_id"

    /**
     * Saves the authenticated user's ID.
     */
    fun saveUserId(context: Context, userId: Int) {
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    /**
     * Retrieves the authenticated user's ID.
     * Returns null if no valid user ID is stored.
     */
    fun getUserId(context: Context): Int? {
        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        if (!preferences.contains(KEY_USER_ID)) {
            return null
        }

        val userId = preferences.getInt(KEY_USER_ID, -1)

        return userId.takeIf { it > 0 }
    }

    /**
     * Clears the user's session when they log out.
     */
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
