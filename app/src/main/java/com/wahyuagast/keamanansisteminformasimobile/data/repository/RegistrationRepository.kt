package com.wahyuagast.keamanansisteminformasimobile.data.repository

import com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationStatusResponse
import com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuditRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationRepository {
    private val api = RetrofitClient.apiService
    // AuditRepository will be created lazily where needed; registration repository is not Android-specific.

    private fun getAuditRepo(): AuditRepository? {
        // Try to get application context from RetrofitClient initialization path or return null
        return try {
            val ctx = com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient::class.java
            // Not ideal - calling constructor with app context should be done in ViewModel instead.
            null
        } catch (_: Exception) { null }
    }

    suspend fun getRegistrationStatus(): Resource<RegistrationStatusResponse> {
        return try {
            val resp = api.getRegistrationStatus()
            if (resp.isSuccessful && resp.body() != null) {
                Resource.Success(resp.body()!!)
            } else if (resp.code() == 404) {
                // Determine if 404 means "Not Registered". Try to fetch resources manually.
                AppLog.w("RegRepo", "Status 404, attempting to fetch resources manually")

                // Fetch Mitras
                val mitrasResp = api.getMitras()
                val mitrasList = if (mitrasResp.isSuccessful) mitrasResp.body()?.mitra ?: emptyList() else emptyList()

                // Fetch Periods (Guessing endpoint)
                val periodsResp = try { api.getPeriods() } catch(_ : Exception) { null }
                val periodsList = if (periodsResp?.isSuccessful == true) periodsResp.body()?.periods ?: emptyList() else emptyList()

                // Return "Not Registered" state with available resources
                Resource.Success(RegistrationStatusResponse(
                    statusRegistrasi = "belum_terdaftar",
                    mitras = mitrasList.map { 
                        // Map Mitra (from MitraResponse) to MitraDto (for RegistrationStatusResponse)
                        com.wahyuagast.keamanansisteminformasimobile.data.model.MitraDto(
                            id = it.id,
                            partnerName = it.partnerName,
                            address = it.address,
                            description = it.description,
                            email = it.email,
                            phoneNumber = it.phoneNumber,
                            websiteAddress = it.websiteAddress,
                            imageUrl = it.imageUrl,
                            status = it.status,
                            type = it.type,
                            whatsappNumber = it.whatsappNumber
                        )
                    },
                    periods = periodsList,
                    message = "Silahkan lakukan pendaftaran"
                ))
            } else {
                Resource.Error(resp.message())
            }
        } catch (_: Exception) {
            Resource.Error("Unknown error")
        }
    }

    suspend fun getPeriods(): Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.PeriodeResponse> {
        return try {
            val resp = api.getPeriods()
            if (resp.isSuccessful && resp.body() != null) {
                Resource.Success(resp.body()!!)
            } else {
                Resource.Error(resp.message())
            }
        } catch (_: Exception) {
            Resource.Error("Unknown error")
        }
    }

    suspend fun submitRegistrationForm(
        fullname: String, nim: String, email: String,
        mitraId: String, periodeId: String, startDate: String, endDate: String
    ): Resource<RegistrationFormResponse> {
        return try {
            val request = RegistrationFormRequest(
                fullname = fullname,
                nim = nim,
                email = email,
                mitraId = mitraId,
                periodeId = periodeId,
                startDate = startDate,
                endDate = endDate
            )
            val resp = api.submitRegistrationForm(request)
            if (resp.isSuccessful && resp.body() != null) {
                // enqueue audit event asynchronously (best effort)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // We don't have direct access to context here; audit enqueue is usually done in ViewModel.
                        // If desired, move enqueue to the caller (ViewModel) where context is available.
                    } catch (_: Exception) {}
                }
                Resource.Success(resp.body()!!)
            } else {
                // Avoid logging raw error bodies (can contain sensitive data).
                val errorBody = resp.errorBody()?.string()
                AppLog.e("RegRepo", "Submit failed: HTTP ${resp.code()} - check server logs")
                val errorMessage = try {
                    if (errorBody != null) {
                        // Attempt to extract a safe message from the error body
                        org.json.JSONObject(errorBody).optString("message", resp.message())
                    } else {
                        resp.message()
                    }
                } catch (_: Exception) {
                    resp.message()
                }
                // Optionally enqueue failed attempt (move to ViewModel where context exists)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // no-op here by default
                    } catch (_: Exception) {}
                }
                Resource.Error(errorMessage)
            }
        } catch (_: Exception) {
            Resource.Error("Unknown error")
        }
    }
}
