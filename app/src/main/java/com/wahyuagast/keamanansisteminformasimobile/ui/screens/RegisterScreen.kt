package com.wahyuagast.keamanansisteminformasimobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wahyuagast.keamanansisteminformasimobile.ui.components.CustomTextField
import com.wahyuagast.keamanansisteminformasimobile.ui.components.PrimaryButton
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomGray
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPrimary
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.RegisterViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit
) {
    val ctx = LocalContext.current
    val state = viewModel.registerState

    var generalError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        when (val s = state) {
            is Resource.Success -> {
                Toast.makeText(ctx, s.data.message ?: "Registrasi berhasil", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onRegisterSuccess()
            }
            is Resource.Error -> {
                generalError = s.message
            }
            else -> {}
        }
    }

    LaunchedEffect(generalError) {
        generalError?.let { Toast.makeText(ctx, it, Toast.LENGTH_LONG).show() }
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
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(Modifier.height(16.dp))

        Text("Buat akun", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Isi data Anda untuk membuat akun", style = MaterialTheme.typography.bodyMedium, color = CustomGray)

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp)) {
            CustomTextField(value = viewModel.fullname, onValueChange = { viewModel.onFullnameChange(it) }, placeholder = "Nama Lengkap", modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.username, onValueChange = { viewModel.onUsernameChange(it) }, placeholder = "Username", modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.email, onValueChange = { viewModel.onEmailChange(it) }, placeholder = "Email", modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.password, onValueChange = { viewModel.onPasswordChange(it) }, placeholder = "Password", isPassword = true, modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.passwordConfirmation, onValueChange = { viewModel.onPasswordConfirmationChange(it) }, placeholder = "Konfirmasi Password", isPassword = true, modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.nim, onValueChange = { viewModel.onNimChange(it) }, placeholder = "NIM", modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.degree, onValueChange = { viewModel.onDegreeChange(it) }, placeholder = "Gelar (S1/S2/...)", modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.phoneNumber, onValueChange = { viewModel.onPhoneNumberChange(it) }, placeholder = "No. HP", modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.studyProgramId, onValueChange = { viewModel.onStudyProgramIdChange(it) }, placeholder = "Study Program ID", modifier = Modifier.fillMaxWidth().padding(4.dp))
            CustomTextField(value = viewModel.year, onValueChange = { viewModel.onYearChange(it) }, placeholder = "Angkatan (tahun)", modifier = Modifier.fillMaxWidth().padding(4.dp))
        }

        Spacer(Modifier.height(16.dp))

        if (state is Resource.Loading) {
            CircularProgressIndicator(color = CustomPrimary)
        } else {
            PrimaryButton(text = "Daftar", onClick = { viewModel.register() })
        }

        Spacer(Modifier.height(12.dp))

        Text(text = "Sudah punya akun? Masuk", color = CustomPrimary, modifier = Modifier.clickable { /* TODO: navigate back */ }, style = MaterialTheme.typography.bodyMedium)
    }
}

