package com.wahyuagast.keamanansisteminformasimobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentTypeResponse(
    @SerialName("document_types") val documentTypes: List<DocumentType> = emptyList(),
    val message: String? = null
)

@Serializable
data class DocumentType(
    val id: Int,
    val name: String
)

@Serializable
data class DocumentStoreRequest(
    @SerialName("document_type_id") val documentTypeId: Int,
    val description: String
)

@Serializable
data class DocumentStoreResponse(
    val message: String,
    val status: String? = null
)

@Serializable
data class DocumentDto(
    val id: Int,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("document_type_id") val documentTypeId: Int,
    val name: String? = null,
    val description: String? = null,
    @SerialName("file_path") val filePath: String? = null,
    val status: String? = null,
    @SerialName("uploaded_at") val uploadedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class AdminDocumentsResponse(
    val document: List<DocumentDto> = emptyList()
)

@Serializable
data class AdminActionRequest(
    @SerialName("admin_note") val adminNote: String? = null
)

@Serializable
data class DocumentListResponse(
    val documents: List<DocumentDto> = emptyList()
)
