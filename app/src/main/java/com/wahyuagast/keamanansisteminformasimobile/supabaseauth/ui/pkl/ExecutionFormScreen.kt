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
fun ExecutionFormScreen(
    onSubmitExecution: (startDate: String, endDate: String, activities: String, file: File?) -> Unit,
    onBack: () -> Unit,
    pickFileAsFile: (Uri) -> File?
) {
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var activities by remember { mutableStateOf("") }
    var pickedFile by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { pickedFile = pickFileAsFile(it) }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Pelaksanaan PKL") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
        )
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Tanggal Mulai (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("Tanggal Selesai (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = activities, onValueChange = { activities = it }, label = { Text("Uraian Kegiatan") }, modifier = Modifier.fillMaxWidth(), maxLines = 6)
            Spacer(modifier = Modifier.height(12.dp))

            FilePickerButton(onPick = { launcher.launch("*/*") }, file = pickedFile)

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onSubmitExecution(start, end, activities, pickedFile) }, modifier = Modifier.fillMaxWidth()) {
                Text("Simpan Pelaksanaan")
            }
        }
    }
}