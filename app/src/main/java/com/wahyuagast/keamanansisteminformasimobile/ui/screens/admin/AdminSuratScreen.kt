package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.CommonHeader
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AdminSuratScreen(
    onBack: () -> Unit,
    documents: List<DocumentItem>? = null,
    onAction: (id: Int, action: String, comment: String?) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { DocumentRepository() }
    val selectedSuratState = remember { mutableStateOf<DocumentItem?>(null) }
    var activeFilter by remember { mutableStateOf("Semua") }

    // Use provided documents when available, else fallback to a small sample
    val suratList = documents ?: listOf(
        DocumentItem(1, 11, 6, "Form 2C", "Mohon buatkan saya Form 2C", null, "Pending", "2025-12-27T06:01:28.000000Z", "2025-12-27T13:01:27.000000Z", "2025-12-27T13:01:27.000000Z"),
        DocumentItem(2, 11, 6, null, "Mohon buatkan saya Form 2C", null, "Pending", "2025-12-28T19:30:10.000000Z", "2025-12-29T02:30:10.000000Z", "2025-12-29T02:30:10.000000Z")
    )

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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                suratList.forEach { surat ->
                    AdminSuratCard(surat) {
                        selectedSuratState.value = surat
                    }
                }
            }
        }
    }

    if (selectedSuratState.value != null) {
        val selectedSurat = selectedSuratState.value!!
        AdminSuratDetailDialog(
            surat = selectedSurat,
            onDismiss = { selectedSuratState.value = null },
            onAction = { id, action, comment ->
                // perform network action and show toast with result
                scope.launch {
                    val ok = if (action == "approve") repo.approveDocument(id, comment) else repo.rejectDocument(id, comment)
                    if (ok) Toast.makeText(context, "Sukses: $action", Toast.LENGTH_SHORT).show() else Toast.makeText(context, "Gagal: $action", Toast.LENGTH_SHORT).show()
                }
                onAction(id, action, comment)
                selectedSuratState.value = null
            }
        )
    }
}

@Composable
fun AdminSuratCard(surat: DocumentItem, onClick: () -> Unit) {
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
                        Text(text = "ID: ${surat.user_id}", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                        Text(text = formatUploadedAt(surat.uploaded_at), style = MaterialTheme.typography.bodySmall, color = CustomGray)
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
    surat: DocumentItem,
    onDismiss: () -> Unit,
    onAction: (id: Int, action: String, comment: String?) -> Unit
) {
    val context = LocalContext.current
    var comment by remember { mutableStateOf("") }

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
                DetailItem("Uploaded At", formatUploadedAt(surat.uploaded_at))

                Spacer(modifier = Modifier.height(16.dp))
                Text("Komentar", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Tambahkan komentar...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = CustomBackground,
                        focusedBorderColor = CustomPrimary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomBackground, contentColor = CustomBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tutup")
                    }
                    Button(
                        onClick = {
                            onAction(surat.id, "reject", comment.ifBlank { null })
                            Toast.makeText(context, "Ditolak", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomDanger),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tolak")
                    }
                    Button(
                        onClick = {
                            onAction(surat.id, "approve", comment.ifBlank { null })
                            Toast.makeText(context, "Disetujui", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomSuccess),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Setujui")
                    }
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

// New data model matching the API response
data class DocumentItem(
    val id: Int,
    val user_id: Int,
    val document_type_id: Int,
    val name: String?,
    val description: String?,
    val file_path: String?,
    val status: String?,
    val uploaded_at: String?,
    val created_at: String?,
    val updated_at: String?
)

// helper to format ISO offset datetimes gracefully
fun formatUploadedAt(value: String?): String {
    if (value.isNullOrBlank()) return "-"
    return try {
        val odt = OffsetDateTime.parse(value)
        odt.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
    } catch (_: Exception) {
        // fallback: return original
        value
    }
}
