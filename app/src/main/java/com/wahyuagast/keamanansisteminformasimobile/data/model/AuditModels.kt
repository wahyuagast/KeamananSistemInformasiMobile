package com.wahyuagast.keamanansisteminformasimobile.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: String,

    @ColumnInfo(name = "actor_id")
    val actorId: String?,

    @ColumnInfo(name = "event_type")
    val eventType: String,

    @ColumnInfo(name = "resource_id")
    val resourceId: String?,

    @ColumnInfo(name = "details")
    val details: String, // JSON string

    @ColumnInfo(name = "severity")
    val severity: String = "INFO",

    @ColumnInfo(name = "attempt_count")
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
