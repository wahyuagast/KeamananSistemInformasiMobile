package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository

import android.content.Context
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.*
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.PklApi
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.data.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class PklRepository(
    private val api: PklApi,
    private val tokenManager: TokenManager,
    private val context: Context
) {
    suspend fun getMySubmissions(): List<SubmissionDto> = api.getMySubmissions()
    suspend fun createSubmission(req: CreateSubmissionRequest): SubmissionDto? {
        val r = api.createSubmission(req)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun getMyRegistrations(): List<RegistrationDto> = api.getMyRegistrations()
    suspend fun createRegistration(req: CreateRegistrationRequest): RegistrationDto? {
        val r = api.createRegistration(req)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun doSubmissionAction(submissionId: String, action: String, comment: String? = null): Boolean {
        val body = SubmissionActionRequest(p_submission_id = submissionId, p_action = action, p_comment = comment)
        val r = api.submissionAction(body)
        return r.isSuccessful
    }

    // Upload to Supabase Storage via PUT. Returns UploadResponse with public URL.
    // NOTE: ensure Constants are configured: STORAGE_BASE, PROJECT_HOST, ANON_KEY
    fun uploadFileToStorage(file: File, bucket: String, destPath: String): UploadResponse {
        val token = runBlocking { tokenManager.getToken() } ?: ""
        val client = OkHttpClient()
        val url = "${com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.Constants.STORAGE_BASE}/$bucket/$destPath"
        val body = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val req = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.Constants.SUPABASE_ANON_KEY)
            .put(body)
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw Exception("Upload failed: ${resp.code} ${resp.message}")
            val publicUrl = "https://${com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.Constants.PROJECT_HOST}/storage/v1/object/public/$bucket/$destPath"
            return UploadResponse(filename = file.name, storage_path = "$bucket/$destPath", url = publicUrl)
        }
    }

    suspend fun createUploadMetadata(userId: String, filename: String, storagePath: String, url: String): UploadResponse? {
        val resp = api.createUploadMetadata(UploadResponse(user_id = userId, filename = filename, storage_path = storagePath, url = url))
        return if (resp.isSuccessful) resp.body() else null
    }

    suspend fun getMyProfile() = api.getMyProfile().firstOrNull()
}
