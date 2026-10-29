
package com.example.mzantsilingo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mzantsilingo.data.model.Lesson

@Entity(tableName = "cached_lessons")
data class CachedLesson(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val language: String,
    val wordCount: Int,
    val isCompleted: Boolean,
    val isLocked: Boolean
) {
    fun toLesson(): Lesson {
        return Lesson(
            id = id,
            title = title,
            description = description,
            language = language,
            wordCount = wordCount,
            isCompleted = isCompleted,
            isLocked = isLocked
        )
    }

    companion object {
        fun fromLesson(lesson: Lesson): CachedLesson {
            return CachedLesson(
                id = lesson.id,
                title = lesson.title,
                description = lesson.description,
                language = lesson.language,
                wordCount = lesson.wordCount,
                isCompleted = lesson.isCompleted,
                isLocked = lesson.isLocked
            )
        }
    }
}
