package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models

import kotlinx.serialization.Serializable

@Serializable
data class DocumentDto(
    val id: String,
    val owner_id: String,
    val title: String? = null,
    val content: String? = null,
    val is_public: Boolean? = false,
    val created_at: String? = null
)