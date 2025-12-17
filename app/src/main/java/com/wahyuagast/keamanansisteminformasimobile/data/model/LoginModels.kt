package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val status: Boolean = false,
    val message: String? = null,
    val token: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("expires_in") val expiresIn: Int? = null,
    val user: User? = null,
    val errors: Map<String, List<String>>? = null
)

@Serializable
data class User(
    val id: Int,
    val email: String,
    @SerialName("role_id") val roleId: Int,
    val status: Int,
    @SerialName("is_registered") val isRegistered: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
