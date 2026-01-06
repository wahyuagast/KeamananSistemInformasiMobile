package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentDto
import com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.CommonHeader
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AdminSuratScreen(
    onBack: () -> Unit,
    // Optional: if passed from nav graph, but usually we fetch here
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { DocumentRepository() }
    
    // State
    var suratList by remember { mutableStateOf<List<DocumentDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedSurat by remember { mutableStateOf<DocumentDto?>(null) }
    var activeFilter by remember { mutableStateOf("Semua") }

    // Fetch Data
    LaunchedEffect(Unit) {
        isLoading = true
        suratList = repo.getAdminDocuments()
        isLoading = false
    }

    // Refresh function
    fun refreshData() {
        scope.launch {
            isLoading = true
            suratList = repo.getAdminDocuments()
            isLoading = false
        }
    }

    val filteredList = remember(suratList, activeFilter) {
        if (activeFilter == "Semua") suratList
        else suratList.filter { it.status.equals(activeFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        CommonHeader(title = "Kelola Surat", onBack = onBack)

        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            // Filters
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Semua", "Pending", "Approved", "Rejected").forEach { filter ->
                    val selected = activeFilter == filter
                    FilterChip(
                        selected = selected,
                        onClick = { activeFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CustomPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = CustomGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            // List
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CustomPrimary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (filteredList.isEmpty()) {
                        Text("Tidak ada dokumen.", modifier = Modifier.align(Alignment.CenterHorizontally), color = CustomGray)
                    } else {
                        filteredList.forEach { surat ->
                            AdminSuratCard(surat) {
                                selectedSurat = surat
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedSurat != null) {
        AdminSuratDetailDialog(
            surat = selectedSurat!!,
            onDismiss = { selectedSurat = null },
            onApprove = { id, file, comment ->
                scope.launch {
                    val success = repo.approveDocument(id, file, comment)
                    if (success) {
                        Toast.makeText(context, "Dokumen disetujui", Toast.LENGTH_SHORT).show()
                        refreshData()
                    } else {
                        Toast.makeText(context, "Gagal menyetujui dokumen", Toast.LENGTH_SHORT).show()
                    }
                    selectedSurat = null
                }
            },
            onReject = { id, comment ->
                scope.launch {
                    val success = repo.rejectDocument(id, comment)
                    if (success) {
                        Toast.makeText(context, "Dokumen ditolak", Toast.LENGTH_SHORT).show()
                        refreshData()
                    } else {
                        Toast.makeText(context, "Gagal menolak dokumen", Toast.LENGTH_SHORT).show()
                    }
                    selectedSurat = null
                }
            }
        )
    }
}

@Composable
fun AdminSuratCard(surat: DocumentDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
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
                        // show name if available, otherwise description
                        val title = surat.name ?: surat.description ?: "-"
                        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                        Text(text = "ID: ${surat.userId}", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                        Text(text = formatUploadedAt(surat.uploadedAt), style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    }
                }

                when (surat.status?.lowercase()) {
                    "pending" -> StatusLabel("Pending", CustomWarning)
                    "approved" -> StatusLabel("Approved", CustomSuccess)
                    "rejected" -> StatusLabel("Rejected", CustomDanger)
                    else -> StatusLabel(surat.status ?: "Unknown", CustomGray)
                }
            }
        }
    }
}

@Composable
fun StatusLabel(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (text) {
                    "Pending" -> Icons.Default.Schedule
                    "Approved" -> Icons.Default.CheckCircle
                    else -> Icons.Default.Cancel
                },
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, color = color, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun AdminSuratDetailDialog(
    surat: DocumentDto,
    onDismiss: () -> Unit,
    onApprove: (id: Int, file: File, comment: String) -> Unit,
    onReject: (id: Int, comment: String) -> Unit
) {
    val context = LocalContext.current
    var comment by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            // Get filename
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) selectedFileName = c.getString(nameIndex)
                }
            }
            if (selectedFileName == null) selectedFileName = "Selected File"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFFC6C6C8), CircleShape)
                )
                Spacer(modifier = Modifier.height(24.dp))

                DetailItem("Nama / Jenis", surat.name ?: "-")
                DetailItem("Deskripsi", surat.description ?: "-")
                DetailItem("Uploaded At", formatUploadedAt(surat.uploadedAt))

                Spacer(modifier = Modifier.height(16.dp))
                
                // Comment Field
                Text("Komentar Admin", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text(if (selectedFileUri != null) "Note untuk persetujuan..." else "Note untuk penolakan...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = CustomBackground,
                        focusedBorderColor = CustomPrimary
                    )
                )

                // Appprove Section: File Selection
                Spacer(modifier = Modifier.height(16.dp))
                if (surat.status.equals("pending", ignoreCase = true)) {
                    Text("File Balasan (Wajib untuk Approve)", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { launcher.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedFileUri != null) CustomSuccess.copy(alpha=0.1f) else CustomBackground,
                            contentColor = if (selectedFileUri != null) CustomSuccess else CustomBlack
                        ),
                        border = if (selectedFileUri != null) androidx.compose.foundation.BorderStroke(1.dp, CustomSuccess) else null,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ArrowUpward, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(selectedFileName ?: "Pilih Dokumen PDF")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                             if (comment.isBlank()) {
                                 Toast.makeText(context, "Note wajib diisi untuk penolakan", Toast.LENGTH_SHORT).show()
                             } else {
                                onReject(surat.id, comment)
                             }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomDanger),
                        shape = RoundedCornerShape(12.dp),
                        enabled = surat.status.equals("pending", ignoreCase = true)
                    ) {
                        Text("Tolak")
                    }
                    Button(
                        onClick = {
                            if (selectedFileUri != null && comment.isNotBlank()) {
                                // Convert Uri to File
                                try {
                                    val inputStream = context.contentResolver.openInputStream(selectedFileUri!!)
                                    val tempFile = File(context.cacheDir, selectedFileName ?: "temp_surat.pdf")
                                    val outputStream = FileOutputStream(tempFile)
                                    inputStream?.copyTo(outputStream)
                                    inputStream?.close()
                                    outputStream.close()
                                    
                                    onApprove(surat.id, tempFile, comment)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Gagal memproses file", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Pilih file dan isi note untuk menyetujui", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomSuccess),
                        shape = RoundedCornerShape(12.dp),
                        enabled = surat.status.equals("pending", ignoreCase = true)
                    ) {
                        Text("Setujui")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                 Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = CustomGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = CustomGray)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
    }
}

// helper to format ISO offset datetimes gracefully
fun formatUploadedAt(value: String?): String {
    if (value.isNullOrBlank()) return "-"
    return try {
        // Handle potentially different formats if needed, but ISO-8601 is standad
        val odt = OffsetDateTime.parse(value)
        odt.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
    } catch (_: Exception) {
        // fallback: return original or try SimpleDateFormat if needed
        value
    }
}
