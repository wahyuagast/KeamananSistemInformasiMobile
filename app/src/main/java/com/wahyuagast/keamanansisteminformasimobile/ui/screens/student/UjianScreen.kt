package com.wahyuagast.keamanansisteminformasimobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun UjianScreen(
    onBack: () -> Unit,
    viewModel: com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.ExamViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var activeTab by remember { mutableStateOf("draft") }

    LaunchedEffect(Unit) {
        viewModel.loadDraft()
        viewModel.loadFinal()
    }

    val draftState = viewModel.draftState
    val finalState = viewModel.finalState

    val primaryColor = if (activeTab == "draft") CustomPurple else CustomSuccess

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        CommonHeader(title = "Ujian PKL", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Tabs
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    TabButton(
                        text = "Berkas Draft",
                        isActive = activeTab == "draft",
                        onClick = { activeTab = "draft" }
                    )
                    TabButton(
                        text = "Berkas Final",
                        isActive = activeTab == "final",
                        onClick = { activeTab = "final" }
                    )
                }
            }

            if (activeTab == "draft") {
                if (draftState is Resource.Loading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CustomPurple) }
                } else if (draftState is Resource.Success) {
                    val data = draftState.data
                     // Mapping draft data
                    val draftDocs = listOf(
                        DocumentItem("jurnal", "Draft Jurnal Jupita", data.draftJurnalJupita?.status ?: "empty", data.draftJurnalJupita?.comment ?: "Belum ada data"),
                        DocumentItem("laporan", "Draft Laporan Pengabdian", data.draftLaporanPengabdian?.status ?: "empty", data.draftLaporanPengabdian?.comment ?: "Belum ada data"),
                        DocumentItem("berita-acara", "Permohonan Berita Acara", data.permohonanBeritaAcara?.status ?: "empty", data.permohonanBeritaAcara?.comment ?: "Belum ada data")
                    )
                    
                    StatusCard(activeTab, primaryColor)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        draftDocs.forEach { doc -> UjianDocCard(doc, primaryColor) }
                    }
                } else if (draftState is Resource.Error) {
                     Text("Error loading draft: ${draftState.message}", color = CustomDanger)
                }
            } else {
                if (finalState is Resource.Loading) {
                     Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CustomSuccess) }
                } else if (finalState is Resource.Success) {
                    val data = finalState.data
                    val finalDocs = listOf(
                        DocumentItem("revisi", "Form Revisi Penguji", data.formRevisiPenguji?.status ?: "empty", data.formRevisiPenguji?.comment ?: "Belum ada data"),
                        DocumentItem("nilai", "Nilai Ujian PKL", data.nilaiUjianPkl?.status ?: "empty", data.nilaiUjianPkl?.comment ?: "Belum ada data"),
                        DocumentItem("jurnal-final", "Jurnal Final", data.jurnalFinal?.status ?: "empty", data.jurnalFinal?.comment ?: "Belum ada data"),
                        DocumentItem("laporan-final", "Laporan Akhir", data.laporanAkhir?.status ?: "empty", data.laporanAkhir?.comment ?: "Belum ada data")
                    )

                    StatusCard(activeTab, primaryColor)

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        finalDocs.forEach { doc -> UjianDocCard(doc, primaryColor) }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Nilai Akhir PKL", color = CustomBlack, style = MaterialTheme.typography.bodyMedium)
                            
                            val nilaiAkhir = data.nilaiAkhir ?: "Belum ada data"
                            val nilaiHuruf = data.nilaiHuruf ?: "-"
                                
                            if (data.nilaiAkhir != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier.size(80.dp).background(CustomSuccess, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(nilaiHuruf, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Nilai Akhir: $nilaiAkhir", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = CustomPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Unduh Nilai Akhir")
                                }
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Nilai belum keluar", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else if (finalState is Resource.Error) {
                    Text("Error loading final: ${finalState.message}", color = CustomDanger)
                }
            }
        }
    }
}

@Composable
fun StatusCard(activeTab: String, primaryColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val title = if (activeTab == "draft") "Status: Persiapan Ujian" else "Ujian Selesai"
            val desc = if (activeTab == "draft") "Upload semua berkas draft untuk mengajukan ujian PKL" else "Upload berkas final dan revisi dari penguji"
            
            Text(text = title, color = CustomBlack, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = desc, color = CustomGray, style = MaterialTheme.typography.bodySmall)
            
            if (activeTab == "final") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, null, tint = CustomPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unduh Berita Acara Ujian", color = CustomPrimary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun RowScope.TabButton(text: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .background(if (isActive) CustomPrimary else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isActive) Color.White else CustomGray,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun UjianDocCard(doc: DocumentItem, color: Color) {
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
                            .background(color.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = doc.name, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                        Text(text = "PDF, max 10MB", style = MaterialTheme.typography.bodySmall, color = CustomGray)
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
                   Text(text = doc.comment, style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            when (doc.status) {
                "empty" -> {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color)
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
                            colors = ButtonDefaults.buttonColors(containerColor = color)
                        ) {
                            Text("Upload Ulang")
                        }
                    }
                }
            }
        }
    }
}
