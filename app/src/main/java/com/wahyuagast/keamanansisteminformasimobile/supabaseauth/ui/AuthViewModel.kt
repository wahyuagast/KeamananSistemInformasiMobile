package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository.AuthRepository
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val repo: AuthRepository): ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onEmailChange(v: String) { uiState = uiState.copy(email = v) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v) }

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val resp = repo.signUp(uiState.email, uiState.password)
                val token = resp.access_token
                token?.let { repo.saveToken(it) }
                uiState = uiState.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.localizedMessage ?: "Signup failed")
            }
        }
    }

    fun login(onSuccess: (ProfileDto?) -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val resp = repo.signIn(uiState.email, uiState.password)
                val token = resp.access_token
                if (token == null) {
                    uiState = uiState.copy(isLoading = false, error = "No access token (confirm email?)")
                    onSuccess(null)
                    return@launch
                }
                repo.saveToken(token)
                // ambil profile via RPC
                val profiles = repo.getMyProfile()
                val profile = profiles.firstOrNull()
                uiState = uiState.copy(isLoading = false)
                onSuccess(profile)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.localizedMessage ?: "Login failed")
                onSuccess(null)
            }
        }
    }

    fun logout(onDone: ()->Unit) {
        viewModelScope.launch {
            repo.clearToken()
            onDone()
        }
    }
}
