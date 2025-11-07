package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.SubmissionDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboard(
    submissions: List<SubmissionDto>,
    onCreateSubmission: () -> Unit,
    onOpenRegistration: () -> Unit,
    onOpenExecution: () -> Unit,
    onOpenMonev: () -> Unit,
    onOpenFinalReport: () -> Unit,
    onOpenExamDocs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard PKL") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateSubmission) {
                Icon(Icons.Default.Add, contentDescription = "Ajukan")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                "Menu Cepat",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(onClick = onOpenRegistration, modifier = Modifier.weight(1f)) {
                    Text("Pendaftaran")
                }
                ElevatedButton(onClick = onOpenExecution, modifier = Modifier.weight(1f)) {
                    Text("Pelaksanaan")
                }
                ElevatedButton(onClick = onOpenMonev, modifier = Modifier.weight(1f)) {
                    Text("Monev")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(onClick = onOpenFinalReport, modifier = Modifier.weight(1f)) {
                    Text("Akhir")
                }
                ElevatedButton(onClick = onOpenExamDocs, modifier = Modifier.weight(1f)) {
                    Text("Berkas Ujian")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Pengajuan Terbaru",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(submissions) { s ->
                    SubmissionCard(submission = s)
                }
            }
        }
    }
}

@Composable
fun SubmissionCard(submission: SubmissionDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(submission.title ?: submission.type, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Status: ${submission.status}", style = MaterialTheme.typography.bodySmall)
            submission.admin_comment?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text("Komentar: $it", style = MaterialTheme.typography.bodySmall)
            }
            submission.file_url?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text("File: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}