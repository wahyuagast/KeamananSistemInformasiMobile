@file:Suppress("DEPRECATION")

package com.wahyuagast.keamanansisteminformasimobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog

// usage of datastore is risky: rooted device can compromise

class TokenManager(context: Context) {

    private val prefs: SharedPreferences

    companion object {
        private const val FILE_NAME = "secure_auth_prefs" // File will be encrypted
        private const val ACCESS_TOKEN_KEY = "access_token"
    }

    init {
        // 1. Create a master key stored securely in the Android Keystore.
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // 2. Initialize EncryptedSharedPreferences using the master key.
        prefs = EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Switched from suspend fun to regular fun, as SharedPreferences is synchronous.
    fun saveToken(token: String) {
        // Do NOT log the token value even partially; this may leak secrets in logs.
        // Keep only a generic message indicating token was saved.
        AppLog.d("TokenManager", "Access token saved to EncryptedSharedPreferences")
        prefs.edit { putString(ACCESS_TOKEN_KEY, token) }
    }

    fun getToken(): String? {
        val token = prefs.getString(ACCESS_TOKEN_KEY, null)
        if (token == null) {
            AppLog.w("TokenManager", "getToken: null")
        }
        return token
    }

    fun clearToken() {
        prefs.edit { clear() }
    }
}
