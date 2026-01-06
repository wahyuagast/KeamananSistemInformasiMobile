package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.LoginResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch
import com.wahyuagast.keamanansisteminformasimobile.utils.InputSanitizer
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuditRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = AuthRepository(TokenManager(application))
    private val auditRepository = AuditRepository(application.applicationContext)

    var loginState by mutableStateOf<Resource<LoginResponse>?>(null)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun login() {
        viewModelScope.launch {
            loginState = Resource.Loading

            // Sanitize/normalize input on client side (server must still validate)
            val e = InputSanitizer.sanitizeForApi(email).lowercase()
            val pw = InputSanitizer.sanitizeForApi(password)

            val result = repository.login(e, pw)
            if (result is Resource.Success) {
                result.data.token?.let { token ->
                    // Do not log the token value
                    AppLog.d("LoginViewModel", "Login success, storing token")
                    repository.saveToken(token)
                    // enqueue audit event
                    auditRepository.enqueueEvent(result.data.user?.id?.toString(), "LOGIN_SUCCESS", null, mapOf("email" to e))
                } ?: AppLog.w("LoginViewModel", "Login succeeded but token missing")
            } else if (result is Resource.Error) {
                // Log a generic error for debugging; do not include untrusted server message bodies
                AppLog.e("LoginViewModel", "Login failed: ${result.message}")
                // enqueue audit event for failed login (do not include password)
                auditRepository.enqueueEvent(null, "LOGIN_FAIL", null, mapOf("email" to e, "reason" to (result.message ?: "unknown")))
            }
            loginState = result
        }
    }

    fun resetState() {
        loginState = null
    }
}
