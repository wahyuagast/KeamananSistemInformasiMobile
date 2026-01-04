package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.data.repository.ProfileRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch

class AdminProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository()
    private val authRepository = AuthRepository(TokenManager(application))

    var profileState by mutableStateOf<Resource<ProfileResponse>>(Resource.Loading)
        private set

    var updateState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse>?>(null)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            profileState = Resource.Loading
            profileState = repository.getProfile()
        }
    }

    fun updateProfile(
        email: String, username: String, nim: String, degree: String, 
        phoneNumber: String, studyProgramId: String, year: String, fullname: String,
        imageFile: java.io.File?
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
        updateState = null
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.clearToken()
            onLogoutSuccess()
        }
    }
}
