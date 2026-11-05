package com.example.mzantsilingo.data.local.offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        LessonEntity::class,
        WordEntity::class,
        PendingXpEntity::class,
        CompletedLessonEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class OfflineDatabase : RoomDatabase() {

    abstract fun offlineDao(): OfflineDao

    companion object {

        @Volatile
        private var instance: OfflineDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS completed_lessons (
                        userId INTEGER NOT NULL,
                        lessonId INTEGER NOT NULL,
                        completedAt INTEGER NOT NULL,
                        synced INTEGER NOT NULL,
                        PRIMARY KEY(userId, lessonId)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): OfflineDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    OfflineDatabase::class.java,
                    "mzansilingo_offline.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}