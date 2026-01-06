package com.wahyuagast.keamanansisteminformasimobile.data.repository

import android.content.Context
import com.wahyuagast.keamanansisteminformasimobile.data.local.audit.AuditDatabase
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditLogEntity
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class AuditRepository(context: Context) {
    private val db = AuditDatabase.getInstance(context)
    private val dao = db.auditDao()

    suspend fun enqueueEvent(
        actorId: String?,
        eventType: String,
        resourceId: String?,
        details: Map<String, String>,
        severity: String = "INFO"
    ) {
        val id = UUID.randomUUID().toString()
        // Use a portable ISO-8601-like timestamp compatible with older Android API levels
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val ts = sdf.format(Date())
        val detailsJson = Json.encodeToString(details)
        val entity = AuditLogEntity(
            id = id,
            timestamp = ts,
            actorId = actorId,
            eventType = eventType,
            resourceId = resourceId,
            details = detailsJson,
            severity = severity
        )
        withContext(Dispatchers.IO) {
            dao.insert(entity)
        }
        AppLog.d("AuditRepository", "Enqueued audit event: $eventType id=$id")
    }

    suspend fun fetchPending(limit: Int = 50): List<AuditLogEntity> =
        withContext(Dispatchers.IO) { dao.getPending(limit) }

    suspend fun removeSent(ids: List<String>) {
        if (ids.isNotEmpty()) withContext(Dispatchers.IO) { dao.deleteByIds(ids) }
    }

    suspend fun markAttempts(ids: List<String>) {
        if (ids.isNotEmpty()) withContext(Dispatchers.IO) { dao.incrementAttemptCount(ids) }
    }
}
