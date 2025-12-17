package com.wahyuagast.keamanansisteminformasimobile.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://simopkl.cloud/api/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private var tokenManager: com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager? = null

    fun initialize(context: android.content.Context) {
        tokenManager = com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager(context)
    }

    private val okHttpClient by lazy {
         OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val tm = tokenManager ?: throw IllegalStateException("RetrofitClient not initialized")
                 com.wahyuagast.keamanansisteminformasimobile.data.remote.AuthInterceptor(tm).intercept(chain)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
