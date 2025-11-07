package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network

import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.LoginRequest
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.SignUpRequest
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface AuthApi {
    // note: baseUrl already points to /auth/v1/
    @POST("signup")
    suspend fun signUp(@Body body: SignUpRequest): Response<TokenResponse>

    @POST("token?grant_type=password")
    suspend fun signIn(@Body body: LoginRequest): Response<TokenResponse>

    // logout endpoint (uses current access token in Authorization header)
    @POST("logout")
    suspend fun logout(): Response<Unit>
}
