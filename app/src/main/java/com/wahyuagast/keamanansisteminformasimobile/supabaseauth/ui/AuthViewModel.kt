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
                val response = repo.signUp(uiState.email, uiState.password)
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.access_token
                    token?.let { repo.saveToken(it) }
                    uiState = uiState.copy(isLoading = false)
                    onSuccess()
                } else {
                    val msg = response.errorBody()?.string() ?: "Signup failed (status ${response.code()})"
                    uiState = uiState.copy(isLoading = false, error = msg)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.localizedMessage ?: "Signup failed")
            }
        }
    }

    fun login(onSuccess: (ProfileDto?) -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = repo.signIn(uiState.email, uiState.password)
                if (!response.isSuccessful) {
                    val msg = response.errorBody()?.string() ?: "Login failed (status ${response.code()})"
                    uiState = uiState.copy(isLoading = false, error = msg)
                    onSuccess(null)
                    return@launch
                }
                val respBody = response.body()
                val token = respBody?.access_token
                if (token == null) {
                    uiState = uiState.copy(isLoading = false, error = "No access token (maybe confirm email?)")
                    onSuccess(null)
                    return@launch
                }
                repo.saveToken(token)
                // now call RPC get_my_profile
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

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // try to logout on server; ignore failure but log it
                try {
                    repo.logoutFromServer()
                } catch (_: Exception) { /* ignore network failures */ }

                repo.clearToken()
            } catch (e: Exception) {
                // still clear local token on error
                try { repo.clearToken() } catch (_: Exception) {}
            } finally {
                uiState = uiState.copy(isLoading = false)
                onDone()
            }
        }
    }
}
