package com.example.mzantsilingo.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mzantsilingo.data.repository.OfflineRepository

/** Background job that uploads XP earned offline once the phone is online again. */
class SyncXpWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = OfflineRepository(applicationContext)
        repository.syncPendingXp()
        return if (repository.pendingCount() > 0) Result.retry() else Result.success()
    }
}