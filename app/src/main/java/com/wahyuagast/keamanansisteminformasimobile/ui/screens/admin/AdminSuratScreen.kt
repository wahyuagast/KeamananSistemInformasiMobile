package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.CommonHeader
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*

@Composable
fun AdminSuratScreen(onBack: () -> Unit) {
    var selectedSurat by remember { mutableStateOf<AdminSuratItem?>(null) }
    var activeFilter by remember { mutableStateOf("Semua") }

    val suratList = listOf(
        AdminSuratItem(1, "Surat 1A", "Budi Santoso", "2021001", "27 Nov 2024", "pending"),
        AdminSuratItem(2, "Surat 2A", "Ani Wijaya", "2021002", "26 Nov 2024", "pending"),
        AdminSuratItem(3, "Surat 2B", "Citra Dewi", "2021003", "25 Nov 2024", "approved")
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
                        selectedSurat = surat
                    }
                }
            }
        }
    }

    if (selectedSurat != null) {
        AdminSuratDetailDialog(
            surat = selectedSurat!!,
            onDismiss = { selectedSurat = null }
        )
    }
}

@Composable
fun AdminSuratCard(surat: AdminSuratItem, onClick: () -> Unit) {
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
                        Text(text = surat.jenis, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                        Text(text = "${surat.mahasiswa} • ${surat.nim}", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                        Text(text = surat.tanggal, style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    }
                }

                when (surat.status) {
                    "pending" -> StatusLabel("Pending", CustomWarning)
                    "approved" -> StatusLabel("Approved", CustomSuccess)
                    "rejected" -> StatusLabel("Rejected", CustomDanger)
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
                imageVector = when(text) {
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
fun AdminSuratDetailDialog(surat: AdminSuratItem, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(40.dp).height(4.dp).background(Color(0xFFC6C6C8), CircleShape)
                )
                Spacer(modifier = Modifier.height(24.dp))

                DetailItem("Jenis Surat", surat.jenis)
                DetailItem("Mahasiswa", "${surat.mahasiswa}\n${surat.nim}")
                DetailItem("Tanggal", surat.tanggal)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Komentar", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Tambahkan komentar...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
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
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomDanger),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tolak")
                    }
                    Button(
                        onClick = onDismiss,
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

data class AdminSuratItem(val id: Int, val jenis: String, val mahasiswa: String, val nim: String, val tanggal: String, val status: String)
