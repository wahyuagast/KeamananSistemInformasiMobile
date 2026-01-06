package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImplementationResponse(
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    @SerialName("current_week") val currentWeek: Int = 0,
    @SerialName("total_weeks") val totalWeeks: Int = 0,
    @SerialName("progress_time") val progressTime: Int = 0,
    @SerialName("form_3a") val form3a: DocumentStatus? = null,
    @SerialName("form_4b") val form4b: DocumentStatus? = null,
    @SerialName("form_5a") val form5a: DocumentStatus? = null
)

@Serializable
data class DocumentStatus(
    val status: String? = null,
    val comment: String? = null,
    @SerialName("file_path") val filePath: String? = null
)
