package com.example.mzantsilingo.data.repository

import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.data.model.ActivityItem
import com.example.mzantsilingo.data.model.User

class ProfileRepository {

    private val api = RetrofitClient.apiService

    suspend fun getUser(userId: String): Result<User> {
        return try {
            val response = api.getUser(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentActivity(userId: String): Result<List<ActivityItem>> {
        return try {
            val response = api.getRecentActivity(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load activity: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}