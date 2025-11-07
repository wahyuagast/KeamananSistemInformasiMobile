package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network

import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.LoginRequest
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.SignUpRequest
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("signup")
    suspend fun signUp(@Body body: SignUpRequest): TokenResponse

    @POST("token?grant_type=password")
    suspend fun signIn(@Body body: LoginRequest): TokenResponse
}