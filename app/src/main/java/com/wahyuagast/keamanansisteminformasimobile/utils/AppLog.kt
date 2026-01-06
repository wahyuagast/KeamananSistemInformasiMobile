package com.wahyuagast.keamanansisteminformasimobile.utils

import com.wahyuagast.keamanansisteminformasimobile.BuildConfig

/**
 * Centralized logging helper that redacts common secrets and keeps logs consistent.
 * Prefer calling AppLog.d/e/w(...) instead of android.util.Log directly.
 */
object AppLog {
    private fun redact(value: String?): String =
        value?.replace(Regex("Bearer\\s+(.+)"), "Bearer [REDACTED]") ?: "(none)"

    fun d(tag: String, msg: String, token: String? = null) {
        // In release builds be conservative: still allow debug logs if needed but prefer using
        // PROGUARD/R8 to strip them. Here we still emit logs but never reveal tokens.
        val safeMsg = if (token != null) "$msg — Authorization=${redact(token)}" else msg
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, safeMsg)
        } else {
            // Optionally keep minimal logs in production
            android.util.Log.d(tag, safeMsg)
        }
    }

    fun w(tag: String, msg: String) {
        android.util.Log.w(tag, msg)
    }

    fun e(tag: String, msg: String) {
        android.util.Log.e(tag, msg)
    }
}

