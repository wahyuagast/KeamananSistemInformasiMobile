package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull // Import the extension function
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody // Import the extension function
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
        studyProgram: String,
        year: String,
        fullname: String,
        imageFile: File?
    ): Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse> {
        return try {
            // FIX: Use the toRequestBody extension function for creating RequestBody parts
            val emailPart = email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val usernamePart = username.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val nimPart = nim.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val degreePart = degree.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val phonePart = phoneNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val studyPart = studyProgram.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val yearPart = year.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val fullnamePart = fullname.toRequestBody("multipart/form-data".toMediaTypeOrNull())

            val imagePart = if (imageFile != null) {
                // FIX: Use the toMediaTypeOrNull() extension function
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                okhttp3.MultipartBody.Part.createFormData("ppImg", imageFile.name, requestFile)
            } else null

            val response = apiService.updateProfile(
                emailPart, usernamePart, nimPart, degreePart, phonePart, studyPart, yearPart, fullnamePart, imagePart
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