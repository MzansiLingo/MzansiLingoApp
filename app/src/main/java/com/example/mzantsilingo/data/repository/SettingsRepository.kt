package com.example.mzantsilingo.data.repository

import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.data.model.UserSettings

class SettingsRepository {

    private val api = RetrofitClient.apiService

    suspend fun updateSettings(userId: String, settings: UserSettings): Result<UserSettings> {
        return try {
            val response = api.updateSettings(userId, settings)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Settings update failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}