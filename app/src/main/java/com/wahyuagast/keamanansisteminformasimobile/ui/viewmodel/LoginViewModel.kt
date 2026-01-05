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

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = AuthRepository(TokenManager(application))

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
            val result = repository.login(email, password)
            if (result is Resource.Success) {
                result.data.token?.let { token ->
                    android.util.Log.d("LoginViewModel", "Login success, saving token...")
                    repository.saveToken(token)
                } ?: android.util.Log.w("LoginViewModel", "Login success but token is null")
            } else if (result is Resource.Error) {
                android.util.Log.e("LoginViewModel", "Login failed: ${result.message}")
            }
            loginState = result
        }
    }

    fun resetState() {
        loginState = null
    }
}
