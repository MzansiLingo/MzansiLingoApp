package com.example.mzantsilingo.data.repository

import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.data.model.Lesson
import com.example.mzantsilingo.data.model.QuizQuestion
import com.example.mzantsilingo.data.model.Word

class LessonRepository {

    private val api = RetrofitClient.apiService

    suspend fun getLessons(language: String): Result<List<Lesson>> {
        return try {
            val response = api.getLessons(language)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load lessons: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWords(lessonId: String): Result<List<Word>> {
        return try {
            val response = api.getWords(lessonId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load words: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuiz(lessonId: String): Result<List<QuizQuestion>> {
        return try {
            val response = api.getQuiz(lessonId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load quiz: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
