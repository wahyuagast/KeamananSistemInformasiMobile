package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.ExamDraftResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.ExamFinalResponse
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

class ExamRepository {
    private val api = RetrofitClient.apiService

    suspend fun getExamDraft(): Resource<ExamDraftResponse> {
        return try {
            val response = api.getExamDraft()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getExamFinal(): Resource<ExamFinalResponse> {
        return try {
            val response = api.getExamFinal()
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
