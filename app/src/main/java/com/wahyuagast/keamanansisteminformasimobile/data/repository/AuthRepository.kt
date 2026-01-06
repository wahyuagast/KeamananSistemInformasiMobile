package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuthRegisterResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterRequest
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.serialization.json.Json

class AuthRepository(private val tokenManager: TokenManager) {
    private val apiService = RetrofitClient.apiService
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveToken(token: String) {
        tokenManager.saveToken(token)
    }

    suspend fun clearToken() {
        tokenManager.clearToken()
    }

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    suspend fun getProfile(): Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                // If 401, it's not resource error, it's just error. Logic to clear token might be upstream.
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
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
    suspend fun register(
        request: RegisterRequest,
        token: String? = null
    ): Resource<AuthRegisterResponse> {
        return try {
            val resp = apiService.register(request, token)
            if (resp.isSuccessful && resp.body() != null) {
                Resource.Success(resp.body()!!)
            } else {
                val err = resp.errorBody()?.string()
                if (err != null) {
                    try {
                        val parsed = json.decodeFromString<AuthRegisterResponse>(err)
                        Resource.Error(
                            message = parsed.message ?: resp.message(),
                            errors = parsed.errors
                        )
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
