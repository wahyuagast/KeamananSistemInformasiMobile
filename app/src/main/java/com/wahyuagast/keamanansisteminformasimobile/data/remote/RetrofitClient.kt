@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.wahyuagast.keamanansisteminformasimobile.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wahyuagast.keamanansisteminformasimobile.BuildConfig
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = BuildConfig.API_BASE_URL

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Demo toggle. Must be used with BuildConfig.DEBUG.
    // WARNING: enabling this in production will print sensitive info (tokens, full URLs).
    private const val REVEAL_SENSITIVE_LOGS = false // do not set true

    private lateinit var tokenManager: com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
    private var deviceId: String? = null

    @SuppressLint("HardwareIds")
    fun initialize(context: Context) {
        val appCtx = context.applicationContext
        tokenManager = com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager(appCtx)
        deviceId = Settings.Secure.getString(appCtx.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown"

        // Ensure OkHttp's internal loggers don't print request URLs or headers at INFO level.
        try {
            val jul = java.util.logging.Logger.getLogger("okhttp3")
            jul.level = java.util.logging.Level.WARNING
            java.util.logging.Logger.getLogger("okhttp3.OkHttpClient").level =
                java.util.logging.Level.WARNING
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                AppLog.w("RetrofitClient", "Could not set okhttp logger level: ${e.message}")
            }
        }

        // Schedule a periodic work to upload audit logs when network is available
        try {
            val wm = androidx.work.WorkManager.getInstance(appCtx)
            val request =
                androidx.work.PeriodicWorkRequestBuilder<com.wahyuagast.keamanansisteminformasimobile.worker.AuditUploadWorker>(
                    15,
                    java.util.concurrent.TimeUnit.MINUTES
                )
                    .setConstraints(
                        androidx.work.Constraints.Builder()
                            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
                    )
                    .build()
            wm.enqueueUniquePeriodicWork(
                "audit_upload_work",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                AppLog.e(
                    "RetrofitClient",
                    "Failed to schedule audit worker: ${e.message}"
                )
            }
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()

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

                // Mask the Authorization header if present for extra safety in logs.
                val authHeader = req.header("Authorization")
                val maskedAuth =
                    authHeader?.replace(Regex("Bearer\\s+(.+)"), "Bearer [REDACTED]") ?: "(none)"

                // Only log when in debug builds
                if (BuildConfig.DEBUG) {
                    AppLog.d("RetrofitClient", "HTTP ${req.method} — Authorization=$maskedAuth")
                }

                // -----------------------------
                // Demo block: reveal sensitive info
                // -----------------------------
                // WARNING: This prints full URL, headers, and request body. Do NOT enable in production.
                if (REVEAL_SENSITIVE_LOGS && BuildConfig.DEBUG) {
                    try {
                        val fullUrl = req.url.toString()
                        val headers =
                            req.headers.toMultimap().entries.joinToString("; ") { (k, v) ->
                                "$k: ${
                                    v.joinToString(",")
                                }"
                            }
                        var bodyString = ""
                        req.body?.let { body ->
                            val buffer = okio.Buffer()
                            body.writeTo(buffer)
                            bodyString = buffer.readUtf8()
                        }

                        AppLog.w(
                            "RetrofitClient",
                            "DEMO (sensitive) - ${req.method} $fullUrl\nHeaders: $headers\nBody: $bodyString"
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            AppLog.e(
                                "RetrofitClient",
                                "Failed reading request for demo: ${e.message}"
                            )
                        }
                    }
                }

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