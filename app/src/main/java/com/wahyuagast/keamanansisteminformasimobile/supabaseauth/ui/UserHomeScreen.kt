package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    profile: ProfileDto?,
    onLogout: () -> Unit,
    onOpenStudentDashboard: () -> Unit // <-- baru: callback untuk buka PKL dashboard
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Home") })
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Text(text = "Halo, ${profile?.full_name ?: "Pengguna"}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onOpenStudentDashboard, modifier = Modifier.fillMaxWidth()) {
                Text("Buka Dashboard PKL")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
        }
    }
}