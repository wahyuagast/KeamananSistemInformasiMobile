package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.Constants
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.data.TokenManager
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit

class AuthInterceptor(private val tokenManager: TokenManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val req = chain.request()
        val token = kotlin.runCatching {
            kotlinx.coroutines.runBlocking { tokenManager.getToken() }
        }.getOrNull()

        val newReq = req.newBuilder()
            .addHeader("apikey", Constants.SUPABASE_ANON_KEY)
            .addHeader("Content-Type", "application/json")
            .apply {
                if (!token.isNullOrEmpty()) addHeader("Authorization", "Bearer $token")
            }
            .build()
        return chain.proceed(newReq)
    }
}

object RetrofitProvider {
    private val json = Json { ignoreUnknownKeys = true }

    fun provideAuthApi(): AuthApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", Constants.SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .callTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.AUTH_BASE + "/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
        return retrofit.create(AuthApi::class.java)
    }

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
}