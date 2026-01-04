package com.wahyuagast.keamanansisteminformasimobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MahasiswaProfileViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun MahasiswaDashboardScreen(
    viewModel: MahasiswaProfileViewModel = viewModel(),
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) { 
        viewModel.loadProfile()
        viewModel.loadMitras()
    }
    val state = viewModel.profileState

    var userName = "Mahasiswa"
    if (state is Resource.Success) {
        val awardee = state.data.user.awardee
        userName = awardee?.fullname ?: state.data.user.email
    }
    
    // Handle Submission Success (Moved to SuratScreen)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(top = 24.dp) // Extra top padding
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(CustomPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Halo,",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomGray
                        )
                        if (state is Resource.Loading) {
                             Box(modifier = Modifier.width(100.dp).height(20.dp).background(CustomGray.copy(alpha=0.2f), RoundedCornerShape(4.dp)))
                        } else {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = CustomBlack
                            )
                        }
                    }
                }
                
                IconButton(
                    onClick = {
                        viewModel.logout(onLogout)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CustomBackground, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = CustomGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFC6C6C8)))

        // Content Scrollable Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(CustomPrimary, Color(0xFF0051D5))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Status PKL",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Pelaksanaan",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = "60%", color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(8.dp)
                                .background(Color.White, CircleShape)
                            )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timeline
            DashboardCard(
                title = "Deadline Terdekat",
                icon = Icons.Default.AccessTime,
                iconTint = CustomPrimary
            ) {
                 TimelineItem(
                    title = "Upload Form 3A",
                    subtitle = "2 hari lagi",
                    color = CustomDanger,
                    isLast = false
                 )
                 TimelineItem(
                    title = "Monev Observasi",
                    subtitle = "7 hari lagi",
                    color = CustomWarning,
                    isLast = true
                 )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Referensi
            DashboardCard(
                title = "Referensi Tempat PKL",
                icon = Icons.Default.Place,
                iconTint = CustomPrimary
            ) {
                val mitraState = viewModel.mitraState
                
                when (mitraState) {
                    is Resource.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CustomPrimary)
                        }
                    }
                    is Resource.Success -> {
                        val mitras = mitraState.data.mitra
                        if (mitras.isEmpty()) {
                             Text("Belum ada data mitra", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                        } else {
                            mitras.take(5).forEachIndexed { index, mitra ->
                                ReferenceItem(
                                    name = mitra.partnerName,
                                    details = "${mitra.address} • ${mitra.email}"
                                )
                                if (index < mitras.size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        Text(text = "Gagal memuat: ${mitraState.message}", style = MaterialTheme.typography.bodySmall, color = CustomDanger)
                    }
                    else -> {}
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Menu Grid
            val menuItems = listOf(
                MenuItem("Profil", Icons.Default.Person, CustomPrimary, "profile"),
                MenuItem("Pengajuan Surat", Icons.Default.Description, CustomSuccess, "surat"),
                MenuItem("Pendaftaran PKL", Icons.Default.PersonAdd, CustomWarning, "pendaftaran"),
                MenuItem("Pelaksanaan", Icons.Default.Work, CustomIndigo, "pelaksanaan"),
                MenuItem("Monev PKL", Icons.Default.Assignment, CustomDanger2, "monev"),
                MenuItem("Ujian PKL", Icons.Default.Book, CustomPurple, "ujian")
            )
            
            // Chunked by 2
            menuItems.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { item ->
                        MenuButton(
                            item = item,
                            onClick = { 
                                onNavigate(item.route)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// Sub-components for Dashboard

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold, color = CustomBlack)
        }
        content()
    }
}

@Composable
fun TimelineItem(title: String, subtitle: String, color: Color, isLast: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 12.dp)
    ) {
         Box(
             modifier = Modifier.padding(top = 6.dp).size(8.dp).background(color, CircleShape)
         )
         Spacer(modifier = Modifier.width(12.dp))
         Column(modifier = Modifier.weight(1f)) {
             Text(text = title, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
             Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = CustomGray)
             if (!isLast) {
                 Spacer(modifier = Modifier.height(12.dp))
                 Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CustomBackground))
             }
         }
    }
}

@Composable
fun ReferenceItem(name: String, details: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CustomBackground, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyMedium, color = CustomBlack, fontWeight = FontWeight.Medium)
        Text(text = details, style = MaterialTheme.typography.bodySmall, color = CustomGray)
    }
}

data class MenuItem(val label: String, val icon: ImageVector, val color: Color, val route: String)

@Composable
fun MenuButton(item: MenuItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(item.color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = item.label, style = MaterialTheme.typography.bodyMedium, color = CustomBlack, fontWeight = FontWeight.Medium)
    }
}
