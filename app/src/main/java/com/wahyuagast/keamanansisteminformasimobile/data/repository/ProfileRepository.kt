package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProfileRepository {
    private val apiService = RetrofitClient.apiService
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getProfile(): Resource<ProfileResponse> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {            // Handle unsuccessful responses by returning an Error resource
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            // Handle exceptions like network errors
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateProfile(
        email: String,
        username: String,
        nim: String,
        degree: String,
        phoneNumber: String,
        studyProgramId: String, // Expecting ID as string
        year: String,
        fullname: String,
        imageFile: File?
    ): Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse> {
        return try {
            val mediaType = "multipart/form-data".toMediaTypeOrNull()

            val emailPart = if (email.isNotEmpty()) email.toRequestBody(mediaType) else null
            val usernamePart =
                if (username.isNotEmpty()) username.toRequestBody(mediaType) else null
            val nimPart = if (nim.isNotEmpty()) nim.toRequestBody(mediaType) else null
            val degreePart = if (degree.isNotEmpty()) degree.toRequestBody(mediaType) else null
            val phonePart =
                if (phoneNumber.isNotEmpty()) phoneNumber.toRequestBody(mediaType) else null
            val studyPart =
                if (studyProgramId.isNotEmpty()) studyProgramId.toRequestBody(mediaType) else null
            val yearPart = if (year.isNotEmpty()) year.toRequestBody(mediaType) else null
            val fullnamePart =
                if (fullname.isNotEmpty()) fullname.toRequestBody(mediaType) else null

            val imagePart = if (imageFile != null) {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                // Key 'ppImg' matches screenshot
                okhttp3.MultipartBody.Part.createFormData("ppImg", imageFile.name, requestFile)
            } else null

            val response = apiService.updateProfile(
                emailPart,
                usernamePart,
                nimPart,
                degreePart,
                phonePart,
                studyPart,
                yearPart,
                fullnamePart,
                imagePart
            )

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
