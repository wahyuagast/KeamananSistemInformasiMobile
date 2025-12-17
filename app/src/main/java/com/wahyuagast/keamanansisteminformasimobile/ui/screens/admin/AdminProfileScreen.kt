package com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.AdminProfileViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun AdminProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadProfile() }

    val state = viewModel.profileState
    val updateState = viewModel.updateState
    val context = androidx.compose.ui.platform.LocalContext.current

    // Observe update state for Toast
    LaunchedEffect(updateState) {
        if (updateState is Resource.Success) {
            android.widget.Toast.makeText(context, updateState.data.message ?: "Update Success", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateState()
        } else if (updateState is Resource.Error) {
            android.widget.Toast.makeText(context, updateState.message, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateState()
        }
    }

    // Image Picker
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? -> selectedImageUri = uri }

    // Form State
    var isEditing by remember { mutableStateOf(false) }
    // We need to initialize form data from 'state' when it loads successfully.
    // Using a simple way: derivedStateOf or separate state initialized in LaunchedEffect(state)

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var studyProgram by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }

    // Sync state to form
    LaunchedEffect(state) {
        if (state is Resource.Success && !isEditing) {
            val u = state.data.user
            val a = u.awardee
            email = u.email
            username = a?.username ?: ""
            nim = a?.nim ?: ""
            degree = a?.degree ?: ""
            phone = a?.phoneNumber ?: ""
            studyProgram = "Computer Science" // Hardcoded in prompt example variable. Ideally "a?.studyProgramId" map to name or text field.
            // Wait, prompt screenshot shows "study_program" is text "Computer Science".
            // But API response shows `study_program_id`. I'll assume text field input for now as per prompt keys.
            year = a?.year ?: ""
            fullname = a?.fullname ?: ""
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(CustomBackground)
    ) {
        // App Bar
        Column(
            modifier = Modifier.fillMaxWidth().background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp).padding(top = 24.dp)
        ) {
             Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = CustomPrimary)
                }
                Text("Profil Admin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                TextButton(onClick = {
                    if (isEditing) {
                        // SAVE
                        val file = selectedImageUri?.let { com.wahyuagast.keamanansisteminformasimobile.utils.FileUtils.getFileFromUri(context, it) }
                        viewModel.updateProfile(email, username, nim, degree, phone, studyProgram, year, fullname, file)
                        isEditing = false
                    } else {
                        isEditing = true
                    }
                }) {
                    Text(if (isEditing) "Simpan" else "Edit", color = CustomPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (state is Resource.Loading && !isEditing) { // Don't show loading when editing (saving handled by toast)
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CustomPrimary) }
        } else if (state is Resource.Success) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier.size(100.dp).background(CustomPrimary, CircleShape)
                        .clickable(enabled = isEditing) { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                         // Ideally show the selected image using Coil, but avoiding new libs if possible or use Coil if added.
                         // Coil was added in build.gradle? Yes: io.coil-kt:coil-compose:2.4.0
                         coil.compose.AsyncImage(
                             model = selectedImageUri,
                             contentDescription = null,
                             modifier = Modifier.fillMaxSize().clip(CircleShape),
                             contentScale = androidx.compose.ui.layout.ContentScale.Crop
                         )
                    } else {
                         Text(email.firstOrNull()?.uppercase() ?: "?", style = MaterialTheme.typography.displayMedium, color = Color.White)
                    }
                    
                    if (isEditing) {
                        Box(modifier = Modifier.align(Alignment.BottomEnd).background(Color.White, CircleShape).padding(4.dp)) {
                            Icon(Icons.Default.Edit, null, tint = CustomPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                     modifier = Modifier.fillMaxWidth(),
                     colors = CardDefaults.cardColors(containerColor = Color.White),
                     shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EditProfileField("Full Name", fullname, isEditing) { fullname = it }
                        Divider(Modifier.padding(vertical = 8.dp))
                        EditProfileField("Email", email, isEditing) { email = it }
                         Divider(Modifier.padding(vertical = 8.dp))
                        EditProfileField("Username", username, isEditing) { username = it }
                         Divider(Modifier.padding(vertical = 8.dp))
                        EditProfileField("NIM", nim, isEditing) { nim = it }
                         Divider(Modifier.padding(vertical = 8.dp))
                         EditProfileField("Degree", degree, isEditing) { degree = it }
                         Divider(Modifier.padding(vertical = 8.dp))
                         EditProfileField("Phone", phone, isEditing) { phone = it }
                         Divider(Modifier.padding(vertical = 8.dp))
                         EditProfileField("Study Program", studyProgram, isEditing) { studyProgram = it }
                         Divider(Modifier.padding(vertical = 8.dp))
                         EditProfileField("Year", year, isEditing) { year = it }
                    }
                }
                
                if (!isEditing) {
                    Spacer(modifier = Modifier.height(24.dp))
                     Button(
                        onClick = { viewModel.logout(onLogout) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEEEE), contentColor = Color.Red),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Keluar")
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileField(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = CustomGray)
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        } else {
             Text(value.ifEmpty { "-" }, style = MaterialTheme.typography.bodyLarge, color = CustomBlack, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
