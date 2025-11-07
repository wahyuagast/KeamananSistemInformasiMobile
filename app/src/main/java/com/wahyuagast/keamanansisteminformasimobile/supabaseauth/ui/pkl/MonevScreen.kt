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
fun MonevScreen(
    onSubmitMonev: (notes: String, checklist: Map<String, Boolean>, file: File?) -> Unit,
    onBack: () -> Unit,
    pickFileAsFile: (Uri) -> File?
) {
    var notes by remember { mutableStateOf("") }
    val items = listOf("Kehadiran", "Kepatuhan Protokol", "Kualitas Pekerjaan")
    var pickedFile by remember { mutableStateOf<File?>(null) }
    var checks by remember { mutableStateOf(items.associateWith { false }.toMutableMap()) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { pickedFile = pickFileAsFile(it) }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Monev / Observasi") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
        )
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            items.forEach { key ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(key, modifier = Modifier.weight(1f))
                    Switch(checked = checks[key] ?: false, onCheckedChange = { checks[key] = it })
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan") }, modifier = Modifier.fillMaxWidth(), maxLines = 6)
            Spacer(modifier = Modifier.height(12.dp))

            FilePickerButton(onPick = { launcher.launch("*/*") }, file = pickedFile)

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { onSubmitMonev(notes, checks.toMap(), pickedFile) }, modifier = Modifier.fillMaxWidth()) {
                Text("Simpan Observasi")
            }
        }
    }
}