package com.example.mzantsilingo.data.local.offline

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A lesson saved on the device so it can be used without internet. */
@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val language: String
)

/** A vocabulary word saved on the device. */
@Entity(tableName = "words", indices = [Index("lessonId")])
data class WordEntity(
    @PrimaryKey val id: Int,
    val lessonId: Int,
    val xhosaWord: String,
    val englishMeaning: String
)

/**
 * XP earned while offline. It waits here until the connection returns,
 * then it is uploaded to the API (see SyncXpWorker).
 */
@Entity(tableName = "pending_xp")
data class PendingXpEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val xp: Int,
    val createdAt: Long = System.currentTimeMillis()
)