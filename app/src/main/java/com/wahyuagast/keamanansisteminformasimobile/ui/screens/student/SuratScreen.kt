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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wahyuagast.keamanansisteminformasimobile.ui.components.PrimaryButton
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*

@Composable
fun SuratScreen(onBack: () -> Unit) {
    var showForm by remember { mutableStateOf(false) }
    
    // Mock Data
    val mockSurat = listOf(
        SuratItem(1, "Surat 1A", "approved", "20 Nov 2024", ""),
        SuratItem(2, "Surat 2A", "pending", "22 Nov 2024", ""),
        SuratItem(3, "Surat 2B", "rejected", "18 Nov 2024", "Format tanda tangan tidak sesuai")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CustomBackground)
        ) {
            // Header
            CommonHeader(title = "Pengajuan Surat", onBack = onBack) {
                // Add Button
                 IconButton(
                    onClick = { showForm = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CustomPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajukan Surat",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // List
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                mockSurat.forEach { surat ->
                    SuratCard(surat)
                }
            }
        }
        
        // Modal / Overlay for Form
        if (showForm) {
            AjukanSuratDialog(onDismiss = { showForm = false })
        }
    }
}

@Composable
fun SuratCard(surat: SuratItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(CustomPrimary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = CustomPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = surat.jenis, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                    Text(text = surat.tanggal, style = MaterialTheme.typography.bodySmall, color = CustomGray)
                }
            }
            
            StatusBadge(status = surat.status)
        }
        
        if (surat.komentar.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomBackground, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(text = "Komentar:", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    Text(text = surat.komentar, style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                }
            }
        }
        
        if (surat.status == "approved") {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomPrimary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unduh Surat", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text, icon) = when (status) {
        "approved" -> Triple(CustomSuccess, "Disetujui", Icons.Default.CheckCircle)
        "pending" -> Triple(CustomWarning, "Pending", Icons.Default.Schedule)
        "rejected" -> Triple(CustomDanger, "Ditolak", Icons.Default.Cancel)
        else -> Triple(CustomGray, status, Icons.Default.Info)
    }
    
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = color, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp))
    }
}

@Composable
fun AjukanSuratDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
             modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFFC6C6C8), CircleShape).padding(bottom = 16.dp)
                )
                Text("Ajukan Surat Baru", style = MaterialTheme.typography.titleMedium, color = CustomBlack)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Form Fields (Simple placeholder for select/text)
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Pilih Jenis Surat") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true, // Simulate select
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Jelaskan keperluan surat...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomBackground, contentColor = CustomBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }
                    Button(
                        onClick = { 
                            // Submit logic
                            onDismiss() 
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ajukan")
                    }
                }
            }
        }
    }
}

@Composable
fun CommonHeader(title: String, onBack: () -> Unit, action: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .background(CustomBackground, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = CustomPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = CustomBlack
            )
            
            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                action()
            }
        }
    }
    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFC6C6C8)))
}

data class SuratItem(val id: Int, val jenis: String, val status: String, val tanggal: String, val komentar: String)
