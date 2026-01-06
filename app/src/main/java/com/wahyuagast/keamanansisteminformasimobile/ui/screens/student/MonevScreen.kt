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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun MonevScreen(
    onBack: () -> Unit,
    viewModel: com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MonevViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadMonev()
    }
    val monevState = viewModel.monevState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        CommonHeader(title = "Monev Observasi PKL", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (monevState) {
                is Resource.Loading -> {
                     Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CustomDanger) }
                }
                is Resource.Error -> {
                    Text("Error: ${monevState.message}", color = CustomDanger, modifier = Modifier.padding(16.dp))
                    Button(onClick = { viewModel.loadMonev() }, modifier = Modifier.padding(start = 16.dp)) { Text("Retry") }
                }
                is Resource.Success -> {
                    val data = monevState.data
                    
                    // Schedule Card (Timeline)
                    if (data.timeline.isNotEmpty()) {
                        data.timeline.forEach { event ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CustomDanger.copy(alpha = 0.1f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CustomDanger.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.DateRange, null, tint = CustomDanger, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = event.title ?: "Jadwal", color = CustomBlack, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    val startDate = event.startDate?.substring(0, 10) ?: "-"
                                    val startTime = if (event.startDate != null && event.startDate.length > 16) event.startDate.substring(11, 16) else ""
                                    val endTime = if (event.endDate != null && event.endDate.length > 16) event.endDate.substring(11, 16) else ""
                                    
                                    Text(text = startDate, color = CustomBlack, style = MaterialTheme.typography.bodySmall)
                                    if (startTime.isNotEmpty()) {
                                        Text(text = "$startTime - $endTime", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                                    }
                                    event.description?.let { desc ->
                                         Text(text = desc, color = CustomGray, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    } else {
                         Text("Belum ada jadwal monev.", color = CustomGray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 16.dp))
                    }

                    // Info Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Catatan Penting", color = CustomBlack, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "• Upload semua dokumen sebelum observasi", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Pastikan draft laporan sudah direvisi", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Siapkan daftar hadir untuk ditandatangani", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Documents
                    val documents = listOf(
                        DocumentItem("form6b", "Form 6B - Monitoring Evaluasi", data.form6b?.status ?: "empty", data.form6b?.comment ?: ""),
                        DocumentItem("form7a", "Form 7A - Laporan Observasi", data.form7a?.status ?: "empty", data.form7a?.comment ?: ""),
                        DocumentItem("daftarhadir", "Daftar Hadir Observasi", data.daftarHadirObservasi?.status ?: "empty", data.daftarHadirObservasi?.comment ?: ""),
                        DocumentItem("draft", "Draft Laporan Pengabdian", data.draftLaporanPengabdian?.status ?: "empty", data.draftLaporanPengabdian?.comment ?: "")
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        documents.forEach { doc ->
                            MonevDocCard(doc)
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun MonevDocCard(doc: DocumentItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(CustomDanger.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = CustomDanger,
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            when (doc.status) {
                "empty" -> {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomDanger)
                    ) {
                        Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Dokumen")
                    }
                }
                "pending" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CustomWarning.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text("Menunggu validasi admin", color = CustomWarning, style = MaterialTheme.typography.bodySmall)
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
                            colors = ButtonDefaults.buttonColors(containerColor = CustomDanger)
                        ) {
                            Text("Upload Ulang")
                        }
                    }
                }
            }
        }
    }
}
