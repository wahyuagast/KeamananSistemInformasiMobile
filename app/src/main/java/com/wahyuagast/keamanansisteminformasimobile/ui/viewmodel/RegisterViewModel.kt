package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(TokenManager(application))

    var registerState by mutableStateOf<Resource<RegisterResponse>?>(null)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordConfirmation by mutableStateOf("")
    var fullname by mutableStateOf("")
    var username by mutableStateOf("")
    var nim by mutableStateOf("")
    var degree by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var studyProgramId by mutableStateOf("")
    var year by mutableStateOf("")

    fun onEmailChange(v: String) { email = v }
    fun onPasswordChange(v: String) { password = v }
    fun onPasswordConfirmationChange(v: String) { passwordConfirmation = v }
    fun onFullnameChange(v: String) { fullname = v }
    fun onUsernameChange(v: String) { username = v }
    fun onNimChange(v: String) { nim = v }
    fun onDegreeChange(v: String) { degree = v }
    fun onPhoneNumberChange(v: String) { phoneNumber = v }
    fun onStudyProgramIdChange(v: String) { studyProgramId = v }
    fun onYearChange(v: String) { year = v }

    fun register() {
        viewModelScope.launch {
            // basic validation
            if (email.isBlank() || password.isBlank() || passwordConfirmation.isBlank()) {
                registerState = Resource.Error("Email and password are required")
                return@launch
            }
            if (password != passwordConfirmation) {
                registerState = Resource.Error("Password confirmation does not match")
                return@launch
            }

            registerState = Resource.Loading
            val req = RegisterRequest(
                email = email.trim(),
                password = password,
                passwordConfirmation = passwordConfirmation,
                fullname = fullname.trim(),
                username = username.trim(),
                nim = nim.trim(),
                degree = degree.trim(),
                phoneNumber = phoneNumber.trim(),
                studyProgramId = studyProgramId.trim(),
                year = year.trim()
            )
            val result = repository.register(req)
            registerState = result
        }
    }

    fun resetState() { registerState = null }
}

