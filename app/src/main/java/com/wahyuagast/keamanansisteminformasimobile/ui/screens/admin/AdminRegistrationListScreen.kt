package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.data.model.AwardeeDto
import com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.CommonHeader
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminRegistrationListScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val repo = remember { DocumentRepository() }
    val scope = rememberCoroutineScope()
    var awardees by remember { mutableStateOf<List<AwardeeDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val (_, list) = repo.getAwardees()
            awardees = list
        } catch (_: Exception) {
            // handle error
        } finally {
            isLoading = false
        }
    }

    val filteredList = awardees.filter {
        it.fullname.contains(searchQuery, ignoreCase = true) ||
        it.nim.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        CommonHeader(title = "Data Pendaftaran", onBack = onBack)

        Column(modifier = Modifier.padding(16.dp)) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari Mahasiswa / NIM") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = CustomGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CustomPrimary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { awardee ->
                        AwardeeCard(awardee) {
                            if (awardee.registerId != null) {
                                onNavigateToDetail(awardee.registerId)
                            }
                        }
                    }
                    if (filteredList.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada data", color = CustomGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AwardeeCard(awardee: AwardeeDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(CustomPrimary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = CustomPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(awardee.fullname, style = MaterialTheme.typography.titleSmall, color = CustomBlack)
                Text(awardee.nim, style = MaterialTheme.typography.bodySmall, color = CustomGray)
                if (awardee.prodi != null) {
                    Text(awardee.prodi, style = MaterialTheme.typography.bodySmall, color = CustomGray)
                }
            }
            if (awardee.status != null) {
                StatusLabel(
                    text = awardee.status,
                    color = when(awardee.status.lowercase()) {
                        "diterima" -> CustomSuccess
                        "ditolak" -> CustomDanger
                        "menunggu" -> CustomWarning
                        else -> CustomGray
                    }
                )
            }
        }
    }
}
