package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenManager(private val context: Context) {
    companion object {
        val ACCESS = stringPreferencesKey("access_token")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs -> prefs[ACCESS] = token }
    }

    fun getTokenFlow() = context.dataStore.data.map { it[ACCESS] }

    suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[ACCESS]
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}