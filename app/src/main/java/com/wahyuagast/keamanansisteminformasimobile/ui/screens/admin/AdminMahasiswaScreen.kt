package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.CommonHeader
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*

@Composable
fun AdminMahasiswaScreen(onBack: () -> Unit) {
    var selectedMhs by remember { mutableStateOf<MahasiswaItem?>(null) }
    var activeFilter by remember { mutableStateOf("Semua") }

    val mahasiswaList = listOf(
        MahasiswaItem(1, "Budi Santoso", "2021001", "Teknik Informatika", "Pelaksanaan", 60),
        MahasiswaItem(2, "Ani Wijaya", "2021002", "Sistem Informasi", "Pendaftaran", 30),
        MahasiswaItem(3, "Citra Dewi", "2021003", "Teknik Informatika", "Ujian", 90),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        CommonHeader(title = "Data Mahasiswa", onBack = onBack) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(36.dp)
                    .background(CustomBackground, CircleShape)
            ) {
                Icon(Icons.Default.Download, null, tint = CustomPrimary, modifier = Modifier.size(20.dp))
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Search (Mock)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Cari mahasiswa...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = CustomGray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = CustomPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Semua", "Pendaftaran", "Pelaksanaan", "Monev", "Ujian", "Selesai").forEach { filter ->
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                mahasiswaList.forEach { mhs ->
                    MahasiswaCard(mhs) {
                        selectedMhs = mhs
                    }
                }
            }
        }
    }

    if (selectedMhs != null) {
        AdminMahasiswaDetailDialog(
            mhs = selectedMhs!!,
            onDismiss = { selectedMhs = null }
        )
    }
}

@Composable
fun MahasiswaCard(mhs: MahasiswaItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(CustomPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White)
                }
                Column {
                    Text(mhs.nama, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                    Text("${mhs.nim} • ${mhs.prodi}", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusPill(mhs.status)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { mhs.progress / 100f },
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = CustomPrimary,
                    trackColor = CustomBackground,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${mhs.progress}%", style = MaterialTheme.typography.bodySmall, color = CustomGray)
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val color = when(status) {
        "Pelaksanaan" -> CustomPrimary
        "Ujian" -> CustomPurple
        "Monev" -> CustomDanger
        "Selesai" -> CustomSuccess
        else -> CustomWarning
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(status, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun AdminMahasiswaDetailDialog(mhs: MahasiswaItem, onDismiss: () -> Unit) {
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

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                    Box(
                        modifier = Modifier.size(64.dp).background(CustomPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(mhs.nama, style = MaterialTheme.typography.titleMedium, color = CustomBlack)
                        Text(mhs.nim, style = MaterialTheme.typography.bodyMedium, color = CustomGray)
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = CustomBackground), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Prodi", style = MaterialTheme.typography.labelSmall, color = CustomGray)
                        Text(mhs.prodi, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = CustomBackground), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Status PKL", style = MaterialTheme.typography.labelSmall, color = CustomGray)
                        Text(mhs.status, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                    }
                }

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
                        colors = ButtonDefaults.buttonColors(containerColor = CustomPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lihat Detail")
                    }
                }
            }
        }
    }
}

data class MahasiswaItem(val id: Int, val nama: String, val nim: String, val prodi: String, val status: String, val progress: Int)
