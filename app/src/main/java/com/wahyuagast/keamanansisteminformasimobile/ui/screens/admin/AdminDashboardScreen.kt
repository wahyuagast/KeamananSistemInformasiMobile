package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentDto
import com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomBackground
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomBlack
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomDanger
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomGray
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPrimary
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomSuccess
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomWarning

@Suppress("UNUSED_PARAMETER")
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    _onLogout: () -> Unit // Kept for compatibility if used elsewhere, but header now goes to Profile
) {
    val repo = remember { DocumentRepository() }

    var documents by remember { mutableStateOf<List<DocumentDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var awardeeCount by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            documents = repo.getAdminDocuments()
            val (count, list) = repo.getAwardees()
            awardeeCount = count
            error = null
        } catch (_: Exception) {
            error = "Failed to load"
        } finally {
            isLoading = false
        }
    }

    // compute stats
    val totalSurat = documents.size
    val approved = documents.count { it.status?.equals("approved", true) == true }
    val rejected = documents.count { it.status?.equals("rejected", true) == true }
    val pending = documents.count { it.status?.equals("pending", true) == true }

    // recent activities: take latest 5 by createdAt (fallback to uploadedAt)
    val recent = documents.sortedByDescending { it.createdAt ?: it.uploadedAt ?: "" }.take(5)

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
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
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

        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color(0xFFC6C6C8))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Stats Grid (dynamic values)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    color = CustomPrimary,
                    value = pending.toString(),
                    label = "Pending"
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    color = CustomSuccess,
                    value = approved.toString(),
                    label = "Approved"
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Cancel,
                    color = CustomDanger,
                    value = rejected.toString(),
                    label = "Rejected"
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Description,
                    color = CustomWarning,
                    value = totalSurat.toString(),
                    label = "Total Surat"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Activity (dynamic)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Aktivitas Terbaru",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.Notifications,
                            null,
                            tint = CustomWarning,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (isLoading) {
                        Text("Memuat...")
                    } else if (error != null) {
                        Text("Error: $error")
                    } else if (recent.isEmpty()) {
                        Text("Tidak ada aktivitas")
                    } else {
                        recent.forEach { doc ->
                            val title = doc.name ?: doc.description ?: "Surat #${doc.id}"
                            val time = relativeTimeAgo(doc.uploadedAt ?: doc.createdAt)
                            val icon = when (doc.status?.lowercase()) {
                                "approved" -> Icons.Default.CheckCircle
                                "rejected" -> Icons.Default.Cancel
                                else -> Icons.Default.Description
                            }
                            val color = when (doc.status?.lowercase()) {
                                "approved" -> CustomSuccess
                                "rejected" -> CustomDanger
                                else -> CustomPrimary
                            }

                            ActivityItem(
                                icon = icon,
                                color = color,
                                title = title,
                                subtitle = "${doc.userId} • $time"
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Menu Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminMenuCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Description,
                    color = CustomPrimary,
                    title = "Kelola Surat",
                    subtitle = "$pending pending",
                    onClick = { onNavigate("admin-surat") }
                )
                AdminMenuCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Group,
                    color = CustomSuccess,
                    title = "Data Mahasiswa",
                    subtitle = awardeeCount?.let { "$it mahasiswa" } ?: "Memuat...",
                    onClick = { onNavigate("admin-mahasiswa") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Second Menu Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminMenuCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.List,
                    color = CustomWarning,
                    title = "Data Pendaftaran",
                    subtitle = awardeeCount?.let { "$it data" } ?: "Memuat...",
                    onClick = { onNavigate("admin-registration-list") }
                )
                // Spacer for now if we don't have a 4th item, or make it take full width
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

fun relativeTimeAgo(timeStr: String?): String {
    if (timeStr.isNullOrBlank()) return "Unknown time"

    // Try to parse common ISO formats and fallback to simple substring
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd"
    )
    var parsed: java.util.Date? = null
    for (p in patterns) {
        try {
            val sdf = java.text.SimpleDateFormat(p, java.util.Locale.getDefault())
            if (p.contains("'Z'") || p.contains("XXX")) sdf.timeZone =
                java.util.TimeZone.getTimeZone("UTC")
            val d = sdf.parse(timeStr)
            if (d != null) {
                parsed = d; break
            }
        } catch (_: Exception) {
        }
    }

    if (parsed == null) return "Invalid date"

    val now = System.currentTimeMillis()
    val diffSecs = (now - parsed.time) / 1000
    return when {
        diffSecs < 60 -> "${diffSecs}s ago"
        diffSecs < 3600 -> "${diffSecs / 60}m ago"
        diffSecs < 86400 -> "${diffSecs / 3600}h ago"
        else -> "${diffSecs / 86400}d ago"
    }
}

@Composable
fun AdminStatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    value: String,
    label: String
) {
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
                    modifier = Modifier
                        .size(32.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                }
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = CustomGray)
        }
    }
}

@Composable
fun ActivityItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color, CircleShape),
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
fun AdminMenuCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape)
                    .padding(bottom = 12.dp),
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
