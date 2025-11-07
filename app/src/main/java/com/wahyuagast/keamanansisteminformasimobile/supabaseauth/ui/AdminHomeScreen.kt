package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    profile: ProfileDto?,
    onLogout: () -> Unit,
    onOpenAdminDashboard: () -> Unit // <-- Tambahkan ini
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Admin Home") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Halo, ${profile?.full_name ?: "Admin"}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onOpenAdminDashboard, modifier = Modifier.fillMaxWidth()) {
                Text("Buka Dashboard Admin")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}