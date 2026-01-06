package com.wahyuagast.keamanansisteminformasimobile.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.Inter
import com.wahyuagast.keamanansisteminformasimobile.ui.utils.ValidationUtils
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.LoginViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.delay
import kotlin.math.min

@SuppressLint("DefaultLocale")
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

    // ========== Client-side brute-force mitigation state ==========
    // Counts consecutive failed attempts in this app session. For persistence across
    // restarts, move this counter to EncryptedSharedPreferences or DataStore.
    var failedAttempts by rememberSaveable { mutableIntStateOf(0) }

    // Lockout end-time in epoch millis; zero means no lockout.
    var lockoutEndTime by rememberSaveable { mutableLongStateOf(0L) }

    // Exponential backoff parameters (client-side UI only)
    val baseLockoutMs = 15_000L // 15 seconds initial lockout
    val maxLockoutMs = 30 * 60 * 1000L // 30 minutes maximum
    val allowedThreshold = 5 // failures allowed before first lockout

    val isLocked = lockoutEndTime > System.currentTimeMillis()
    var remainingSeconds by remember { mutableLongStateOf(0L) }

    // Update remainingSeconds every second while locked.
    LaunchedEffect(isLocked, lockoutEndTime) {
        if (isLocked) {
            while (lockoutEndTime > System.currentTimeMillis()) {
                val diff = lockoutEndTime - System.currentTimeMillis()
                // ceiling division to show "5s" (for 4.1s) instead of "4s"
                remainingSeconds = (diff + 999) / 1000L
                delay(1000)
            }
            // When lockout expires:
            remainingSeconds = 0L
            lockoutEndTime = 0L
        } else {
            remainingSeconds = 0L
        }
    }
    // ===============================================================

    LaunchedEffect(loginState) {
        when (loginState) {
            is Resource.Success -> {
                // On success reset client-side counters
                failedAttempts = 0
                lockoutEndTime = 0L

                loginState.data.user?.let { onLoginSuccess(it) }
                viewModel.resetState()
            }

            is Resource.Error -> {
                generalError = loginState.message
                if (!loginState.errors.isNullOrEmpty()) {
                    emailError = loginState.errors["email"]?.firstOrNull()
                    passwordError = loginState.errors["password"]?.firstOrNull()
                }

                // Increment failed attempt counter and apply lockout if threshold reached.
                failedAttempts++
                if (failedAttempts >= allowedThreshold) {
                    // exponent grows with how many times the user exceeded the threshold
                    val exponent = (failedAttempts - allowedThreshold).coerceAtMost(30)
                    // duration doubles each step: base * 2^exponent, capped to maxLockoutMs
                    val duration = min(maxLockoutMs, baseLockoutMs * (1L shl exponent))
                    // Set lockout end time (now + duration)
                    lockoutEndTime = System.currentTimeMillis() + duration
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
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = Inter),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Masuk ke akun Anda",
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Inter),
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

        // Show warning when user has failed attempts but is not yet locked
        if (!isLocked && failedAttempts > 0) {
            Text(
                text = "Percobaan login gagal: $failedAttempts. Setelah $allowedThreshold kali gagal, akun akan dikunci sementara.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Stronger warning when user is one attempt away from lockout
        if (!isLocked && failedAttempts >= (allowedThreshold - 1)) {
            Text(
                text = "Peringatan: Satu percobaan lagi akan mengunci akun sementara.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Show lockout info if currently locked (real-time countdown)
        if (isLocked) {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val timeText = if (minutes > 0) String.format(
                "%02dm %02ds",
                minutes,
                seconds
            ) else String.format("%02ds", seconds)

            Text(
                text = "Akun terkunci sementara. Coba lagi dalam $timeText",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (loginState is Resource.Loading) {
            CircularProgressIndicator(color = CustomPrimary)
        } else {
            PrimaryButton(
                text = "Masuk",
                // Disable click while locked to prevent rapid retries.
                onClick = {
                    if (isLocked) {
                        // No-op; UI already shows countdown. Optionally show a toast.
                        Toast.makeText(
                            context,
                            "Akun terkunci sementara. Harap tunggu.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@PrimaryButton
                    }

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
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Inter)
        )
    }
}
