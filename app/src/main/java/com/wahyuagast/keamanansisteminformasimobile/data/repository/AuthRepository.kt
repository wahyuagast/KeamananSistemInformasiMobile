package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginResponse
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuthRegisterResponse

class AuthRepository(private val tokenManager: TokenManager) {
    private val apiService = RetrofitClient.apiService
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveToken(token: String) {
        tokenManager.saveToken(token)
    }

    suspend fun clearToken() {
        tokenManager.clearToken()
    }

    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.status == true) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    try {
                        val errorResponse = json.decodeFromString<LoginResponse>(errorBody)
                        Resource.Error(
                            message = errorResponse.message ?: "Login failed",
                            errors = errorResponse.errors
                        )
                    } catch (e: Exception) {
                        Resource.Error("Parsing error: ${e.message}")
                    }
                } else {
                    // Check if body status is false but 200 OK (logic from prompt: "or (no email address recorded): { status: false ... }")
                    val body = response.body()
                    if (body != null && !body.status) {
                         Resource.Error(body.message ?: "Login failed")
                    } else {
                        Resource.Error(response.message())
                    }
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    // Register method
    suspend fun register(request: RegisterRequest): Resource<AuthRegisterResponse> {
        return try {
            val resp = apiService.register(request)
            if (resp.isSuccessful && resp.body() != null) {
                Resource.Success(resp.body()!!)
            } else {
                val err = resp.errorBody()?.string()
                if (err != null) {
                    try {
                        val parsed = json.decodeFromString<AuthRegisterResponse>(err)
                        Resource.Error(parsed.message ?: resp.message())
                    } catch (e: Exception) {
                        Resource.Error(resp.message())
                    }
                } else {
                    Resource.Error(resp.message())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

}
