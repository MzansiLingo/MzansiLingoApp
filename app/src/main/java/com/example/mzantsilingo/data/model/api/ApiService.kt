package com.example.mzantsilingo.data.model.api

import com.example.mzantsilingo.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("api/lessons")
    suspend fun getLessons(@Query("language") language: String): Response<List<Lesson>>

    @GET("api/lessons/{lessonId}/words")
    suspend fun getWords(@Path("lessonId") lessonId: Int): Response<List<Word>>

    @GET("api/lessons/{lessonId}/quiz")
    suspend fun getQuiz(@Path("lessonId") lessonId: String): Response<List<QuizQuestion>>

    @GET("api/users/{userId}/achievements")
    suspend fun getAchievements(@Path("userId") userId: String): Response<List<Achievement>>
    @POST("api/users/{userId}/xp")
    suspend fun addXp(
        @Path("userId") userId: Int,
        @Body request: AddXpRequest
    ): Response<UserXp>

    @GET("api/users/{userId}/xp")
    suspend fun getXp(
        @Path("userId") userId: Int
    ): Response<UserXp>
    @GET("api/users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int
    ): Response<User>
    @PUT("api/users/{userId}/settings")
    suspend fun updateSettings(
        @Path("userId") userId: String,
        @Body settings: UserSettings
    ): Response<UserSettings>
}