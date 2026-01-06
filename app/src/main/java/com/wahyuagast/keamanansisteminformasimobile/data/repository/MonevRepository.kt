package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.MonevResponse
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

class MonevRepository {
    private val api = RetrofitClient.apiService

    suspend fun getMonev(): Resource<MonevResponse> {
        return try {
            val response = api.getMonev()
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
