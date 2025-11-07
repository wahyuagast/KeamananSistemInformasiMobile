package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models

import kotlinx.serialization.Serializable

@Serializable
data class SubmissionDto(
    val id: String,
    val user_id: String,
    val type: String,         // surat_ttd, form_2a, form_3a, monev, logbook, exam_doc, ...
    val title: String? = null,
    val description: String? = null,
    val file_url: String? = null,
    val status: String = "pending", // pending, approved, denied
    val admin_comment: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class CreateSubmissionRequest(
    val user_id: String,
    val type: String,
    val title: String? = null,
    val description: String? = null,
    val file_url: String? = null
)

@Serializable
data class RegistrationDto(
    val id: String,
    val user_id: String,
    val form_type: String, // 2A, 2B, etc
    val fields: Map<String, String> = emptyMap(),
    val file_url: String? = null,
    val status: String = "pending",
    val created_at: String? = null
)

@Serializable
data class CreateRegistrationRequest(
    val user_id: String,
    val form_type: String,
    val fields: Map<String, String> = emptyMap(),
    val file_url: String? = null
)

@Serializable
data class UploadResponse(
    val id: String? = null,
    val user_id: String? = null,
    val filename: String? = null,
    val storage_path: String? = null,
    val url: String? = null,
    val created_at: String? = null
)

@Serializable
data class SubmissionActionRequest(
    val p_submission_id: String,
    val p_action: String, // "approve","deny","comment"
    val p_comment: String? = null
)