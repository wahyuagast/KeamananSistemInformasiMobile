package com.wahyuagast.keamanansisteminformasimobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.wahyuagast.keamanansisteminformasimobile.utils.AppLog

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

    val isLoading = state is Resource.Loading

    LaunchedEffect(state) {
        when (val s = state) {
            is Resource.Success -> {
                val msg = s.data.message ?: "Registrasi berhasil"
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onRegisterSuccess()
            }

            is Resource.Error -> {
                // Hide raw JSON parsing / stack messages from users. Show a friendly message instead
                val raw = s.message ?: ""
                if (raw.contains("json", ignoreCase = true) && raw.contains("unexpected", ignoreCase = true)) {
                    // Try to extract an offset or position from messages like:
                    // "Unexpected JSON token at offset 123" or "unexpected character (offset: 123)"
                    val offsetRegex = Regex("(?:offset[:\\s]*)(\\d+)", RegexOption.IGNORE_CASE)
                    val match = offsetRegex.find(raw)
                    val offsetPart = match?.groups?.get(1)?.value

                    generalError = if (offsetPart != null) {
                        "Terjadi kesalahan pada respons server (format tidak sesuai) di posisi sekitar $offsetPart. Isi respons tidak ditampilkan untuk keamanan."
                    } else {
                        "Terjadi kesalahan pada respons server (format tidak sesuai). Silakan coba lagi nanti."
                    }

                    // Log the raw error for debugging (not shown to user)
                    AppLog.e("RegisterScreen", "Server response parse error: $raw")
                } else {
                    generalError = raw
                }
                // map field errors if provided
                if (!s.errors.isNullOrEmpty()) {
                    fullnameError = s.errors["fullname"]?.firstOrNull()
                    usernameError = s.errors["username"]?.firstOrNull()
                    emailError = s.errors["email"]?.firstOrNull()
                    passwordError = s.errors["password"]?.firstOrNull()
                    // also handle other possible keys
                    nimError = s.errors["nim"]?.firstOrNull() ?: nimError
                    studyProgramIdError =
                        s.errors["studyProgramId"]?.firstOrNull() ?: studyProgramIdError
                }
            }

            else -> {}
        }
    }

    // Show toast for general errors but keep the message visible on screen as well
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
            Icon(
                Icons.Default.Person,
                contentDescription = "avatar",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Buat akun",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Isi data Anda untuk membuat akun",
            style = MaterialTheme.typography.bodyMedium,
            color = CustomGray
        )

        Spacer(Modifier.height(24.dp))

        // general/server error block (persistent on screen)
        if (!generalError.isNullOrEmpty()) {
            Text(
                text = generalError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            CustomTextField(
                value = viewModel.fullname,
                onValueChange = {
                    viewModel.onFullnameChange(it); fullnameError = null; generalError = null
                },
                placeholder = "Nama Lengkap",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = fullnameError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.username,
                onValueChange = {
                    viewModel.onUsernameChange(it); usernameError = null; generalError = null
                },
                placeholder = "Username",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = usernameError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.email,
                onValueChange = {
                    viewModel.onEmailChange(it); emailError = null; generalError = null
                },
                placeholder = "Email",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = emailError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.onPasswordChange(it); passwordError = null; generalError = null
                },
                placeholder = "Password",
                isPassword = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = passwordError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.passwordConfirmation,
                onValueChange = {
                    viewModel.onPasswordConfirmationChange(it); passwordConfirmationError =
                    null; generalError = null
                },
                placeholder = "Konfirmasi Password",
                isPassword = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = passwordConfirmationError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.nim,
                onValueChange = { viewModel.onNimChange(it); nimError = null; generalError = null },
                placeholder = "NIM",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = nimError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.degree,
                onValueChange = {
                    viewModel.onDegreeChange(it); degreeError = null; generalError = null
                },
                placeholder = "Gelar (S1/S2...)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = degreeError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.phoneNumber,
                onValueChange = {
                    viewModel.onPhoneNumberChange(it); phoneNumberError = null; generalError = null
                },
                placeholder = "No. HP",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = phoneNumberError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.studyProgramId,
                onValueChange = {
                    viewModel.onStudyProgramIdChange(it); studyProgramIdError = null; generalError =
                    null
                },
                placeholder = "Study Program ID",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = studyProgramIdError,
                enabled = !isLoading
            )
            CustomTextField(
                value = viewModel.year,
                onValueChange = {
                    viewModel.onYearChange(it); yearError = null; generalError = null
                },
                placeholder = "Angkatan (tahun)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                errorMessage = yearError,
                enabled = !isLoading
            )
        }

        Spacer(Modifier.height(16.dp))

        // show progress above the button when loading
        if (isLoading) {
            CircularProgressIndicator(color = CustomPrimary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        PrimaryButton(text = "Daftar", onClick = {
            // clear previous general/server error
            generalError = null

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

            if (ok) {
                viewModel.register()
            } else {
                Toast.makeText(ctx, "Mohon periksa kembali inputan Anda", Toast.LENGTH_SHORT).show()
            }
        }, enabled = !isLoading)

        Spacer(Modifier.height(12.dp))

        // show a small helper / error note below the button
        if (!generalError.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Terjadi kesalahan: ${generalError}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "Sudah punya akun? Masuk",
            color = CustomPrimary,
            modifier = Modifier.clickable { onNavigateToLogin() },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
