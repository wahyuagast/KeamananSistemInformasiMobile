// Kotlin
package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto

@Composable
fun LoginScreen(vm: AuthViewModel, onRegister: () -> Unit, onLoginSuccess: (ProfileDto?) -> Unit) {
    val state = vm.uiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = state.email, onValueChange = vm::onEmailChange, label = { Text("Email") }, singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = vm::onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = { vm.login(onLoginSuccess) }, modifier = Modifier.fillMaxWidth()) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp)) else Text("Login")
        }
        TextButton(onClick = onRegister) { Text("Create account") }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
fun RegisterScreen(vm: AuthViewModel, onBack: () -> Unit) {
    val state = vm.uiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create account", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = state.email, onValueChange = vm::onEmailChange, label = { Text("Email") }, singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = vm::onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = { vm.signUp { onBack() } }, modifier = Modifier.fillMaxWidth()) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp)) else Text("Sign up")
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
fun UserHomeScreen(profile: ProfileDto?, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("User Home", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Hello, ${profile?.full_name ?: profile?.email}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLogout) { Text("Logout") }
    }
}

@Composable
fun AdminHomeScreen(profile: ProfileDto?, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Admin Home", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Hello Admin, ${profile?.full_name ?: profile?.email}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLogout) { Text("Logout") }
    }
}