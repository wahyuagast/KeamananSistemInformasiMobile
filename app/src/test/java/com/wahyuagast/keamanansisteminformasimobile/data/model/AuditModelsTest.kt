package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class AuditModelsTest {
    @Test
    fun auditDto_serializesAndDeserializes() {
        val dto = AuditLogDto(
            id = "123",
            timestamp = "2025-01-01T00:00:00Z",
            actorId = "u1",
            eventType = "TEST_EVENT",
            resourceId = "r1",
            details = mapOf("k" to "v"),
            severity = "INFO"
        )
        val json = Json.encodeToString(AuditLogDto.serializer(), dto)
        val parsed = Json.decodeFromString(AuditLogDto.serializer(), json)
        assertEquals(dto.id, parsed.id)
        assertEquals(dto.eventType, parsed.eventType)
        assertEquals(dto.details["k"], parsed.details["k"])
    }
}

