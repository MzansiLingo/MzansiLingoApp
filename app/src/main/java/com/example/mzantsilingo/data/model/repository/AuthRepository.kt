package com.example.mzantsilingo.data.model.repository

import com.example.mzantsilingo.data.model.ApiError
import com.example.mzantsilingo.data.model.LoginRequest
import com.example.mzantsilingo.data.model.LoginResponse
import com.example.mzantsilingo.data.model.RegisterRequest
import com.example.mzantsilingo.data.model.User
import com.example.mzantsilingo.data.model.api.ApiService
import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.google.gson.Gson
import retrofit2.Response

/**
 * Handles registration and login through the REST API.
 * The ApiService is passed in so it can be replaced with a mock in unit tests.
 */
class AuthRepository(
    private val api: ApiService = RetrofitClient.apiService
) {

    suspend fun register(
        fullName: String,
        email: String,
        username: String,
        rawPassword: String
    ): Result<User> = call("Registration failed") {
        api.register(RegisterRequest(fullName, email, username, rawPassword))
    }

    suspend fun login(email: String, rawPassword: String): Result<User> =
        call("Login failed") { api.login(LoginRequest(email, rawPassword)) }

    // Runs an API call and turns the result into Result<User> with a readable error.
    private suspend fun call(
        fallback: String,
        request: suspend () -> Response<LoginResponse>
    ): Result<User> {
        return try {
            val response = request()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body.user)
            } else {
                Result.failure(Exception(errorMessage(response, fallback)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not reach the server. Check your internet connection and try again."))
        }
    }

    private fun errorMessage(response: Response<*>, fallback: String): String {
        return try {
            val raw = response.errorBody()?.string()
            Gson().fromJson(raw, ApiError::class.java)?.message ?: "$fallback (${response.code()})"
        } catch (e: Exception) {
            "$fallback (${response.code()})"
        }
    }
}