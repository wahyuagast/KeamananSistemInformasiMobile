package com.wahyuagast.keamanansisteminformasimobile.data.local.audit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditLogEntity

@Database(entities = [AuditLogEntity::class], version = 1, exportSchema = false)
abstract class AuditDatabase : RoomDatabase() {
    abstract fun auditDao(): AuditDao

    companion object {
        private const val DB_NAME = "audit_db"
        @Volatile private var instance: AuditDatabase? = null

        fun getInstance(context: Context): AuditDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AuditDatabase::class.java, DB_NAME).build().also { instance = it }
        }
    }
}
