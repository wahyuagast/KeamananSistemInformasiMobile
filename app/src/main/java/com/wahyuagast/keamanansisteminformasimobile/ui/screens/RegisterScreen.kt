package com.wahyuagast.keamanansisteminformasimobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.wahyuagast.keamanansisteminformasimobile.ui.utils.ValidationUtils
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.RegisterViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val state = viewModel.registerState

    var generalError by remember { mutableStateOf<String?>(null) }
    var fullnameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordConfirmationError by remember { mutableStateOf<String?>(null) }
    var nimError by remember { mutableStateOf<String?>(null) }
    var degreeError by remember { mutableStateOf<String?>(null) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }
    var studyProgramIdError by remember { mutableStateOf<String?>(null) }
    var yearError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        when (val s = state) {
            is Resource.Success -> {
                val msg = s.data.message ?: "Registrasi berhasil"
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onRegisterSuccess()
            }
            is Resource.Error -> {
                generalError = s.message
                if (!s.errors.isNullOrEmpty()) {
                    fullnameError = s.errors["fullname"]?.firstOrNull()
                    usernameError = s.errors["username"]?.firstOrNull()
                    emailError = s.errors["email"]?.firstOrNull()
                    passwordError = s.errors["password"]?.firstOrNull()
                }
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
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
            CustomTextField(value = viewModel.fullname, onValueChange = { viewModel.onFullnameChange(it); fullnameError = null }, placeholder = "Nama Lengkap", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = fullnameError)
            CustomTextField(value = viewModel.username, onValueChange = { viewModel.onUsernameChange(it); usernameError = null }, placeholder = "Username", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = usernameError)
            CustomTextField(value = viewModel.email, onValueChange = { viewModel.onEmailChange(it); emailError = null }, placeholder = "Email", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = emailError)
            CustomTextField(value = viewModel.password, onValueChange = { viewModel.onPasswordChange(it); passwordError = null }, placeholder = "Password", isPassword = true, modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = passwordError)
            CustomTextField(value = viewModel.passwordConfirmation, onValueChange = { viewModel.onPasswordConfirmationChange(it); passwordConfirmationError = null }, placeholder = "Konfirmasi Password", isPassword = true, modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = passwordConfirmationError)
            CustomTextField(value = viewModel.nim, onValueChange = { viewModel.onNimChange(it); nimError = null }, placeholder = "NIM", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = nimError)
            CustomTextField(value = viewModel.degree, onValueChange = { viewModel.onDegreeChange(it); degreeError = null }, placeholder = "Gelar (S1/S2...)", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = degreeError)
            CustomTextField(value = viewModel.phoneNumber, onValueChange = { viewModel.onPhoneNumberChange(it); phoneNumberError = null }, placeholder = "No. HP", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = phoneNumberError)
            CustomTextField(value = viewModel.studyProgramId, onValueChange = { viewModel.onStudyProgramIdChange(it); studyProgramIdError = null }, placeholder = "Study Program ID", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = studyProgramIdError)
            CustomTextField(value = viewModel.year, onValueChange = { viewModel.onYearChange(it); yearError = null }, placeholder = "Angkatan (tahun)", modifier = Modifier.fillMaxWidth().padding(4.dp), errorMessage = yearError)
        }

        Spacer(Modifier.height(16.dp))

        if (state is Resource.Loading) {
            CircularProgressIndicator(color = CustomPrimary)
        } else {
            PrimaryButton(text = "Daftar", onClick = {
                var ok = true
                if (ValidationUtils.isFieldEmpty(viewModel.fullname)) {
                    fullnameError = "Nama lengkap tidak boleh kosong"
                    ok = false
                }
                if (ValidationUtils.isFieldEmpty(viewModel.username)) {
                    usernameError = "Username tidak boleh kosong"
                    ok = false
                }
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
                } else if (!ValidationUtils.isPasswordValid(viewModel.password)) {
                    passwordError = "Password minimal 8 karakter, dengan huruf dan angka"
                    ok = false
                }

                if (ValidationUtils.isFieldEmpty(viewModel.passwordConfirmation)) {
                    passwordConfirmationError = "Harap konfirmasi password"
                    ok = false
                } else if (viewModel.password != viewModel.passwordConfirmation) {
                    passwordConfirmationError = "Konfirmasi password tidak cocok"
                    ok = false
                }

                if (ValidationUtils.isFieldEmpty(viewModel.nim)) {
                    nimError = "NIM tidak boleh kosong"
                    ok = false
                }
                if (ValidationUtils.isFieldEmpty(viewModel.degree)) {
                    degreeError = "Gelar tidak boleh kosong"
                    ok = false
                }
                if (ValidationUtils.isFieldEmpty(viewModel.phoneNumber)) {
                    phoneNumberError = "No. HP tidak boleh kosong"
                    ok = false
                }
                if (ValidationUtils.isFieldEmpty(viewModel.studyProgramId)) {
                    studyProgramIdError = "ID Program Studi tidak boleh kosong"
                    ok = false
                }
                if (ValidationUtils.isFieldEmpty(viewModel.year)) {
                    yearError = "Angkatan (tahun) tidak boleh kosong"
                    ok = false
                }
                
                if (ok) viewModel.register()
            })
        }

        Spacer(Modifier.height(12.dp))

        Text(text = "Sudah punya akun? Masuk", color = CustomPrimary, modifier = Modifier.clickable { onNavigateToLogin() }, style = MaterialTheme.typography.bodyMedium)
    }
}
