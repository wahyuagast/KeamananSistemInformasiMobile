package com.wahyuagast.keamanansisteminformasimobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MenuAnchorType
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MahasiswaProfileViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendaftaranScreen(
    onBack: () -> Unit,
    viewModel: MahasiswaProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        viewModel.loadRegistrationStatus()
    }
    val regState = viewModel.registrationState
    val formState = viewModel.formSubmissionState

    val context = androidx.compose.ui.platform.LocalContext.current

    // Form State
    var selectedMitra by remember {
        mutableStateOf<com.wahyuagast.keamanansisteminformasimobile.data.model.MitraDto?>(
            null
        )
    }
    var selectedPeriode by remember { mutableStateOf<String>("") } // Using ID
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    // UI State
    var showGuide by remember { mutableStateOf(false) }
    var showMitraDropdown by remember { mutableStateOf(false) }
    var showPeriodeDropdown by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Mock Documents
    // Load Document Types
    LaunchedEffect(Unit) {
        viewModel.loadDocumentTypes()
    }
    val docTypesState = viewModel.documentTypesState
    val uploadState = viewModel.uploadDocumentState

    // File Picker & Confirmation
    var activeTypeId by remember { mutableStateOf<Int?>(null) }
    var pendingUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            pendingUri = it
            showConfirmDialog = true
        }
    }

    if (showConfirmDialog && pendingUri != null) {
        val uri = pendingUri!!
        // Get generic filename
        var fileName = "Dokumen PDF"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex)
                }
            }
        }

        AlertDialog(
            onDismissRequest = { 
                showConfirmDialog = false 
                pendingUri = null
            },
            title = { Text("Konfirmasi Upload") },
            text = { Text("Apakah Anda yakin ingin mengupload file \"$fileName\"?") },
            confirmButton = {
                Button(onClick = {
                    activeTypeId?.let { id ->
                         viewModel.uploadDocument(uri, id)
                    }
                    showConfirmDialog = false
                    pendingUri = null
                }) {
                    Text("Ya, Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showConfirmDialog = false
                    pendingUri = null
                }) {
                    Text("Batal")
                }
            }
        )
    }

    // Upload Feedback
    LaunchedEffect(uploadState) {
        if (uploadState is Resource.Success) {
            android.widget.Toast.makeText(context, "Dokumen berhasil diupload", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetUploadState()
        } else if (uploadState is Resource.Error) {
            android.widget.Toast.makeText(context, uploadState.message, android.widget.Toast.LENGTH_LONG).show()
            viewModel.resetUploadState()
        }
    }

    // Combine Types and Status
    val docItems = remember(docTypesState, regState) {
        if (docTypesState is Resource.Success) {
            val uploadedDocs = if (regState is Resource.Success) regState.data.documents else emptyList()
            docTypesState.data.documentTypes.map { type ->
                val uploaded = uploadedDocs.find { it.documentTypeId == type.id }
                DocumentItem(
                    id = type.id.toString(),
                    name = type.name,
                    status = if (uploaded != null) "uploaded" else "empty" // Simplified status logic
                )
            }
        } else {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        // ... (Header remains same, logic handled by surrounding code)
        CommonHeader(title = "Pendaftaran PKL", onBack = onBack) {
            IconButton(
                onClick = { showGuide = !showGuide },
                modifier = Modifier
                    .size(36.dp)
                    .background(CustomBackground, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Guide",
                    tint = CustomPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ... (Registration Status Logic remains same)
            if (regState is Resource.Success) {
                // ... (Status Card Code)
                  val data = regState.data
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val statusText =
                                data.statusRegistrasi.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            Text(
                                text = "Status: $statusText",
                                color = CustomBlack,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${data.progress}%",
                                color = CustomPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { data.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = CustomPrimary,
                            trackColor = CustomBackground,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        )
                        data.message?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = CustomGray
                            )
                        }
                    }
                }
            } else if (regState is Resource.Loading) {
                 Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = CustomPrimary) }
            } else if (regState is Resource.Error) {
                 Card(
                    colors = CardDefaults.cardColors(containerColor = CustomDanger.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = CustomDanger)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = regState.message,
                            color = CustomDanger,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    // Retry Button
                    Button(
                        onClick = { viewModel.loadRegistrationStatus() },
                        colors = ButtonDefaults.buttonColors(containerColor = CustomDanger),
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Retry")
                    }
                }
            }

            // 2. Guide
            if (showGuide) {
                 Card(
                    colors = CardDefaults.cardColors(containerColor = CustomPrimary.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        CustomPrimary.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Panduan Pendaftaran",
                            color = CustomBlack,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "1. Isi Form Pendaftaran Online",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomBlack
                        )
                        Text(
                            text = "2. Upload Dokumen",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomBlack
                        )
                        Text(
                            text = "3. Tunggu Verifikasi",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomBlack
                        )
                    }
                }
            }

            // 3. Documents List (Dynamic)
            if (docTypesState is Resource.Loading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else {
                 Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    docItems.forEach { doc ->
                        DocumentCard(
                            doc = doc,
                            isLoading = uploadState is Resource.Loading && activeTypeId.toString() == doc.id,
                            onUpload = {
                                activeTypeId = doc.id.toIntOrNull()
                                launcher.launch("application/pdf")
                            }
                        )
                    }
                     if (docItems.isEmpty()) {
                         Text("Tidak ada dokumen yang perlu diupload.", style = MaterialTheme.typography.bodySmall, color = CustomGray)
                     }
                }
            }

            // Message UI (Relocated)
            if (formState is Resource.Error) {
                val message = formState.message
                Card(
                    colors = CardDefaults.cardColors(containerColor = CustomDanger.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = CustomDanger)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = message,
                            color = CustomDanger,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else if (formState is Resource.Success) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CustomSuccess.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = CustomSuccess)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formState.data.message ?: "",
                            color = CustomSuccess,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // 4. Form Pendaftaran Online
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Form Pendaftaran Online",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val isFormEnabled = if (regState is Resource.Success) {
                        !regState.data.statusRegistrasi.equals("diterima", ignoreCase = true)
                    } else {
                        true
                    }

                    // Mitra Dropdown
                    ExposedDropdownMenuBox(
                        expanded = showMitraDropdown,
                        onExpandedChange = { if (isFormEnabled) showMitraDropdown = !showMitraDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedMitra?.partnerName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Mitra") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMitraDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            enabled = isFormEnabled,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CustomPrimary,
                                focusedLabelColor = CustomPrimary,
                                disabledTextColor = CustomBlack,
                                disabledBorderColor = CustomGray,
                                disabledLabelColor = CustomGray,
                                disabledTrailingIconColor = CustomGray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showMitraDropdown,
                            onDismissRequest = { showMitraDropdown = false }
                        ) {
                            if (regState is Resource.Success) {
                                regState.data.mitras.forEach { mitra ->
                                    DropdownMenuItem(
                                        text = { Text(mitra.partnerName ?: "") },
                                        onClick = {
                                            selectedMitra = mitra
                                            showMitraDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Periode Dropdown
                    ExposedDropdownMenuBox(
                        expanded = showPeriodeDropdown,
                        onExpandedChange = { if (isFormEnabled) showPeriodeDropdown = !showPeriodeDropdown }
                    ) {
                        val selectedPeriodeName = if (regState is Resource.Success) {
                            regState.data.periods.find { it.id.toString() == selectedPeriode }?.name
                                ?: ""
                        } else ""

                        OutlinedTextField(
                            value = selectedPeriodeName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Periode") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPeriodeDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            enabled = isFormEnabled,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CustomPrimary,
                                focusedLabelColor = CustomPrimary,
                                disabledTextColor = CustomBlack,
                                disabledBorderColor = CustomGray,
                                disabledLabelColor = CustomGray,
                                disabledTrailingIconColor = CustomGray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showPeriodeDropdown,
                            onDismissRequest = { showPeriodeDropdown = false }
                        ) {
                            if (regState is Resource.Success) {
                                regState.data.periods.forEach { periode ->
                                    DropdownMenuItem(
                                        text = { Text(periode.name ?: "") },
                                        onClick = {
                                            selectedPeriode = periode.id.toString()
                                            // Auto-fill dates
                                            startDate = periode.startDate?.replace("-", "/") ?: ""
                                            endDate = periode.endDate?.replace("-", "/") ?: ""
                                            showPeriodeDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Start Date
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tanggal Mulai") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.DateRange,
                                null,
                                modifier = Modifier.clickable(enabled = isFormEnabled) { showStartDatePicker = true })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isFormEnabled) { showStartDatePicker = true },
                        enabled = false, // Disable text input, rely on click
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = CustomBlack,
                            disabledBorderColor = CustomGray,
                            disabledLabelColor = CustomGray,
                            disabledTrailingIconColor = if(isFormEnabled) CustomPrimary else CustomGray
                        )
                    )
                    if (showStartDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { showStartDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val date = java.text.SimpleDateFormat(
                                            "yyyy/MM/dd",
                                            java.util.Locale.getDefault()
                                        ).format(java.util.Date(millis))
                                        startDate = date
                                    }
                                    showStartDatePicker = false
                                }) { Text("OK", color = CustomPrimary) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showStartDatePicker = false
                                }) { Text("Cancel", color = CustomDanger) }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // End Date
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tanggal Selesai") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.DateRange,
                                null,
                                modifier = Modifier.clickable(enabled = isFormEnabled) { showEndDatePicker = true })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isFormEnabled) { showEndDatePicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = CustomBlack,
                            disabledBorderColor = CustomGray,
                            disabledLabelColor = CustomGray,
                            disabledTrailingIconColor = if(isFormEnabled) CustomPrimary else CustomGray
                        )
                    )
                    if (showEndDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { showEndDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val date = java.text.SimpleDateFormat(
                                            "yyyy/MM/dd",
                                            java.util.Locale.getDefault()
                                        ).format(java.util.Date(millis))
                                        endDate = date
                                    }
                                    showEndDatePicker = false
                                }) { Text("OK", color = CustomPrimary) }
                            },
                            dismissButton = {
                                TextButton(onClick = { showEndDatePicker = false }) {
                                    Text(
                                        "Cancel",
                                        color = CustomDanger
                                    )
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (selectedMitra != null && selectedPeriode.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                                viewModel.submitRegistrationForm(
                                    selectedMitra!!.id.toString(),
                                    selectedPeriode,
                                    startDate,
                                    endDate
                                )
                            } else {
                                android.widget.Toast.makeText(
                                    context,
                                    "Mohon lengkapi semua data",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isFormEnabled && formState !is Resource.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomPrimary,
                            disabledContainerColor = CustomGray
                        )
                    ) {
                        if (formState is Resource.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Simpan & Lanjutkan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentCard(doc: DocumentItem, isLoading: Boolean = false, onUpload: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(CustomPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.InsertDriveFile,
                            contentDescription = null,
                            tint = CustomPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = doc.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CustomBlack
                        )
                        Text(
                            text = "PDF, max 2MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = CustomGray
                        )
                    }
                }

                // Status Icon
                when (doc.status) {
                    "uploaded" -> Icon(
                        Icons.Default.CheckCircle,
                        null,
                        tint = CustomSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    "pending" -> Icon(
                        Icons.Default.Schedule,
                        null,
                        tint = CustomWarning,
                        modifier = Modifier.size(20.dp)
                    )
                    else -> if(isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = CustomPrimary)
                    } else {
                        Icon(
                            Icons.Default.Upload,
                            null,
                            tint = CustomPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (doc.status) {
                "empty" -> {
                    Button(
                        onClick = onUpload,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomPrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                             CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload Dokumen")
                        }
                    }
                }

                "pending" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CustomWarning.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Menunggu verifikasi admin",
                            color = CustomWarning,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                "uploaded" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CustomSuccess.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Dokumen terverifikasi",
                            color = CustomSuccess,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

data class DocumentItem(
    val id: String,
    val name: String,
    val status: String,
    val comment: String = ""
)
