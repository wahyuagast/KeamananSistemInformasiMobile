package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.SubmissionDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    submissions: List<SubmissionDto>,
    onRefresh: () -> Unit,
    onAction: (submissionId: String, action: String, comment: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<SubmissionDto?>(null) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Admin - Pengajuan PKL") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh")
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (submissions.isEmpty()) {
                EmptyState(
                    message = "No submissions yet",
                    onRefresh = onRefresh,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(submissions) { s ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(s.title ?: s.type, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("User: ${s.user_id}", style = MaterialTheme.typography.bodySmall)
                                    Text("Status: ${s.status}", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(onClick = { selected = s }) { Text("Review") }
                            }
                        }
                    }
                }
            }
        }

        selected?.let { sub ->
            ReviewDialog(
                submission = sub,
                onDismiss = { selected = null },
                onAction = { action, comment ->
                    onAction(sub.id, action, comment)
                    selected = null
                }
            )
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(message, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onRefresh) { Text("Refresh") }
    }
}