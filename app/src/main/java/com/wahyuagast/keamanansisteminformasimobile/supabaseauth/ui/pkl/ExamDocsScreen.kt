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
fun ExamDocsScreen(
    onUploadExamDocs: (files: List<File>) -> Unit,
    onBack: () -> Unit,
    pickFileAsFile: (Uri) -> File?
) {
    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        files = uris.mapNotNull { pickFileAsFile(it) }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Berkas Ujian") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
        )
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            Button(onClick = { launcher.launch("*/*") }) { Text("Pilih Berkas") }
            Spacer(modifier = Modifier.height(8.dp))
            files.forEach { f -> Text(f.name, style = MaterialTheme.typography.bodySmall) }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onUploadExamDocs(files) }, modifier = Modifier.fillMaxWidth()) { Text("Upload Semua") }
        }
    }
}