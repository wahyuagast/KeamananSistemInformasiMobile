package com.wahyuagast.keamanansisteminformasimobile.data.local.audit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditLogEntity

@Dao
interface AuditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AuditLogEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getPending(limit: Int): List<AuditLogEntity>

    @Query("DELETE FROM audit_logs WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("UPDATE audit_logs SET attemptCount = attemptCount + 1 WHERE id IN (:ids)")
    suspend fun incrementAttemptCount(ids: List<String>)
}

