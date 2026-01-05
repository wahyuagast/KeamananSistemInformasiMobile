@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.wahyuagast.keamanansisteminformasimobile.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wahyuagast.keamanansisteminformasimobile.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = BuildConfig.API_BASE_URL

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    ///Critical level = HttpLoggingInterceptor.Level.BODY will expose login info
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
    }

    private lateinit var tokenManager: com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
    private var deviceId: String? = null

    @SuppressLint("HardwareIds")
    fun initialize(context: Context) {
        // store application context to avoid leaks
        val appCtx = context.applicationContext
        tokenManager = com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager(appCtx)
        deviceId = Settings.Secure.getString(appCtx.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Add app headers (always)
                val req = chain.request().newBuilder()
                    .header("User-Agent", "SIMOPKL-Android/1.0")
                    .header("X-App-Platform", "android")
                    .header("X-App-Version", "1.0.0")
                    .header("X-Device-Id", deviceId ?: "unknown")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor { chain ->
                val tm = tokenManager
                AuthInterceptor(tm).intercept(chain)
            }
            // debug interceptor: logs URL and masked Authorization header (Moved to end to capture headers added by previous interceptors)
            .addInterceptor { chain ->
                val req = chain.request()
                val authHeader = req.header("Authorization")
                val masked = authHeader?.replace(Regex("Bearer\\s+(.+)"), "Bearer [REDACTED]") ?: "(none)"
                Log.d("RetrofitClient", "Request: ${req.url} Authorization=$masked")
                chain.proceed(req)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private fun createApiService(): ApiService {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }

    val apiService: ApiService by lazy { createApiService() }
}