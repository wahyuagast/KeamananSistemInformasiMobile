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
fun RegistrationFormScreen(
    initialFormType: String = "2A",
    onSubmitRegistration: (formType: String, fields: Map<String, String>, file: File?) -> Unit,
    onBack: () -> Unit,
    pickFileAsFile: (Uri) -> File?
) {
    var nim by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var supervisor by remember { mutableStateOf("") }
    var formType by remember { mutableStateOf(initialFormType) }
    var pickedFile by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { pickedFile = pickFileAsFile(it) }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Pendaftaran PKL") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
        )
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            OutlinedTextField(value = nim, onValueChange = { nim = it }, label = { Text("NIM") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = institution, onValueChange = { institution = it }, label = { Text("Instansi PKL") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = supervisor, onValueChange = { supervisor = it }, label = { Text("Pembimbing Instansi") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = formType, onValueChange = { formType = it }, label = { Text("Jenis Form (2A/2B)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            FilePickerButton(onPick = { launcher.launch("*/*") }, file = pickedFile)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val fields = mapOf("nim" to nim, "instansi" to institution, "pembimbing" to supervisor)
                onSubmitRegistration(formType, fields, pickedFile)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Daftar PKL")
            }
        }
    }
}

/** reuse FilePickerButton (if you put in separate util file you can remove duplicate) */
@Composable
fun FilePickerButtonRegistration(onPick: () -> Unit, file: File?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onPick) { Text("Pilih Berkas") }
        file?.let { Text(text = it.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 12.dp)) }
    }
}