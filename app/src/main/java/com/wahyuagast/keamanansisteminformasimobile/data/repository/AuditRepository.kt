package com.wahyuagast.keamanansisteminformasimobile.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.wahyuagast.keamanansisteminformasimobile.data.local.audit.AuditDatabase
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditLogDto
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditLogEntity
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog
import java.util.UUID
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuditRepository(private val context: Context) {
    private val db = AuditDatabase.getInstance(context)
    private val dao = db.auditDao()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun enqueueEvent(actorId: String?, eventType: String, resourceId: String?, details: Map<String, String>, severity: String = "INFO") {
        val id = UUID.randomUUID().toString()
        val ts = java.time.Instant.now().toString()
        val detailsJson = Json.encodeToString(details)
        val entity = AuditLogEntity(id = id, timestamp = ts, actorId = actorId, eventType = eventType, resourceId = resourceId, details = detailsJson, severity = severity)
        dao.insert(entity)
        AppLog.d("AuditRepository", "Enqueued audit event: $eventType id=$id")
    }

    suspend fun fetchPending(limit: Int = 50): List<AuditLogEntity> = dao.getPending(limit)

    suspend fun removeSent(ids: List<String>) { if (ids.isNotEmpty()) dao.deleteByIds(ids) }

    suspend fun markAttempts(ids: List<String>) { if (ids.isNotEmpty()) dao.incrementAttemptCount(ids) }
}

