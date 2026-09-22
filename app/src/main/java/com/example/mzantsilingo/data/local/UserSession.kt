package com.example.mzantsilingo.data.local

import android.content.Context

/**
 * Manages the currently logged-in user's session using SharedPreferences.
 *
 * The user's ID is stored locally so that other parts of the application
 * can identify the logged-in user without requiring the user to log in again.
 */
object UserSession {

    // Name of the SharedPreferences file used to store the user's session.
    private const val PREF_NAME = "MzansiLingoSession"

    // Key used to store and retrieve the logged-in user's ID.
    private const val KEY_USER_ID = "user_id"

    /**
     * Saves the logged-in user's ID to local storage.
     *
     * @param context Application or Activity context used to access SharedPreferences.
     * @param userId The unique ID of the authenticated user.
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
     * Retrieves the logged-in user's ID from local storage.
     *
     * @param context Application or Activity context used to access SharedPreferences.
     * @return The stored user ID, or null if no user session exists.
     */
    fun getUserId(context: Context): Int? {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        // Check whether a user ID has previously been stored.
        if (!preferences.contains(KEY_USER_ID)) {
            return null
        }

        return preferences.getInt(KEY_USER_ID, -1)
    }

    /**
     * Clears all stored session information.
     *
     * This is used when the user logs out of the application.
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