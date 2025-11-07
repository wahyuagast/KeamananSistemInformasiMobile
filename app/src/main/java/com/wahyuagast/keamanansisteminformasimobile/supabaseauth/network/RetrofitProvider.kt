package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.Constants
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.data.TokenManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor

class AuthInterceptor(private val tokenManager: TokenManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val req = chain.request()
        val token = kotlin.runCatching {
            kotlinx.coroutines.runBlocking { tokenManager.getToken() }
        }.getOrNull()

        val newReqBuilder = req.newBuilder()
            .addHeader("apikey", Constants.SUPABASE_ANON_KEY)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")

        if (!token.isNullOrEmpty()) {
            newReqBuilder.addHeader("Authorization", "Bearer $token")
        }

        val newReq = newReqBuilder.build()
        return chain.proceed(newReq)
    }
}

object RetrofitProvider {
    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    fun provideAuthApi(): AuthApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", Constants.SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging) // remove or set to NONE in production
            .callTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.AUTH_BASE + "/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
        return retrofit.create(AuthApi::class.java)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun providePostgrestApi(context: Context, tokenManager: TokenManager): PostgrestApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .callTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.REST_BASE + "/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
        return retrofit.create(PostgrestApi::class.java)
    }

    fun providePklApi(tokenManager: TokenManager, context: Context): PklApi {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        val client = OkHttpClient.Builder()
            // Reuse your AuthInterceptor that injects supabase apikey + bearer token
            .addInterceptor(AuthInterceptor(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Base URL must end with "/"
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.POSTGREST_BASE) // e.g. https://<PROJECT>.supabase.co/rest/v1/
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()

        return retrofit.create(PklApi::class.java)
    }
}