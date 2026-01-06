package com.wahyuagast.keamanansisteminformasimobile.data.repository

import android.content.Context
import com.wahyuagast.keamanansisteminformasimobile.data.model.AdminActionRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.AwardeeDto
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentDto
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentTypeResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterDto
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class DocumentRepository(context: Context? = null) {
    private val apiService = RetrofitClient.apiService
    private val auditRepo = context?.let { AuditRepository(it.applicationContext) }

    suspend fun getDocumentTypes(): Resource<DocumentTypeResponse> {
        return try {
            val response = apiService.getDocumentTypes()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (_: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    suspend fun getAdminDocuments(): List<DocumentDto> {
        return try {
            val resp = apiService.getAdminDocuments()
            if (resp.isSuccessful) resp.body()?.document ?: emptyList() else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAwardees(): Pair<Int, List<AwardeeDto>> {
        val r = apiService.getAwardees()
        if (r.isSuccessful && r.body() != null) {
            val b = r.body()!!
            return b.totalAwardee to b.data
        } else {
            val code = r.code()
            val msg = r.errorBody()?.string()
            throw Exception("Failed to fetch awardees: HTTP $code ${r.message()} ${msg ?: ""}")
        }
    }

    suspend fun getAwardeeDetail(id: Int): AwardeeDto? {
        return try {
            val r = apiService.getAwardeeDetail(id)
            if (r.isSuccessful) r.body() else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun postAwardeeComment(id: Int, comment: String?): Boolean {
        return try {
            val action = AdminActionRequest(adminNote = comment)
            val r = apiService.postAwardeeComment(id, action)
            r.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun submitDocumentRequest(
        documentTypeId: Int,
        description: String
    ): Resource<DocumentStoreResponse> {
        return try {
            val request = DocumentStoreRequest(documentTypeId, description)
            val response = apiService.submitDocumentRequest(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val parsedError = try {
                    if (errorBody != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        json.decodeFromString<DocumentStoreResponse>(errorBody)
                    } else null
                } catch (_: Exception) {
                    null
                }

                val errorMessage = parsedError?.message ?: response.message()
                Resource.Error(errorMessage)
            }
        } catch (_: Exception) {
            Resource.Error("An unknown error occurred")
        }
    }

    suspend fun uploadDocument(
        file: java.io.File,
        documentTypeId: Int
    ): Resource<DocumentStoreResponse> {
        return try {
            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val body = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestFile)
            val typeIdBody =
                documentTypeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadRegistrationDocument(body, typeIdBody)

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val parsedError = try {
                    if (errorBody != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        json.decodeFromString<DocumentStoreResponse>(errorBody)
                    } else null
                } catch (_: Exception) {
                    null
                }

                val errorMessage = parsedError?.message ?: response.message()
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    // Admin actions: approve/reject document
    suspend fun approveDocument(documentId: Int, file: java.io.File, comment: String): Boolean {
        return try {
            val noteBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())
            // Assuming PDF is common, or generic octet-stream
            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val docPart =
                okhttp3.MultipartBody.Part.createFormData("document", file.name, requestFile)

            val resp = apiService.approveDocument(documentId, noteBody, docPart)
            val success = resp.isSuccessful
            if (success) {
                // enqueue audit
                auditRepo?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        it.enqueueEvent(
                            null,
                            "DOCUMENT_APPROVE",
                            documentId.toString(),
                            mapOf("comment" to comment)
                        )
                    }
                }
            }
            success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun rejectDocument(documentId: Int, comment: String? = null): Boolean {
        return try {
            val body = comment?.let { AdminActionRequest(adminNote = it) }
            val resp = apiService.rejectDocument(documentId, body)
            val success = resp.isSuccessful
            if (success) {
                auditRepo?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        it.enqueueEvent(
                            null,
                            "DOCUMENT_REJECT",
                            documentId.toString(),
                            mapOf("comment" to (comment ?: ""))
                        )
                    }
                }
            }
            success
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getAwardeeRegister(id: Int): RegisterDto? {
        return try {
            val r = apiService.getAwardeeRegister(id)
            if (r.isSuccessful) r.body()?.register else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun approveAwardeeRegister(id: Int): RegisterDto? {
        return try {
            val r = apiService.approveAwardeeRegister(id)
            return if (r.isSuccessful) {
                auditRepo?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        it.enqueueEvent(
                            null,
                            "REGISTER_APPROVE",
                            id.toString(),
                            emptyMap()
                        )
                    }
                }
                r.body()?.register
            } else {
                null
            }
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun rejectAwardeeRegister(id: Int): RegisterDto? {
        return try {
            val r = apiService.rejectAwardeeRegister(id)
            return if (r.isSuccessful) {
                auditRepo?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        it.enqueueEvent(
                            null,
                            "REGISTER_REJECT",
                            id.toString(),
                            emptyMap()
                        )
                    }
                }
                r.body()?.register
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
