package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String,
    val fullname: String,
    val username: String,
    val nim: String,
    val degree: String,
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("studyProgramId") val studyProgramId: String,
    val year: String
)

@Serializable
data class AuthRegisterResponse(
    val message: String? = null,
    val user: RegisteredUser? = null,
    val awardee: RegisteredAwardee? = null
)

@Serializable
data class RegisteredUser(
    val email: String,
    @SerialName("role_id") val roleId: Int? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val id: Int? = null
)

@Serializable
data class RegisteredAwardee(
    val fullname: String? = null,
    val username: String? = null,
    val nim: String? = null,
    val degree: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("study_program_id") val studyProgramId: String? = null,
    val year: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val id: Int? = null
)
