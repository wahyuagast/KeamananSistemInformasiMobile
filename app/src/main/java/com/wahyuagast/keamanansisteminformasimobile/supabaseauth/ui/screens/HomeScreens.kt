package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.DocumentDto
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(viewModel: HomeViewModel, onEditProfile: ()->Unit, onLogout: ()->Unit) {
    val state by viewModel.ui.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("User Home") }, actions = {
            TextButton(onClick = { onEditProfile() }) { Text("Edit") }
            TextButton(onClick = { viewModel.logout { onLogout() } }) { Text("Logout") }
        })
    }) { padding ->
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                "Hello, ${state.profile?.full_name ?: state.profile?.email ?: "User"}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(12.dp))

            Text("Your Documents", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))

            if (state.documents.isEmpty()) {
                Text("No documents yet.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.documents) { doc ->
                        DocumentCard(doc)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(viewModel: HomeViewModel, onLogout: ()->Unit) {
    val state by viewModel.ui.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAll() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Admin Home") }, actions = {
            TextButton(onClick = { viewModel.logout { onLogout() } }) { Text("Logout") }
        })
    }) { padding ->
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                "Welcome Admin, ${state.profile?.full_name ?: state.profile?.email}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(12.dp))

            Text("All Users", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))

            if (state.allProfiles.isEmpty()) {
                Text("No users found")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.allProfiles) { p ->
                        ProfileCard(p)
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentCard(doc: DocumentDto) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(doc.title ?: "(no title)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(doc.content?.take(200) ?: "-", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "Status: ${if (doc.is_public == true) "Public" else "Private"}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun ProfileCard(p: ProfileDto) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(p.full_name ?: p.email ?: "(no name)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text("Role: ${p.role ?: "user"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}