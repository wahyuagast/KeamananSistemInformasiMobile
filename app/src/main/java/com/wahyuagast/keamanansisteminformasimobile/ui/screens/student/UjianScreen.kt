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

@Composable
fun UjianScreen(onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf("draft") }

    val draftDocs = listOf(
        DocumentItem("jurnal", "Draft Jurnal Jupita", "uploaded"),
        DocumentItem("laporan", "Draft Laporan Pengabdian", "pending"),
        DocumentItem("berita-acara", "Permohonan Berita Acara", "empty")
    )
    
    val finalDocs = listOf(
        DocumentItem("revisi", "Form Revisi Penguji", "empty"),
        DocumentItem("nilai", "Nilai Ujian PKL", "empty"),
        DocumentItem("jurnal-final", "Jurnal Final", "empty"),
        DocumentItem("laporan-final", "Laporan Akhir", "empty")
    )

    val docs = if (activeTab == "draft") draftDocs else finalDocs
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

            // Status Info
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

            // Documents
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                docs.forEach { doc ->
                    UjianDocCard(doc, primaryColor)
                }
            }

            if (activeTab == "final") {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nilai Akhir PKL", color = CustomBlack, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.size(80.dp).background(CustomSuccess, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sangat Baik", color = CustomGray, style = MaterialTheme.typography.bodySmall)
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
                    }
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                "pending" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CustomWarning.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text("Menunggu verifikasi admin", color = CustomWarning, style = MaterialTheme.typography.bodySmall)
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
