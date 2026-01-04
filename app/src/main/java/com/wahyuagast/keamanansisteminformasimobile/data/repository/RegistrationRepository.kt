package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationStatusResponse
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

class RegistrationRepository {
    private val api = RetrofitClient.apiService

    suspend fun getRegistrationStatus(): Resource<RegistrationStatusResponse> {
        return try {
            val resp = api.getRegistrationStatus()
            if (resp.isSuccessful && resp.body() != null) {
                Resource.Success(resp.body()!!)
            } else {
                Resource.Error(resp.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun submitRegistrationForm(mitraId: String, periodeId: String, startDate: String, endDate: String): Resource<RegistrationFormResponse> {
        return try {
            val request = RegistrationFormRequest(
                fullname = "",
                nim = "",
                email = "",
                mitraId = mitraId,
                periodeId = periodeId,
                startDate = startDate,
                endDate = endDate
            )
            val resp = api.submitRegistrationForm(request)
            if (resp.isSuccessful && resp.body() != null) {
                Resource.Success(resp.body()!!)
            } else {
                Resource.Error(resp.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}
