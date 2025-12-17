package com.wahyuagast.keamanansisteminformasimobile.data.remote

import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Blocking here is generally discouraged but standard for OkHttp interceptors in simple apps.
        // For production, consider using an Authenticator or async refresh logic if needed.
        val token = runBlocking {
            tokenManager.token.firstOrNull()
        }

        return if (token != null) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }
    }
}
