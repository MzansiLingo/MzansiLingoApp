
package com.example.mzantsilingo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LessonDao {

    @Query(
        "SELECT * FROM cached_lessons " +
                "WHERE language = :language ORDER BY id ASC"
    )
    suspend fun getLessonsByLanguage(
        language: String
    ): List<CachedLesson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(
        lessons: List<CachedLesson>
    )

    @Query("DELETE FROM cached_lessons WHERE language = :language")
    suspend fun deleteLessonsByLanguage(
        language: String
    )

    @Transaction
    suspend fun replaceLessonsForLanguage(
        language: String,
        lessons: List<CachedLesson>
    ) {
        deleteLessonsByLanguage(language)
        insertLessons(lessons)
    }
}
