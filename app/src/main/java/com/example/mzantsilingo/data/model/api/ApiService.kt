package com.example.mzantsilingo.data.model.api

import com.example.mzantsilingo.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Defines the REST API endpoints used by the MzansiLingo Android application.
 *
 * Retrofit uses these method definitions to send HTTP requests to the
 * ASP.NET Core backend API.
 */
interface ApiService {

    /**
     * Registers a new user account.
     *
     * Sends the user's registration details to the authentication API.
     */
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<User>

    /**
     * Authenticates an existing user.
     *
     * Sends the user's login details and receives the login response
     * from the backend.
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    /**
     * Retrieves lessons for the selected language.
     *
     * The language is sent as a query parameter, for example:
     * api/lessons?language=isiXhosa
     */
    @GET("api/lessons")
    suspend fun getLessons(
        @Query("language") language: String
    ): Response<List<Lesson>>

    /**
     * Retrieves the vocabulary words belonging to a specific lesson.
     *
     * The lesson ID is included as part of the API URL.
     */
    @GET("api/lessons/{lessonId}/words")
    suspend fun getWords(
        @Path("lessonId") lessonId: Int
    ): Response<List<Word>>

    /**
     * Retrieves quiz questions for a specific lesson.
     */
    @GET("api/lessons/{lessonId}/quiz")
    suspend fun getQuiz(
        @Path("lessonId") lessonId: String
    ): Response<List<QuizQuestion>>

    /**
     * Retrieves achievements earned by a specific user.
     */
    @GET("api/users/{userId}/achievements")
    suspend fun getAchievements(
        @Path("userId") userId: String
    ): Response<List<Achievement>>

    /**
     * Adds XP to a user's account after completing a learning activity.
     */
    @POST("api/users/{userId}/xp")
    suspend fun addXp(
        @Path("userId") userId: Int,
        @Body request: AddXpRequest
    ): Response<UserXp>

    /**
     * Retrieves the current XP information for a user.
     */
    @GET("api/users/{userId}/xp")
    suspend fun getXp(
        @Path("userId") userId: Int
    ): Response<UserXp>

    /**
     * Retrieves the profile information for a specific user.
     */
    @GET("api/users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int
    ): Response<User>

    /**
     * Updates the user's application settings through the REST API.
     */
    @PUT("api/users/{userId}/settings")
    suspend fun updateSettings(
        @Path("userId") userId: String,
        @Body settings: UserSettings
    ): Response<UserSettings>
}