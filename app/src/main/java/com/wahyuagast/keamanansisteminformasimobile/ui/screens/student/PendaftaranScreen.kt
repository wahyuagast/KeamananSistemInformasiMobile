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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*

@Composable
fun PendaftaranScreen(onBack: () -> Unit) {
    var showGuide by remember { mutableStateOf(false) }
    var isFormFilled by remember { mutableStateOf(false) } // State to track if form is filled

    // Mock Data State
    var documents by remember { mutableStateOf(listOf(
        DocumentItem("form2a", "Form 2A", "empty"),
        DocumentItem("form2b", "Form 2B", "empty"),
        DocumentItem("transkrip", "Transkrip Nilai", "empty"),
        DocumentItem("suratTerima", "Surat Keterangan Diterima", "empty")
    )) }

    val progress = if (isFormFilled) 50 else 0 // Mock progress

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        // Header
        CommonHeader(title = "Pendaftaran PKL", onBack = onBack) {
            IconButton(
                onClick = { showGuide = !showGuide },
                modifier = Modifier
                    .size(36.dp)
                    .background(CustomBackground, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Guide",
                    tint = CustomPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Guide
            if (showGuide) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CustomPrimary.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CustomPrimary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Panduan Pendaftaran", color = CustomBlack, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "1. Isi Form Pendaftaran Online terlebih dahulu", style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                        Text(text = "2. Upload Form 2A (Formulir Pendaftaran)", style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                        Text(text = "3. Upload Form 2B (Biodata Mahasiswa)", style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                        Text(text = "4. Upload Transkrip Nilai terbaru", style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                        Text(text = "5. Upload Surat Keterangan Diterima", style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                        Text(text = "6. Tunggu verifikasi dari admin", style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                    }
                }
            }

            // Form Online Button (Moved to the top)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CustomSuccess.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = CustomSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Form Pendaftaran Online", color = CustomBlack, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Isi data PKL Anda terlebih dahulu", color = CustomGray, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Button(
                        onClick = { isFormFilled = true }, // Set state to true on click
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isFormFilled, // Disable button after it's clicked
                        colors = ButtonDefaults.buttonColors(containerColor = CustomSuccess)
                    ) {
                        if(isFormFilled) {
                            Icon(Icons.Default.Check, "Form Filled")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Form Sudah Diisi")
                        } else {
                            Text("Isi Form")
                        }
                    }
                }
            }

            // Show progress and documents only after form is filled
            if (isFormFilled) {
                // Progress
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Progress Pendaftaran", color = CustomBlack, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "$progress%", color = CustomPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = CustomPrimary,
                            trackColor = CustomBackground,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        )
                    }
                }

                // Documents
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    documents.forEach { doc ->
                        DocumentCard(
                            doc = doc,
                            onUpload = {
                                // Mock upload
                                documents = documents.map {
                                    if (it.id == doc.id) it.copy(status = "pending") else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentCard(doc: DocumentItem, onUpload: () -> Unit) {
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
                            .background(CustomPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = CustomPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = doc.name, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                        Text(text = "PDF, max 2MB", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    }
                }

                // Status Icon
                when (doc.status) {
                    "uploaded" -> Icon(Icons.Default.CheckCircle, null, tint = CustomSuccess, modifier = Modifier.size(20.dp))
                    "pending" -> Icon(Icons.Default.Schedule, null, tint = CustomWarning, modifier = Modifier.size(20.dp))
                    else -> Icon(Icons.Default.Upload, null, tint = CustomPrimary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (doc.status) {
                "empty" -> {
                    Button(
                        onClick = onUpload,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomPrimary)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CustomSuccess.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text("Dokumen terverifikasi", color = CustomSuccess, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

data class DocumentItem(val id: String, val name: String, val status: String, val comment: String = "")
