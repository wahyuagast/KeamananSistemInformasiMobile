package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SessionState {
    object Loading : SessionState()
    object Unauthenticated : SessionState()
    data class Authenticated(val roleId: Int) : SessionState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(TokenManager(application))

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val token = repository.getToken()
            if (token.isNullOrBlank()) {
                _sessionState.value = SessionState.Unauthenticated
            } else {
                // Validate token by fetching profile
                val result = repository.getProfile()
                if (result is Resource.Success) {
                    val roleId = result.data.user.roleId
                    _sessionState.value = SessionState.Authenticated(roleId)
                } else {
                    // Token invalid or network error. 
                    // If 401, we should clear token. 
                    // To be safe, if we can't get profile, we force login.
                    repository.clearToken()
                    _sessionState.value = SessionState.Unauthenticated
                }
            }
        }
    }
}
