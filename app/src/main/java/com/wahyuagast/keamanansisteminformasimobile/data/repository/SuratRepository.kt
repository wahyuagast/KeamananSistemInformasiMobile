package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.*
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

class SuratRepository {
    private val api = RetrofitClient.apiService

    suspend fun getDocuments(): Resource<DocumentListResponse> {
        return try {
            val response = api.getDocuments()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getDocumentTypes(): Resource<DocumentTypeResponse> {
        return try {
            val response = api.getDocumentTypes()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun submitDocument(typeId: Int, description: String): Resource<DocumentStoreResponse> {
        return try {
            val request = DocumentStoreRequest(documentTypeId = typeId, description = description)
            val response = api.submitDocumentRequest(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}
