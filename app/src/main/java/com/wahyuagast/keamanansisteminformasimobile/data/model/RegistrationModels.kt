package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MitraDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("partner_name") val partnerName: String? = null,
    val description: String? = null,
    val email: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("whatsapp_number") val whatsappNumber: String? = null,
    val address: String? = null,
    @SerialName("website_address") val websiteAddress: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val status: Int? = null,
    val type: String? = null
)

@Serializable
data class PeriodeDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val name: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    val status: Int? = null
)

@Serializable
data class PeriodeResponse(
    val periods: List<PeriodeDto> = emptyList(),
    val message: String? = null
)

@Serializable
data class AwardeeNestedDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val nim: String? = null,
    val fullname: String? = null,
    val username: String? = null,
    val degree: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("study_program_id") val studyProgramId: Int? = null,
    val year: String? = null,
    @SerialName("study_program") val studyProgram: StudyProgramDto? = null
)

@Serializable
data class StudyProgramDto(
    val id: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val name: String? = null,
    @SerialName("faculty_id") val facultyId: Int? = null
)

@Serializable
data class RegisterDto(
    val id: Int,
    @SerialName("registration_number") val registrationNumber: String? = null,
    val fullname: String? = null,
    val nim: String? = null,
    val faculty: String? = null,
    @SerialName("study_program") val studyProgram: String? = null,
    val email: String? = null,
    @SerialName("awardee_id") val awardeeId: Int? = null,
    @SerialName("periode_id") val periodeId: Int? = null,
    @SerialName("mitra_id") val mitraId: Int? = null,
    val status: String? = null,
    val unit: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val mitra: MitraDto? = null,
    val periode: PeriodeDto? = null,
    val awardee: AwardeeNestedDto? = null
)

@Serializable
data class RegisterResponse(
    val register: RegisterDto
)

// Registration status response used by PendaftaranScreen
@Serializable
data class RegistrationStatusResponse(
    @SerialName("status_registrasi") val statusRegistrasi: String = "",
    val progress: Int = 0,
    val message: String? = null,
    val mitras: List<MitraDto> = emptyList(),
    val periods: List<PeriodeDto> = emptyList(),
    val documents: List<DocumentDto> = emptyList()
)

// Keep older placeholder form models
@Serializable
data class RegistrationFormRequest(
    val fullname: String,
    val nim: String,
    val email: String,
    @SerialName("mitra_id") val mitraId: String? = null,
    @SerialName("periode_id") val periodeId: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null
)

@Serializable
data class RegistrationFormResponse(
    val status: Boolean = true,
    val message: String? = null
)
