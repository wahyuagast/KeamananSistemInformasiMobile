package com.wahyuagast.keamanansisteminformasimobile.data.remote

import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @retrofit2.http.GET("profile")
    suspend fun getProfile(): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse>

    @retrofit2.http.Multipart
    @POST("profile")
    suspend fun updateProfile(
        @retrofit2.http.Part("email") email: okhttp3.RequestBody,
        @retrofit2.http.Part("username") username: okhttp3.RequestBody,
        @retrofit2.http.Part("nim") nim: okhttp3.RequestBody,
        @retrofit2.http.Part("degree") degree: okhttp3.RequestBody,
        @retrofit2.http.Part("phoneNumber") phoneNumber: okhttp3.RequestBody,
        @retrofit2.http.Part("study_program") studyProgram: okhttp3.RequestBody,
        @retrofit2.http.Part("year") year: okhttp3.RequestBody,
        @retrofit2.http.Part("fullname") fullname: okhttp3.RequestBody,
        @retrofit2.http.Part ppImg: okhttp3.MultipartBody.Part?
    ): Response<com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse>
}
