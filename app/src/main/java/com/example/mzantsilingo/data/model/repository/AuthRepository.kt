package com.example.mzantsilingo.data.model.repository

import com.example.mzantsilingo.data.model.LoginRequest
import com.example.mzantsilingo.data.model.RegisterRequest
import com.example.mzantsilingo.data.model.User
import com.example.mzantsilingo.data.model.api.RetrofitClient

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun register(
        fullName: String,
        email: String,
        username: String,
        rawPassword: String
    ): Result<User> {
        return try {

            // Send registration details to the API.
            // The server is responsible for securely hashing the password.
            val request = RegisterRequest(
                fullName = fullName,
                email = email,
                username = username,
                password = rawPassword
            )

            val response = api.register(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Registration failed: ${response.code()}")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        rawPassword: String
    ): Result<User> {
        return try {

            // Send the password to the API.
            // The server verifies it against the stored password hash.
            val response = api.login(
                LoginRequest(
                    email = email,
                    password = rawPassword
                )
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Login failed: ${response.code()}")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}