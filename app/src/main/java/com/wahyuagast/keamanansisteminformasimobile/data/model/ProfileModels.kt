package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val user: UserProfile,
    val message: String? = null // For error cases like "Unauthenticated."
)

@Serializable
data class UserProfile(
    val id: Int,
    val email: String,
    @SerialName("role_id") val roleId: Int,
    @SerialName("created_at") val createdAt: String,
    val awardee: Awardee? = null
)

@Serializable
data class Awardee(
    val id: Int,
    val nim: String,
    val fullname: String,
    val username: String,
    val degree: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("study_program_id") val studyProgramId: Int,
    val year: String
)

@Serializable
data class UpdateProfileResponse(
    val message: String? = null,
    val data: UpdateProfileData? = null
)

@Serializable
data class UpdateProfileData(
    val user: UserProfile,
    val awardee: Awardee? = null
)
