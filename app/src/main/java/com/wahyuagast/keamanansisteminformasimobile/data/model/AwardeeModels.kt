package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AwardeeDto(
    @SerialName("awardee_id") val awardeeId: Int,
    val fullname: String,
    val nim: String,
    @SerialName("register_id") val registerId: Int? = null,
    val prodi: String? = null,
    val status: String? = null,
    val progress: Int? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null
)

@Serializable
data class AwardeeResponse(
    @SerialName("total_awardee") val totalAwardee: Int,
    val data: List<AwardeeDto> = emptyList()
)

