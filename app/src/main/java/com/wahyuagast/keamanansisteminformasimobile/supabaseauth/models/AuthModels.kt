package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class TokenResponse(
    val access_token: String? = null,
    val refresh_token: String? = null,
    val token_type: String? = null,
    val expires_in: Int? = null,
    val user: UserPayload? = null,
    val error_description: String? = null
)

@Serializable
data class UserPayload(val id: String? = null, val email: String? = null)

@Serializable
data class ProfileDto(
    val id: String,
    val full_name: String? = null,
    val email: String? = null,
    val role: String? = null
)