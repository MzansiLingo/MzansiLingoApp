package com.example.mzantsilingo.data.local.offline

import androidx.room.Entity

@Entity(
    tableName = "completed_lessons",
    primaryKeys = ["userId", "lessonId"]
)
data class CompletedLessonEntity(
    val userId: Int,
    val lessonId: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)