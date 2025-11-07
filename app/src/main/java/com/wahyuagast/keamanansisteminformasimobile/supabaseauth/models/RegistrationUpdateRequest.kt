package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationUpdateRequest(
    val status: String,
    val admin_comment: String? = null
)