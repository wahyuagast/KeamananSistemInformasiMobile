package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalReportScreen(
    onUploadFinal: (logbook: File?, suratSelesai: File?, draftJurnal: File?) -> Unit,
    onBack: () -> Unit,
    pickFileAsFile: (Uri) -> File?
) {
    var logbook by remember { mutableStateOf<File?>(null) }
    var surat by remember { mutableStateOf<File?>(null) }
    var draft by remember { mutableStateOf<File?>(null) }

    val launcherLog = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { logbook = pickFileAsFile(it) } }
    val launcherSurat = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { surat = pickFileAsFile(it) } }
    val launcherDraft = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { draft = pickFileAsFile(it) } }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Akhir Pelaksanaan") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
        )
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            Button(onClick = { launcherLog.launch("*/*") }) { Text("Upload Logbook") }
            logbook?.let { Text("Logbook: ${it.name}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)) }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { launcherSurat.launch("*/*") }) { Text("Upload Surat Selesai") }
            surat?.let { Text("Surat: ${it.name}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)) }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { launcherDraft.launch("*/*") }) { Text("Upload Draft Jurnal") }
            draft?.let { Text("Draft: ${it.name}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)) }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onUploadFinal(logbook, surat, draft) }, modifier = Modifier.fillMaxWidth()) { Text("Submit") }
        }
    }
}