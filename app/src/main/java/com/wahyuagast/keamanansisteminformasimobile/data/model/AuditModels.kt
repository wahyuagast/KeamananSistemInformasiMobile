package com.wahyuagast.keamanansisteminformasimobile.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val actorId: String?,
    val eventType: String,
    val resourceId: String?,
    val details: String, // JSON string
    val severity: String = "INFO",
    val attemptCount: Int = 0
)

@Serializable
data class AuditLogDto(
    val id: String,
    val timestamp: String,
    val actorId: String? = null,
    val eventType: String,
    val resourceId: String? = null,
    val details: Map<String, String> = emptyMap(),
    val severity: String = "INFO"
)

@Serializable
data class AuditBatchRequest(
    val logs: List<AuditLogDto>
)

