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
fun SubmissionFormScreen(
    initialType: String = "",
    onSubmit: (type: String, title: String?, description: String?, file: File?) -> Unit,
    onBack: () -> Unit,
    pickFileAsFile: (Uri) -> File?
) {
    var type by remember { mutableStateOf(initialType) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pickedFile by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { pickedFile = pickFileAsFile(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pengajuan") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Tipe (surat_ttd / form_2a / form_3a / monev / logbook / exam)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 6
            )
            Spacer(modifier = Modifier.height(12.dp))

            FilePickerButton(onPick = { launcher.launch("*/*") }, file = pickedFile)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onSubmit(type, title.ifEmpty { null }, description.ifEmpty { null }, pickedFile) },
                modifier = Modifier.fillMaxWidth()) {
                Text("Kirim Pengajuan")
            }
        }
    }
}

/** Simple file picker button that shows selected file name */
@Composable
fun FilePickerButton(onPick: () -> Unit, file: File?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onPick) { Text("Pilih Berkas") }
        file?.let { Text(text = it.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 12.dp)) }
    }
}