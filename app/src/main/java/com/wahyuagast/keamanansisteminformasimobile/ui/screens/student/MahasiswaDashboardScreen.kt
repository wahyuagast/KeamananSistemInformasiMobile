package com.wahyuagast.keamanansisteminformasimobile.ui.screens.student

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomBackground
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomBlack
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomDanger
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomDanger2
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomGray
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomIndigo
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPrimary
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPurple
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomSuccess
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomWarning
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MahasiswaProfileViewModel
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MonevViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    // Monev view model to fetch timeline/deadlines
    val monevViewModel: MonevViewModel = viewModel()
    LaunchedEffect(Unit) { monevViewModel.loadMonev() }
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Halo,",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomGray,
                            maxLines = 1
                        )
                        if (state is Resource.Loading) {
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(20.dp)
                                    .background(
                                        CustomGray.copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        } else {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = CustomBlack,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = CustomGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(Color(0xFFC6C6C8)))

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
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "60%",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
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
                val monevState = monevViewModel.monevState
                when (monevState) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = CustomPrimary
                            )
                        }
                    }

                    is Resource.Success -> {
                        val timeline = monevState.data.timeline
                        if (timeline.isEmpty()) {
                            Text(
                                "Tidak ada deadline",
                                style = MaterialTheme.typography.bodySmall,
                                color = CustomGray
                            )
                        } else {
                            // find up to 3 timeline items nearest to now (by endDate/startDate/createdAt)
                            val nowMillis = System.currentTimeMillis()
                            val itemsWithDate = timeline.mapNotNull { item ->
                                val d =
                                    parseToDate(item.endDate ?: item.startDate ?: item.createdAt)
                                if (d != null) Pair(item, d) else null
                            }

                            if (itemsWithDate.isEmpty()) {
                                Text(
                                    "Tidak ada deadline",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CustomGray
                                )
                            } else {
                                val nearestList =
                                    itemsWithDate.sortedBy { pair -> kotlin.math.abs(nowMillis - pair.second.time) }
                                        .take(3)
                                val periodsState = viewModel.periodsState
                                nearestList.forEachIndexed { idx, (item, date) ->
                                    val relative = formatRelative(date)
                                    // resolve periode name if available
                                    val periodeName = when (periodsState) {
                                        is Resource.Success -> periodsState.data.periods.find { it.id == item.periodeId }?.name
                                        else -> null
                                    }
                                    TimelineItem(
                                        title = item.title ?: "(tidak berjudul)",
                                        subtitle = relative,
                                        description = item.description,
                                        periode = periodeName,
                                        color = if (date.time < System.currentTimeMillis()) CustomDanger else CustomWarning,
                                        isLast = idx == nearestList.size - 1,
                                        onClick = { onNavigate("monev") }
                                    )
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        Text(
                            text = "Gagal memuat deadline: ${monevState.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomDanger
                        )
                    }

                    else -> {}
                }
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = CustomPrimary
                            )
                        }
                    }

                    is Resource.Success -> {
                        val mitras = mitraState.data.mitra
                        if (mitras.isEmpty()) {
                            Text(
                                "Belum ada data mitra",
                                style = MaterialTheme.typography.bodySmall,
                                color = CustomGray
                            )
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
                        Text(
                            text = "Gagal memuat: ${mitraState.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomDanger
                        )
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
                MenuItem("Monev PKL", Icons.AutoMirrored.Filled.Assignment, CustomDanger2, "monev"),
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold, color = CustomBlack)
        }
        content()
    }
}

@Composable
fun TimelineItem(
    title: String,
    subtitle: String,
    description: String? = null,
    periode: String? = null,
    color: Color,
    isLast: Boolean,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(bottom = if (isLast) 0.dp else 12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = CustomBlack)
            Spacer(modifier = Modifier.height(4.dp))
            // subtitle (relative)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = CustomGray)
            // optional periode and description
            periode?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Periode: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomGray
                )
            }
            description?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = CustomBlack)
            }
            if (!isLast) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CustomBackground))
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
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = CustomBlack,
            fontWeight = FontWeight.Medium
        )
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
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            color = CustomBlack,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper: parse various date string formats to Date (compatible with minSdk 24)
private fun parseToDate(dateStr: String?): Date? {
    if (dateStr.isNullOrBlank()) return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd"
    )
    for (p in patterns) {
        try {
            val sdf = SimpleDateFormat(p, Locale.getDefault())
            // when pattern includes 'Z' it's UTC
            if (p.contains("'Z'") || p.contains("XXX")) {
                sdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            val d = sdf.parse(dateStr)
            if (d != null) return d
        } catch (_: Exception) {
        }
    }
    return null
}

// Helper: format relative time in Indonesian like "Jatuh tempo 3 hari yang lalu" or "Jatuh tempo dalam 5 hari"; if date is null show unknown
private fun formatRelative(date: Date?): String {
    if (date == null) return "Waktu tidak tersedia"
    val now = System.currentTimeMillis()
    val secs = (now - date.time) / 1000
    val absDays = kotlin.math.abs(secs / (60 * 60 * 24))
    return if (secs > 0) {
        // past
        if (absDays < 30) "Jatuh tempo $absDays hari yang lalu" else "Jatuh tempo ${absDays / 30} bulan yang lalu"
    } else if (secs < 0) {
        val days = absDays
        if (days < 30) "Jatuh tempo dalam $days hari" else "Jatuh tempo dalam ${days / 30} bulan"
    } else {
        "Jatuh tempo hari ini"
    }
}
