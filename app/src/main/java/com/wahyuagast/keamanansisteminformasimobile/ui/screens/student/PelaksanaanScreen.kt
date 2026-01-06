package com.wahyuagast.keamanansisteminformasimobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun PelaksanaanScreen(
    onBack: () -> Unit,
    viewModel: com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.ImplementationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadImplementation()
    }
    val implState = viewModel.implementationState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        CommonHeader(title = "Pelaksanaan PKL", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (implState) {
                is Resource.Loading -> {
                     Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator(color = CustomPrimary)
                     }
                }
                is Resource.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CustomDanger.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Error: ${implState.message}", color = CustomDanger)
                            Button(onClick = { viewModel.loadImplementation() }) { Text("Retry") }
                        }
                    }
                }
                is Resource.Success -> {
                    val data = implState.data
                    
                    // Status Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF5856D6), Color(0xFF4842C7))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(text = "Status Pelaksanaan", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                                Text(text = "Sedang Berjalan", color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Minggu ke-${data.currentWeek} dari ${data.totalWeeks}", color = Color.White, style = MaterialTheme.typography.bodySmall)
                                    Text(text = "${data.progressTime}%", color = Color.White, style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { data.progressTime / 100f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.2f),
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${data.startDate} - ${data.endDate}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Document List
                    // Mapping forms from response to Document items
                    val forms = listOf(
                        DocumentItem("form3a", "Form 3A - Lembar Bimbingan", data.form3a?.status ?: "empty", data.form3a?.comment ?: ""),
                        DocumentItem("form4b", "Form 4B - Monitoring", data.form4b?.status ?: "empty", data.form4b?.comment ?: ""),
                        DocumentItem("form5a", "Form 5A - Kerjasama Instansi", data.form5a?.status ?: "empty", data.form5a?.comment ?: "")
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        forms.forEach { doc ->
                            PelaksanaanDocCard(doc)
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun PelaksanaanDocCard(doc: DocumentItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF5856D6).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color(0xFF5856D6), // Custom Indigo
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = doc.name, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                        Text(text = "PDF, max 5MB", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    }
                }
                
                when (doc.status) {
                    "uploaded" -> Icon(Icons.Default.CheckCircle, null, tint = CustomSuccess, modifier = Modifier.size(20.dp))
                    "pending" -> Icon(Icons.Default.Schedule, null, tint = CustomWarning, modifier = Modifier.size(20.dp))
                }
            }

            if (doc.comment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CustomBackground, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Status:", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                        Text(text = doc.comment, style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            when (doc.status) {
                "empty" -> {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5856D6))
                    ) {
                        Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Dokumen")
                    }
                }
                "uploaded" -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBackground, contentColor = CustomBlack)
                        ) {
                            Text("Lihat")
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5856D6))
                        ) {
                            Text("Upload Ulang")
                        }
                    }
                }
            }
        }
    }
}
