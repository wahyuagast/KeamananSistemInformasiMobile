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
fun SuratScreen(
    onBack: () -> Unit,
    viewModel: com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.SuratViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showForm by remember { mutableStateOf(false) }
    
    // Initial Load
    LaunchedEffect(Unit) {
        viewModel.loadDocuments()
    }

    val documentListState = viewModel.documentListState
    val documentTypesState = viewModel.documentTypesState
    val submissionState = viewModel.submissionState
    val context = androidx.compose.ui.platform.LocalContext.current

    // Handle Submission Success
    LaunchedEffect(submissionState) {
        if (submissionState is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Success) {
            showForm = false
            viewModel.resetSubmissionState()
            android.widget.Toast.makeText(context, "Pengajuan berhasil!", android.widget.Toast.LENGTH_SHORT).show()
        } else if (submissionState is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Error) {
             android.widget.Toast.makeText(context, submissionState.message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CustomBackground)
        ) {
            // Header
            CommonHeader(title = "Pengajuan Surat", onBack = onBack) {
                Row {
                    // Reload Button
                    IconButton(
                        onClick = { viewModel.loadDocuments() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Gray.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = CustomBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    // Add Button
                    IconButton(
                        onClick = { 
                            viewModel.loadDocumentTypes()
                            showForm = true 
                        },
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
            }

            // List
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                 when (documentListState) {
                    is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CustomPrimary) }
                    }
                    is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Success -> {
                        val documents = documentListState.data.documents
                        if (documents.isEmpty()) {
                             Box(modifier = Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) {
                                Text("Belum ada surat diajukan", color = CustomGray)
                             }
                        } else {
                            documents.forEach { doc ->
                                SuratCard(doc)
                            }
                        }
                    }
                    is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Error -> {
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                 Text("Gagal memuat surat: ${documentListState.message}", color = CustomDanger)
                                 Button(onClick = { viewModel.loadDocuments() }) { Text("Coba Lagi") }
                             }
                         }
                    }

                     else -> {}
                 }
            }
        }
        
        // Modal / Overlay for Form
        if (showForm) {
            val types = if (documentTypesState is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Success) documentTypesState.data.documentTypes else emptyList()
            val isSubmitting = submissionState is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Loading
            val errorMessage = if (submissionState is com.wahyuagast.keamanansisteminformasimobile.utils.Resource.Error) submissionState.message else null

            com.wahyuagast.keamanansisteminformasimobile.ui.components.PengajuanSuratDialog(
                documentTypes = types,
                isLoading = isSubmitting,
                errorMessage = errorMessage,
                onDismiss = { showForm = false },
                onSubmit = { typeId, desc ->
                    viewModel.submitDocument(typeId, desc)
                }
            )
        }
    }
}

@Composable
fun SuratCard(doc: com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentDto) {
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
                    Text(text = doc.name ?: "Dokumen", style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                    val date = if (doc.createdAt != null && doc.createdAt.length >= 10) doc.createdAt.substring(0, 10) else "-"
                    Text(text = date, style = MaterialTheme.typography.bodySmall, color = CustomGray)
                }
            }
            
            StatusBadge(status = doc.status ?: "Unknown")
        }
        
        if (doc.description != null && doc.description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomBackground, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(text = "Keterangan:", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    Text(text = doc.description, style = MaterialTheme.typography.bodySmall, color = CustomBlack)
                }
            }
        }
        
        if (doc.status == "approved" && !doc.filePath.isNullOrEmpty()) {
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
    val (color, text, icon) = when (status.lowercase()) {
        "approved", "completed" -> Triple(CustomSuccess, "Disetujui", Icons.Default.CheckCircle)
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
    // This is just a placeholder, the actual dialog used is PengajuanSuratDialog from components
}

@Composable
fun CommonHeader(title: String, onBack: () -> Unit, action: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
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
            
            Row {
                action()
            }
        }
    }
    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFC6C6C8)))
}
