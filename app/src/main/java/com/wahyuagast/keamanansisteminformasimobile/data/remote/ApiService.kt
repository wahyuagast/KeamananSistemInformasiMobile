package com.wahyuagast.keamanansisteminformasimobile.data.remote

import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuditBatchRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Register endpoint
    @POST("register")
    suspend fun register(
        @Body request: com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterRequest,
        @retrofit2.http.Header("Authorization") token: String? = null
    ): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.AuthRegisterResponse>

    @GET("profile")
    suspend fun getProfile(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse>

    @Multipart
    @POST("profile")
    suspend fun updateProfile(
        @Part("email") email: okhttp3.RequestBody?,
        @Part("username") username: okhttp3.RequestBody?,
        @Part("nim") nim: okhttp3.RequestBody?,
        @Part("degree") degree: okhttp3.RequestBody?,
        @Part("phoneNumber") phoneNumber: okhttp3.RequestBody?,
        @Part("studyProgramId") studyProgramId: okhttp3.RequestBody?,
        @Part("year") year: okhttp3.RequestBody?,
        @Part("fullname") fullname: okhttp3.RequestBody?,
        ppImg: okhttp3.MultipartBody.Part?
    ): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse>

    @GET("mitras")
    suspend fun getMitras(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.MitraResponse>

    @GET("registation")
    suspend fun getRegistrationStatus(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationStatusResponse>

    @GET("periods")
    suspend fun getPeriods(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.PeriodeResponse>

    @POST("registation/form")
    suspend fun submitRegistrationForm(@Body request: com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormRequest): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormResponse>

    @GET("documents/types")
    suspend fun getDocumentTypes(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentTypeResponse>

    @POST("document_store")
    suspend fun submitDocumentRequest(@Body request: com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreRequest): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreResponse>

    @Multipart
    @POST("documents/regis")
    suspend fun uploadRegistrationDocument(
        @Part file: okhttp3.MultipartBody.Part,
        @Part("document_type_id") documentTypeId: okhttp3.RequestBody
    ): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreResponse>

    // Admin: fetch awardees (students)
    @GET("admin/awardee")
    suspend fun getAwardees(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.AwardeeResponse>

    // Admin: fetch awardee detail
    @GET("admin/awardee/detail/{id}")
    suspend fun getAwardeeDetail(@Path("id") id: Int): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.AwardeeDto>

    // Admin: post comment on awardee
    @POST("admin/awardee/{id}/comment")
    suspend fun postAwardeeComment(@Path("id") id: Int, @Body body: com.wahyuagast.keamanansisteminformasimobile.data.model.AdminActionRequest): Response<Unit>

    // Admin: fetch documents
    @GET("admin/documents")
    suspend fun getAdminDocuments(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.AdminDocumentsResponse>

    // Admin approve/reject endpoints
    @Multipart
    @POST("admin/documents/{id}/approve")
    suspend fun approveDocument(
        @Path("id") id: Int,
        @Part("admin_note") adminNote: okhttp3.RequestBody,
        @Part document: okhttp3.MultipartBody.Part
    ): Response<Unit>

    @POST("admin/documents/{id}/reject")
    suspend fun rejectDocument(@Path("id") id: Int, @Body body: com.wahyuagast.keamanansisteminformasimobile.data.model.AdminActionRequest? = null): Response<Unit>

    // Admin: fetch awardee register detail
    @GET("admin/awardee/register/{id}")
    suspend fun getAwardeeRegister(@Path("id") id: Int): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterResponse>

    // Admin: approve/reject registration
    @POST("admin/awardee/register/approve/{id}")
    suspend fun approveAwardeeRegister(@Path("id") id: Int): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterResponse>

    @POST("admin/awardee/register/reject/{id}")
    suspend fun rejectAwardeeRegister(@Path("id") id: Int): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterResponse>

    @GET("implementation")
    suspend fun getImplementation(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.ImplementationResponse>

    @GET("exam/draft")
    suspend fun getExamDraft(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.ExamDraftResponse>

    @GET("exam/final")
    suspend fun getExamFinal(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.ExamFinalResponse>

    @GET("monev")
    suspend fun getMonev(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.MonevResponse>

    @GET("documents")
    suspend fun getDocuments(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentListResponse>

    // Audit endpoint: accept batch of audit logs from clients
    @POST("audit/logs")
    suspend fun postAuditLogs(@Body request: AuditBatchRequest): Response<Unit>
}
