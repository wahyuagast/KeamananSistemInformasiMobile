package com.wahyuagast.keamanansisteminformasimobile.data.local.audit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditLogEntity

@Dao
interface AuditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(log: AuditLogEntity): Long

    @Query("SELECT * FROM audit_logs ORDER BY timestamp ASC LIMIT :limit")
    fun getPending(limit: Int): List<AuditLogEntity>

    @Query("DELETE FROM audit_logs WHERE id IN (:ids)")
    fun deleteByIds(ids: List<String>): Int

    @Query("UPDATE audit_logs SET attempt_count = attempt_count + 1 WHERE id IN (:ids)")
    fun incrementAttemptCount(ids: List<String>): Int
}
