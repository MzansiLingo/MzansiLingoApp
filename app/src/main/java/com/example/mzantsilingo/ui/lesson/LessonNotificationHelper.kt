package com.example.mzantsilingo.ui.lesson

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object LessonNotificationHelper {

    const val CHANNEL_ID = "lesson_reminders"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Lesson reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to continue unfinished lessons"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showLessonReminder(
        context: Context,
        lessonId: Int,
        lessonTitle: String,
        wordIndex: Int
    ) {
        val intent = Intent(context, LessonActivity::class.java).apply {
            putExtra("LESSON_ID", lessonId)
            putExtra("LESSON_TITLE", lessonTitle)
            putExtra("RESUME_WORD_INDEX", wordIndex)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            lessonId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.example.mzantsilingo.R.drawable.ic_launcher_foreground)
            .setContentTitle("Continue your lesson !")
            .setContentText("You still have words to learn in $lessonTitle.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID,
                notification
            )
        } catch (e: SecurityException) {
            // Notifications are disabled or permission was not granted.
        }
    }
}