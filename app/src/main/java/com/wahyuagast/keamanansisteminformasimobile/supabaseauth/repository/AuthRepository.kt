package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository

import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.data.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.LoginRequest
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.SignUpRequest
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.AuthApi
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.PostgrestApi

class AuthRepository(
    private val authApi: AuthApi,
    private val postgrestApi: PostgrestApi,
    private val tokenManager: TokenManager
) {
    suspend fun signUp(email: String, password: String) = authApi.signUp(
        SignUpRequest(
            email,
            password
        )
    )
    suspend fun signIn(email: String, password: String) = authApi.signIn(
        LoginRequest(
            email,
            password
        )
    )

    suspend fun getAllProfiles() = postgrestApi.getAllProfiles()
    suspend fun getDocumentsForOwner(ownerEq: String?) = postgrestApi.getDocuments(ownerEq)

    suspend fun saveToken(token: String) = tokenManager.saveToken(token)
    suspend fun clearToken() = tokenManager.clear()
    suspend fun getMyProfile() = postgrestApi.getMyProfile()
}
