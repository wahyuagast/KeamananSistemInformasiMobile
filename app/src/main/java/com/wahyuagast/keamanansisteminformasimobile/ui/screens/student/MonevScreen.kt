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

@Composable
fun MonevScreen(onBack: () -> Unit) {
    // Mock Documents
    val documents = listOf(
        DocumentItem("form6b", "Form 6B - Monitoring Evaluasi", "uploaded"),
        DocumentItem("form7a", "Form 7A - Laporan Observasi", "pending"),
        DocumentItem("daftarhadir", "Daftar Hadir Observasi", "uploaded"),
        DocumentItem("draft", "Draft Laporan Pengabdian", "empty")
    )

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
            // Schedule Card
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
                        Text(text = "Jadwal Observasi", color = CustomBlack, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(text = "Kamis, 5 Desember 2024", color = CustomBlack, style = MaterialTheme.typography.bodySmall)
                    Text(text = "10:00 - 12:00 WIB", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Lokasi: PT. Tech Nusantara - Jakarta", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                }
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                documents.forEach { doc ->
                    MonevDocCard(doc)
                }
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
