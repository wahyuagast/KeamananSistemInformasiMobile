package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.data.repository.ProfileRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch
import java.io.File

class MahasiswaProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = com.wahyuagast.keamanansisteminformasimobile.data.repository.ProfileRepository()
    private val authRepository = com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository(TokenManager(application))
    private val mitraRepository = com.wahyuagast.keamanansisteminformasimobile.data.repository.MitraRepository()
    private val registrationRepository = com.wahyuagast.keamanansisteminformasimobile.data.repository.RegistrationRepository()
    private val documentRepository = com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository()

    var profileState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse>>(Resource.Loading)
        private set

    var mitraState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.MitraResponse>>(Resource.Loading)
        private set

    var registrationState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationStatusResponse>>(Resource.Loading)
        private set

    var formSubmissionState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormResponse>>(Resource.Idle)
        private set
    
    var documentTypesState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentTypeResponse>>(Resource.Idle)
        private set

    var documentSubmissionState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreResponse>>(Resource.Idle)
        private set

    var updateState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse?>>(Resource.Idle)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            profileState = Resource.Loading
            profileState = repository.getProfile()
        }
    }

    fun loadMitras() {
        viewModelScope.launch {
            mitraState = Resource.Loading
            mitraState = mitraRepository.getMitras()
        }
    }

    fun loadRegistrationStatus() {
        viewModelScope.launch {
            registrationState = Resource.Loading
            registrationState = registrationRepository.getRegistrationStatus()
        }
    }
    
    fun loadDocumentTypes() {
        viewModelScope.launch {
            documentTypesState = Resource.Loading
            documentTypesState = documentRepository.getDocumentTypes()
        }
    }
    
    fun submitDocumentRequest(documentTypeId: Int, description: String) {
        viewModelScope.launch {
            documentSubmissionState = Resource.Loading
            documentSubmissionState = documentRepository.submitDocumentRequest(documentTypeId, description)
            if (documentSubmissionState is Resource.Success) {
                // Optionally reload registration status if documents are listed there,
                // or just keep success state for UI feedback
            }
        }
    }
    
    fun resetDocumentSubmissionState() {
        documentSubmissionState = Resource.Idle
    }

    fun submitRegistrationForm(mitraId: String, periodeId: String, startDate: String, endDate: String) {
        val sdf = java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault())
        try {
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)
            if (start != null && end != null) {
                if (!end.after(start)) { // End date must be strictly AFTER start date (cannot be same or before)
                    formSubmissionState = Resource.Error("Tanggal selesai tidak boleh sebelum tanggal mulai!")
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewModelScope.launch {
            formSubmissionState = Resource.Loading
            formSubmissionState = registrationRepository.submitRegistrationForm(mitraId, periodeId, startDate, endDate)
            if (formSubmissionState is Resource.Success) {
                 loadRegistrationStatus() // Refresh status after successful submission
            }
        }
    }

    fun resetFormSubmissionState() {
        formSubmissionState = Resource.Idle
    }

    fun updateProfile(
        email: String, username: String, nim: String, degree: String,
        phoneNumber: String, studyProgramId: String, year: String, fullname: String,
        imageFile: File?
    ) {
        viewModelScope.launch {
            updateState = Resource.Loading
            updateState = repository.updateProfile(
                email, username, nim, degree, phoneNumber, studyProgramId, year, fullname, imageFile
            )
            // Reload profile on success
            if (updateState is Resource.Success) {
                loadProfile()
            }
        }
    }

    fun resetUpdateState() {
        updateState = Resource.Idle
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.clearToken()
            onLogoutSuccess()
        }
    }
}
