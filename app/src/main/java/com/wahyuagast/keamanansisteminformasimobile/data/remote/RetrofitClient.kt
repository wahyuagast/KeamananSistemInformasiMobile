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
        // Keep logging disabled by default to avoid leaking tokens/PII in logs.
        level = HttpLoggingInterceptor.Level.NONE
        // NOTE: Some versions of HttpLoggingInterceptor support redactHeader(name)
        // which can be used in debug builds to redact Authorization header instead
        // of disabling logging. Example (uncomment in debug only):
        // loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        // loggingInterceptor.redactHeader("Authorization")
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

            // Add app headers (always) in a dedicated interceptor so later interceptors can observe them
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", "SIMOPKL-Android/1.0")
                    .header("X-App-Platform", "android")
                    .header("X-App-Version", "1.0.0")
                    .header("X-Device-Id", deviceId ?: "unknown")
                    .build()
                chain.proceed(req)
            }

            // Attach Authorization header from TokenManager using AuthInterceptor
            .addInterceptor { chain ->
                val tm = tokenManager
                AuthInterceptor(tm).intercept(chain)
            }

            // Safe debug interceptor: logs minimal info and NEVER prints the full URL or body.
            // This avoids leaking API endpoints and sensitive headers (token/PII).
            .addInterceptor { chain ->
                val req = chain.request()

                // IMPORTANT: Do NOT log full request URLs, query parameters, or bodies here.
                // If you need traceability in development, log only non-sensitive fields.

                // Mask the Authorization header if present for extra safety in logs.
                val authHeader = req.header("Authorization")
                val maskedAuth = authHeader?.replace(Regex("Bearer\\s+(.+)"), "Bearer [REDACTED]") ?: "(none)"

                // Log minimal information: HTTP method and presence of auth token.
                // Avoid printing req.url or req.url.encodedPath which may reveal endpoints.
                Log.d("RetrofitClient", "HTTP ${req.method} — Authorization=$maskedAuth")

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