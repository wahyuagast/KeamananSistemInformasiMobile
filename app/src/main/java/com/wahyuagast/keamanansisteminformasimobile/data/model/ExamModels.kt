package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExamDraftResponse(
    @SerialName("draft_jurnal_jupita") val draftJurnalJupita: DocumentStatus? = null,
    @SerialName("draft_laporan_pengabdian") val draftLaporanPengabdian: DocumentStatus? = null,
    @SerialName("permohonan_berita_acara") val permohonanBeritaAcara: DocumentStatus? = null
)

@Serializable
data class ExamFinalResponse(
    @SerialName("form_revisi_penguji") val formRevisiPenguji: DocumentStatus? = null,
    @SerialName("nilai_ujian_pkl") val nilaiUjianPkl: DocumentStatus? = null,
    @SerialName("jurnal_final") val jurnalFinal: DocumentStatus? = null,
    @SerialName("laporan_akhir") val laporanAkhir: DocumentStatus? = null,
    @SerialName("berita_acara_ujian") val beritaAcaraUjian: DocumentStatus? = null,
    @SerialName("nilai_Huruf") val nilaiHuruf: String? = null,
    @SerialName("nilai_akhir") val nilaiAkhir: String? = null
)
