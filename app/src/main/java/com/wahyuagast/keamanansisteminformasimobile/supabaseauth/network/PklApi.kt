package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network

import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.*
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PklApi {
    // PostgREST endpoints (RLS protects rows)
    @GET("pkl_submissions")
    suspend fun getMySubmissions(): List<SubmissionDto>

    @POST("pkl_submissions")
    suspend fun createSubmission(@Body req: CreateSubmissionRequest): Response<SubmissionDto>

    @GET("pkl_registrations")
    suspend fun getMyRegistrations(): List<RegistrationDto>

    @POST("pkl_registrations")
    suspend fun createRegistration(@Body req: CreateRegistrationRequest): Response<RegistrationDto>

    // upload metadata (optional)
    @POST("pkl_uploads")
    suspend fun createUploadMetadata(@Body upload: UploadResponse): Response<UploadResponse>

    // RPCs
    @POST("rpc/get_my_profile")
    suspend fun getMyProfile(): List<com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto>

    @POST("rpc/submission_action")
    suspend fun submissionAction(@Body body: SubmissionActionRequest): Response<Unit>

    // Admin: fetch all registrations (RLS must allow your admin user)
    @GET("pkl_registrations")
    suspend fun getRegistrationsAdmin(
        @Query("order") order: String = "created_at.desc"
    ): List<RegistrationDto>

    @Headers("Prefer: return=minimal")
    @PATCH("pkl_registrations")
    suspend fun updateRegistration(
        @Body body: RegistrationUpdateRequest,
        @Query("id") idFilter: String // pass "eq.<uuid>"
    ): Response<Unit>

    // Admin: update registration status/comment (PATCH pkl_registrations?id=eq.<uuid>)
    @Headers("Prefer: return=minimal")
    @PATCH("pkl_registrations")
    suspend fun updateRegistration(
        @Body body: Map<String, @JvmSuppressWildcards Any?>,
        @Query("id") idFilter: String // pass "eq.<uuid>"
    ): Response<Unit>
}

@Serializable
data class UploadMetadataRequest(
    val user_id: String,
    val filename: String,
    val storage_path: String,
    val url: String
)
