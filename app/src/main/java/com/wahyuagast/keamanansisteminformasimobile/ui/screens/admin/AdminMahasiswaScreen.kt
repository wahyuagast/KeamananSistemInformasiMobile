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
import com.wahyuagast.keamanansisteminformasimobile.data.model.AwardeeDto
import kotlinx.coroutines.launch

@Composable
fun AdminMahasiswaScreen(onBack: () -> Unit) {
    var selectedMhs by remember { mutableStateOf<MahasiswaItem?>(null) }
    var activeFilter by remember { mutableStateOf("Semua") }
    var search by remember { mutableStateOf("") }

    val context = LocalContext.current
    val repo = remember { DocumentRepository() }
    var mahasiswaList by remember { mutableStateOf<List<MahasiswaItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // loader function so we can call it from LaunchedEffect and refresh button
    suspend fun loadAwardees() {
        isLoading = true
        try {
            val (count, list) = repo.getAwardees()
            mahasiswaList = list.map { it.toMahasiswaItem() }
            error = null
            Toast.makeText(context, "Loaded $count awardees", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            val msg = e.message ?: "Gagal memuat data"
            error = msg
            Toast.makeText(context, "Gagal memuat awardees: $msg", Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    // initial load
    LaunchedEffect(Unit) { loadAwardees() }

    // filter + search
    val filtered = mahasiswaList.filter { m ->
        val matchesFilter = when (activeFilter) {
            "Semua" -> true
            "Pendaftaran" -> m.status.lowercase().contains("daftar") || m.status.lowercase().contains("pendaftaran")
            "Pelaksanaan" -> m.status.lowercase().contains("pelaksanaan") || m.status.lowercase().contains("pelaksanaan")
            "Monev" -> m.status.lowercase().contains("monev")
            "Ujian" -> m.status.lowercase().contains("ujian")
            "Selesai" -> m.status.lowercase().contains("selesai")
            else -> true
        }
        val matchesSearch = search.isBlank() || m.nama.contains(search, ignoreCase = true) || m.nim.contains(search, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        CommonHeader(title = "Data Mahasiswa", onBack = onBack) {
            IconButton(
                onClick = { scope.launch { loadAwardees() } },
                modifier = Modifier
                    .size(36.dp)
                    .background(CustomBackground, CircleShape)
            ) {
                Icon(Icons.Default.Refresh, null, tint = CustomPrimary, modifier = Modifier.size(20.dp))
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Search
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
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
                if (isLoading) {
                    Text("Memuat...")
                } else if (error != null) {
                    Text("Error: $error")
                } else if (filtered.isEmpty()) {
                    Text("Tidak ada mahasiswa sesuai filter")
                } else {
                    filtered.forEach { mhs ->
                        MahasiswaCard(mhs) {
                            selectedMhs = mhs
                        }
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
    val repo = remember { com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository() }
    val context = LocalContext.current
    var detail by remember { mutableStateOf<com.wahyuagast.keamanansisteminformasimobile.data.model.AwardeeDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(mhs.id) {
        isLoading = true
        detail = try { repo.getAwardeeDetail(mhs.id) } catch (_: Exception) { null }
        isLoading = false
    }

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

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    // header
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                        Box(
                            modifier = Modifier.size(64.dp).background(CustomPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(detail?.fullname ?: mhs.nama, style = MaterialTheme.typography.titleMedium, color = CustomBlack)
                            Text(detail?.nim ?: mhs.nim, style = MaterialTheme.typography.bodyMedium, color = CustomGray)
                        }
                    }

                    // info cards
                    Card(colors = CardDefaults.cardColors(containerColor = CustomBackground), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Prodi", style = MaterialTheme.typography.labelSmall, color = CustomGray)
                            Text(detail?.prodi ?: mhs.prodi, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                        }
                    }

                    Card(colors = CardDefaults.cardColors(containerColor = CustomBackground), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Status PKL", style = MaterialTheme.typography.labelSmall, color = CustomGray)
                            Text(detail?.status ?: mhs.status, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                        }
                    }

                    // additional read-only fields from detail if available
                    detail?.startDate?.let { sd ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Start Date", style = MaterialTheme.typography.labelSmall, color = CustomGray)
                        Text(sd, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                    }

                    detail?.endDate?.let { ed ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("End Date", style = MaterialTheme.typography.labelSmall, color = CustomGray)
                        Text(ed, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBackground, contentColor = CustomBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Tutup")
                        }
                    }
                }
            }
        }
    }
}

// helper mapping
private fun AwardeeDto.toMahasiswaItem(): MahasiswaItem {
    return MahasiswaItem(
        id = this.awardeeId,
        nama = this.fullname,
        nim = this.nim,
        prodi = this.prodi ?: "-",
        status = this.status?.replace('_', ' ')?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "-",
        progress = this.progress ?: 0
    )
}

data class MahasiswaItem(val id: Int, val nama: String, val nim: String, val prodi: String, val status: String, val progress: Int)
