package com.wahyuagast.keamanansisteminformasimobile.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditBatchRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditLogDto
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuditRepository
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog

class AuditUploadWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    private val repo = AuditRepository(appContext)
    private val api = RetrofitClient.apiService

    override suspend fun doWork(): Result {
        return try {
            val pending = repo.fetchPending(50)
            if (pending.isEmpty()) return Result.success()
            val dtos = pending.map { AuditLogDto(id = it.id, timestamp = it.timestamp, actorId = it.actorId, eventType = it.eventType, resourceId = it.resourceId, details = mapOf("raw" to it.details), severity = it.severity) }
            val batch = AuditBatchRequest(logs = dtos)
            val resp = api.postAuditLogs(batch)
            if (resp.isSuccessful) {
                val ids = pending.map { it.id }
                repo.removeSent(ids)
                AppLog.d("AuditUploadWorker", "Uploaded ${ids.size} audit logs")
                Result.success()
            } else {
                // Mark attempts and retry later with backoff
                repo.markAttempts(pending.map { it.id })
                AppLog.e("AuditUploadWorker", "Upload failed: ${resp.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            AppLog.e("AuditUploadWorker", "Exception uploading audit logs: ${e.message}")
            Result.retry()
        }
    }
}

