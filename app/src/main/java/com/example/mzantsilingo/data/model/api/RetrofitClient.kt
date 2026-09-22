package com.example.mzantsilingo.data.model.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Creates and configures the Retrofit client used by the Android application
 * to communicate with the MzansiLingo REST API.
 */
object RetrofitClient {

    // Base URL used to connect the Android emulator to the local ASP.NET Core API.
    // 10.0.2.2 refers to the host computer when using the Android Emulator.
    private const val BASE_URL = "http://10.0.2.2:5029/"

    /**
     * Logs HTTP requests and responses while developing and debugging the app.
     *
     * BODY logging displays request and response details, which helps identify
     * problems when communicating with the REST API.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Adds the logging interceptor to the HTTP client used by Retrofit.
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Creates the Retrofit API service only when it is first accessed.
     *
     * Gson is used to convert JSON responses from the REST API into Kotlin
     * data model objects.
     */
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}