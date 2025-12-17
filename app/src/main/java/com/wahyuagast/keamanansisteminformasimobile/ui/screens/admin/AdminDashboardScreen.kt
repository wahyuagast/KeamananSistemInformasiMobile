package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit // Kept for compatibility if used elsewhere, but header now goes to Profile
) {
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
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .padding(top = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.labelLarge,
                        color = CustomGray
                    )
                    Text(
                        text = "Administrator",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CustomBlack
                    )
                }
                IconButton(
                    onClick = { onNavigate("admin-profile") },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CustomBackground, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Logout",
                        tint = CustomGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFC6C6C8)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    color = CustomPrimary,
                    value = "24",
                    label = "Pending"
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    color = CustomSuccess,
                    value = "156",
                    label = "Approved"
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Cancel,
                    color = CustomDanger,
                    value = "8",
                    label = "Rejected"
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Group,
                    color = CustomWarning,
                    value = "188",
                    label = "Total Mhs"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Activity
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aktivitas Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.Notifications, null, tint = CustomWarning, modifier = Modifier.size(20.dp))
                    }
                    
                    ActivityItem(
                        icon = Icons.Default.Description,
                        color = CustomPrimary,
                        title = "Surat 1A - Budi Santoso",
                        subtitle = "Menunggu approval • 2 jam lalu"
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    ActivityItem(
                        icon = Icons.Default.CheckCircle,
                        color = CustomSuccess,
                        title = "Form 2A - Ani Wijaya",
                        subtitle = "Disetujui • 5 jam lalu"
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    ActivityItem(
                        icon = Icons.Default.Warning,
                        color = CustomWarning,
                        title = "Form 3A - Dedi Susanto",
                        subtitle = "Perlu revisi • 1 hari lalu"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Menu Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminMenuCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Description,
                    color = CustomPrimary,
                    title = "Kelola Surat",
                    subtitle = "24 pending",
                    onClick = { onNavigate("admin-surat") }
                )
                AdminMenuCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Group,
                    color = CustomSuccess,
                    title = "Data Mahasiswa",
                    subtitle = "188 mahasiswa",
                    onClick = { onNavigate("admin-mahasiswa") }
                )
            }
        }
    }
}

@Composable
fun AdminStatCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, value: String, label: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(32.dp).background(color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                }
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = CustomGray)
        }
    }
}

@Composable
fun ActivityItem(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(32.dp).background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = CustomGray)
        }
    }
}

@Composable
fun AdminMenuCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), CircleShape).padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
             Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = CustomGray)
        }
    }
}
