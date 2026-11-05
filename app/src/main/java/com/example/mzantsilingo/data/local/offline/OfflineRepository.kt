package com.example.mzantsilingo.data.repository

import android.content.Context
import com.example.mzantsilingo.data.local.offline.OfflineDatabase
import com.example.mzantsilingo.data.local.offline.LessonEntity
import com.example.mzantsilingo.data.local.offline.PendingXpEntity
import com.example.mzantsilingo.data.local.offline.WordEntity
import com.example.mzantsilingo.data.model.AddXpRequest
import com.example.mzantsilingo.data.model.Lesson
import com.example.mzantsilingo.data.model.Word
import com.example.mzantsilingo.data.model.api.ApiService
import com.example.mzantsilingo.data.model.api.RetrofitClient
import kotlinx.coroutines.CancellationException
import java.io.IOException
import com.example.mzantsilingo.data.local.offline.CompletedLessonEntity

/** Result of loading lessons or words: either the data, or a failure. */
sealed class LoadResult<out T> {
    /** [fromCache] is true when the data came from the device because the API could not be reached. */
    data class Loaded<T>(val data: T, val fromCache: Boolean) : LoadResult<T>()
    object Failed : LoadResult<Nothing>()
}

/** What happened when we tried to award XP. */
enum class XpResult { SYNCED, QUEUED, FAILED }

/**
 * Offline feature: lessons and words.
 *
 * - Online: data comes from the REST API and is saved to Room (if Offline Lessons is on).
 * - Offline: data is read from Room.
 * - XP earned offline is queued in Room and uploaded later by SyncXpWorker.
 */
class OfflineRepository(
    context: Context,
    private val api: ApiService = RetrofitClient.apiService
) {

    private val dao = OfflineDatabase.getInstance(context).offlineDao()

    // ---------------- Lessons ----------------

    suspend fun loadLessons(language: String, cacheEnabled: Boolean): LoadResult<List<Lesson>> {
        try {
            val response = api.getLessons(language)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                if (cacheEnabled) {
                    dao.replaceLessons(language, body.map {
                        LessonEntity(it.id, it.title, it.description, language)
                    })
                }
                return LoadResult.Loaded(body, fromCache = false)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // No connection or server problem: fall through and use the saved copy.
        }

        val saved = dao.getLessons(language)
        return if (saved.isNotEmpty()) {
            LoadResult.Loaded(
                saved.map { Lesson(id = it.id, title = it.title, description = it.description, language = it.language) },
                fromCache = true
            )
        } else {
            LoadResult.Failed
        }
    }

    // ---------------- Words ----------------

    suspend fun loadWords(lessonId: Int, cacheEnabled: Boolean): LoadResult<List<Word>> {
        try {
            val response = api.getWords(lessonId)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                if (cacheEnabled) {
                    dao.replaceWords(lessonId, body.map {
                        WordEntity(it.id, lessonId, it.xhosaWord, it.englishMeaning)
                    })
                }
                return LoadResult.Loaded(body, fromCache = false)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Fall through to the saved copy.
        }

        val saved = dao.getWords(lessonId)
        return if (saved.isNotEmpty()) {
            LoadResult.Loaded(
                saved.map { Word(it.id, it.lessonId, it.xhosaWord, it.englishMeaning) },
                fromCache = true
            )
        } else {
            LoadResult.Failed
        }
    }

    /** Saves the words of every given lesson so the whole lesson list works offline. */
    suspend fun prefetchWords(lessons: List<Lesson>) {
        for (lesson in lessons) {
            try {
                val response = api.getWords(lesson.id)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    dao.replaceWords(lesson.id, body.map {
                        WordEntity(it.id, lesson.id, it.xhosaWord, it.englishMeaning)
                    })
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return // probably offline, stop trying
            }
        }
    }

    /** Downloads all lessons and words for a language. Returns the number of lessons, or null if it failed. */
    suspend fun downloadAll(language: String): Int? {
        return try {
            val response = api.getLessons(language)
            val lessons = response.body()
            if (!response.isSuccessful || lessons == null) return null
            dao.replaceLessons(language, lessons.map {
                LessonEntity(it.id, it.title, it.description, language)
            })
            prefetchWords(lessons)
            lessons.size
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    /** Removes the saved lessons and words (pending XP is kept so nothing is lost). */
    suspend fun clearCache() {
        dao.clearLessons()
        dao.clearWords()
    }

    // ---------------- XP with sync ----------------

    /** Tries to save XP on the server. With no connection it is queued on the device instead. */
    suspend fun addXp(userId: Int, xp: Int): XpResult {
        return try {
            val response = api.addXp(userId, AddXpRequest(xp))
            when {
                response.isSuccessful -> XpResult.SYNCED
                // The server is having a problem (for example it is waking up): keep the XP and try again later.
                response.code() in 500..599 -> {
                    dao.insertPendingXp(PendingXpEntity(userId = userId, xp = xp))
                    XpResult.QUEUED
                }
                else -> XpResult.FAILED
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            dao.insertPendingXp(PendingXpEntity(userId = userId, xp = xp))
            XpResult.QUEUED
        } catch (e: Exception) {
            XpResult.FAILED
        }
    }

    /** Uploads queued XP. Returns how many items were uploaded. */
    suspend fun syncPendingXp(): Int {
        var synced = 0
        for (item in dao.getPendingXp()) {
            try {
                val response = api.addXp(item.userId, AddXpRequest(item.xp))
                when {
                    response.isSuccessful -> {
                        dao.deletePendingXp(item.id)
                        synced++
                    }
                    // The server rejected it for good (for example the user no longer exists).
                    response.code() in 400..499 -> dao.deletePendingXp(item.id)
                    // Server problem: keep it and try again later.
                    else -> return synced
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return synced // still offline
            }
        }
        return synced
    }

    suspend fun pendingCount(): Int = dao.pendingXpCount()

    // ---------------- Completed lessons ----------------

    /**
     * Saves a completed lesson locally.
     * The same lesson cannot be counted twice for the same user.
     */
    suspend fun recordLessonCompleted(
        userId: Int,
        lessonId: Int
    ) {
        if (userId <= 0 || lessonId <= 0) {
            return
        }

        dao.insertCompletedLesson(
            CompletedLessonEntity(
                userId = userId,
                lessonId = lessonId
            )
        )
    }

    /**
     * Returns all lessons completed by the user.
     */
    suspend fun getCompletedLessons(
        userId: Int
    ): List<CompletedLessonEntity> {
        return dao.getCompletedLessons(userId)
    }

    /**
     * Returns the number of lessons completed by the user.
     */
    suspend fun getCompletedLessonCount(
        userId: Int
    ): Int {
        return dao.getCompletedLessonCount(userId)
    }

    /**
     * Returns completion records that still need to be uploaded.
     */
    suspend fun getUnsyncedCompletedLessons():
            List<CompletedLessonEntity> {
        return dao.getUnsyncedCompletedLessons()
    }

    /**
     * Marks a completion as synchronized with the backend.
     */
    suspend fun markCompletedLessonSynced(
        userId: Int,
        lessonId: Int
    ) {
        dao.markCompletedLessonSynced(userId, lessonId)
    }
}