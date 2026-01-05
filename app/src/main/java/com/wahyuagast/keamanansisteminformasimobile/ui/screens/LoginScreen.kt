package com.wahyuagast.keamanansisteminformasimobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wahyuagast.keamanansisteminformasimobile.data.model.User
import com.wahyuagast.keamanansisteminformasimobile.ui.components.CustomTextField
import com.wahyuagast.keamanansisteminformasimobile.ui.components.PrimaryButton
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomGray
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPrimary
import com.wahyuagast.keamanansisteminformasimobile.ui.utils.ValidationUtils
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.LoginViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    val context = LocalContext.current
    val loginState = viewModel.loginState

    var generalError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                state.data.user?.let { onLoginSuccess(it) }
                viewModel.resetState()
            }
            is Resource.Error -> {
                generalError = state.message
                if (!state.errors.isNullOrEmpty()) {
                    emailError = state.errors["email"]?.firstOrNull()
                    passwordError = state.errors["password"]?.firstOrNull()
                }
            }
            is Resource.Loading -> {
                generalError = null
                emailError = null
                passwordError = null
            }
            else -> {}
        }
    }

    LaunchedEffect(generalError) {
        if (generalError != null && emailError == null && passwordError == null) {
            Toast.makeText(context, generalError, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(CustomPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "SIMOPKL",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Masuk ke akun Anda",
            style = MaterialTheme.typography.bodyMedium,
            color = CustomGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp)
        ) {
             CustomTextField(
                value = viewModel.email,
                onValueChange = { 
                    viewModel.onEmailChange(it)
                    emailError = null 
                },
                placeholder = "Email",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                errorMessage = emailError
            )
            CustomTextField(
                value = viewModel.password,
                onValueChange = { 
                    viewModel.onPasswordChange(it)
                    passwordError = null
                },
                placeholder = "Password",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                isPassword = true,
                errorMessage = passwordError
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        if (loginState is Resource.Loading) {
            CircularProgressIndicator(color = CustomPrimary)
        } else {
            PrimaryButton(
                text = "Masuk",
                onClick = {
                    var ok = true
                    if (ValidationUtils.isFieldEmpty(viewModel.email)) {
                        emailError = "Email tidak boleh kosong"
                        ok = false
                    } else if (!ValidationUtils.isEmailValid(viewModel.email)) {
                        emailError = "Format email tidak valid"
                        ok = false
                    }

                    if (ValidationUtils.isFieldEmpty(viewModel.password)) {
                        passwordError = "Password tidak boleh kosong"
                        ok = false
                    }
                    
                    if (ok) viewModel.login()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Buat akun",
            color = CustomPrimary,
            modifier = Modifier.clickable { onNavigateToRegister() },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
