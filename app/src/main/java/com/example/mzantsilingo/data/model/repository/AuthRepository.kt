package com.example.mzantsilingo.data.model.repository

import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.data.model.LoginRequest
import com.example.mzantsilingo.data.model.User
import com.example.mzantsilingo.util.PasswordHasher

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun register(fullName: String, email: String, username: String, rawPassword: String): Result<User> {
        return try {
            val hashed = PasswordHasher.hash(rawPassword)
            val user = User(fullName = fullName, email = email, username = username, passwordHash = hashed)
            val response = api.register(user)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, rawPassword: String): Result<User> {
        return try {
            val hashed = PasswordHasher.hash(rawPassword)
            val response = api.login(LoginRequest(email, hashed))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}