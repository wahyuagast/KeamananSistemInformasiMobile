package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterDto
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRegistrationScreen(registrationId: Int, onBack: () -> Unit) {
    val repo = remember { DocumentRepository() }
    val context = LocalContext.current
    var register by remember { mutableStateOf<RegisterDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(registrationId) {
        isLoading = true
        try {
            register = repo.getAwardeeRegister(registrationId)
            error = null
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(CustomBackground)) {
        TopAppBar(
            title = { Text("Detail Registrasi", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            if (isLoading) {
                Text("Memuat...", style = MaterialTheme.typography.bodyMedium)
            } else if (error != null) {
                Text("Error: $error", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            } else if (register == null) {
                Text("Registrasi tidak ditemukan", style = MaterialTheme.typography.bodyMedium)
            } else {
                val r = register!!
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(r.fullname ?: "-", style = MaterialTheme.typography.titleMedium)
                        Text(r.nim ?: "-", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Periode: ${r.periode?.name ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                        Text("Mitra: ${r.mitra?.partnerName ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Start: ${r.startDate ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                        Text("End: ${r.endDate ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Status: ${r.status ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onBack() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CustomBackground)) {
                        Text("Tutup", style = MaterialTheme.typography.labelLarge)
                    }
                    Button(onClick = {
                        scope.launch {
                            val updated = repo.rejectAwardeeRegister(registrationId)
                            if (updated != null) {
                                register = updated
                                Toast.makeText(context, "Registrasi ditolak", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Gagal menolak registrasi", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CustomDanger)) {
                        Text("Tolak", style = MaterialTheme.typography.labelLarge)
                    }
                    Button(onClick = {
                        scope.launch {
                            val updated = repo.approveAwardeeRegister(registrationId)
                            if (updated != null) {
                                register = updated
                                Toast.makeText(context, "Registrasi disetujui", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Gagal menyetujui registrasi", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CustomSuccess)) {
                        Text("Setujui", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
