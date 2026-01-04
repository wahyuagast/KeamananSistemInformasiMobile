package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MitraResponse(
    val mitra: List<Mitra> = emptyList(),
    val message: String? = null
)

@Serializable
data class Mitra(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("partner_name") val partnerName: String,
    val description: String,
    val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("whatsapp_number") val whatsappNumber: String?,
    val address: String,
    @SerialName("website_address") val websiteAddress: String?,
    @SerialName("image_url") val imageUrl: String?,
    val status: Int,
    val type: String?
)
