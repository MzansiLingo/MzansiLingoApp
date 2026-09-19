package com.example.mzantsilingo.data.repository

import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.data.model.Achievement

class RewardsRepository {

    private val api = RetrofitClient.apiService

    suspend fun getAchievements(userId: String): Result<List<Achievement>> {
        return try {
            val response = api.getAchievements(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load achievements: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
