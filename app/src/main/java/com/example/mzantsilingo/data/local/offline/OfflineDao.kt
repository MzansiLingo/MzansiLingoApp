package com.example.mzantsilingo.data.local.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class OfflineDao {

    // ---------- Lessons ----------
    @Query("SELECT * FROM lessons WHERE language = :language ORDER BY id")
    abstract suspend fun getLessons(language: String): List<LessonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("DELETE FROM lessons WHERE language = :language")
    abstract suspend fun deleteLessons(language: String)

    // Replace the saved lessons for one language in a single step.
    @Transaction
    open suspend fun replaceLessons(language: String, lessons: List<LessonEntity>) {
        deleteLessons(language)
        insertLessons(lessons)
    }

    // ---------- Words ----------
    @Query("SELECT * FROM words WHERE lessonId = :lessonId ORDER BY id")
    abstract suspend fun getWords(lessonId: Int): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWords(words: List<WordEntity>)

    @Query("DELETE FROM words WHERE lessonId = :lessonId")
    abstract suspend fun deleteWords(lessonId: Int)

    @Transaction
    open suspend fun replaceWords(lessonId: Int, words: List<WordEntity>) {
        deleteWords(lessonId)
        insertWords(words)
    }

    @Query("DELETE FROM lessons")
    abstract suspend fun clearLessons()

    @Query("DELETE FROM words")
    abstract suspend fun clearWords()

    // ---------- XP waiting to be uploaded ----------
    @Insert
    abstract suspend fun insertPendingXp(item: PendingXpEntity)

    @Query("SELECT * FROM pending_xp ORDER BY id")
    abstract suspend fun getPendingXp(): List<PendingXpEntity>

    @Query("DELETE FROM pending_xp WHERE id = :id")
    abstract suspend fun deletePendingXp(id: Int)

    @Query("SELECT COUNT(*) FROM pending_xp")
    abstract suspend fun pendingXpCount(): Int

    // ---------- Completed lessons ----------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertCompletedLesson(
        lesson: CompletedLessonEntity
    )

    @Query("""
    SELECT * FROM completed_lessons
    WHERE userId = :userId
    ORDER BY completedAt DESC
""")
    abstract suspend fun getCompletedLessons(
        userId: Int
    ): List<CompletedLessonEntity>

    @Query("""
    SELECT COUNT(*) FROM completed_lessons
    WHERE userId = :userId
""")
    abstract suspend fun getCompletedLessonCount(
        userId: Int
    ): Int

    @Query("""
    SELECT * FROM completed_lessons
    WHERE synced = 0
    ORDER BY completedAt
""")
    abstract suspend fun getUnsyncedCompletedLessons():
            List<CompletedLessonEntity>

    @Query("""
    UPDATE completed_lessons
    SET synced = 1
    WHERE userId = :userId AND lessonId = :lessonId
""")
    abstract suspend fun markCompletedLessonSynced(
        userId: Int,
        lessonId: Int
    )
}
