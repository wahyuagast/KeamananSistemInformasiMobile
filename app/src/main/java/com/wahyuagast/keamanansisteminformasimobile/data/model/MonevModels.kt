package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MonevResponse(
    val timeline: List<TimelineItem> = emptyList(),
    @SerialName("form_6b") val form6b: DocumentStatus? = null,
    @SerialName("form_7a") val form7a: DocumentStatus? = null,
    @SerialName("daftar_hadir_observasi") val daftarHadirObservasi: DocumentStatus? = null,
    @SerialName("draft_laporan_pengabdian") val draftLaporanPengabdian: DocumentStatus? = null
)

@Serializable
data class TimelineItem(
    val id: Int,
    @SerialName("periode_id") val periodeId: Int? = null,
    val title: String? = null,
    val description: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    val type: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
