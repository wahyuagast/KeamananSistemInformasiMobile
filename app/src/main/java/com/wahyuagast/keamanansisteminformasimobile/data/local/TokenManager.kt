package com.wahyuagast.keamanansisteminformasimobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

// usage of datastore is risky: rooted device can compromise
// This class is now secure for storing sensitive data.
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
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(ACCESS_TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().clear().apply()
    }
}
    