package com.wahyuagast.keamanansisteminformasimobile.ui.screens.student

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomBackground
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomBlack
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomGray
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPrimary
import com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MahasiswaProfileViewModel
import com.wahyuagast.keamanansisteminformasimobile.utils.FileUtils
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource

@Composable
fun ProfileScreen(
    viewModel: MahasiswaProfileViewModel = viewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadProfile() }

    val state = viewModel.profileState
    val updateState = viewModel.updateState
    val context = LocalContext.current

    // Observe update state for Toast
    LaunchedEffect(updateState) {
        if (updateState is Resource.Success) {
            Toast.makeText(
                context,
                updateState.data?.message ?: "Update Success",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetUpdateState()
        } else if (updateState is Resource.Error) {
            Toast.makeText(context, updateState.message, Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateState()
        }
    }

    // Image Picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    // Form State
    var isEditing by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var studyProgramId by remember { mutableStateOf("") } // Store ID as string
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
            studyProgramId = a?.studyProgramId?.toString() ?: ""
            year = a?.year ?: ""
            fullname = a?.fullname ?: ""
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        // App Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(CustomBackground, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = CustomPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Profil",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = CustomBlack
                )

                TextButton(
                    onClick = {
                        if (isEditing) {
                            // SAVE
                            val file =
                                selectedImageUri?.let { FileUtils.getFileFromUri(context, it) }
                            viewModel.updateProfile(
                                email,
                                username,
                                nim,
                                degree,
                                phone,
                                studyProgramId,
                                year,
                                fullname,
                                file
                            )
                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    }
                ) {
                    Text(
                        text = if (isEditing) "Simpan" else "Edit",
                        color = CustomPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(Color(0xFFC6C6C8)))

        if (state is Resource.Loading && !isEditing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = CustomPrimary) }
        } else {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Photo Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(CustomPrimary, CircleShape)
                                .clip(CircleShape)
                                .clickable(enabled = isEditing) { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .background(Color.White, CircleShape)
                                    .shadow(4.dp, CircleShape)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Edit Photo",
                                    tint = CustomPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = fullname.ifEmpty { "Nama Mahasiswa" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = CustomBlack
                    )
                    Text(
                        text = nim.ifEmpty { "-" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomGray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Form Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    ProfileItem(
                        icon = Icons.Default.Person,
                        label = "Nama Lengkap",
                        value = fullname,
                        isEditing = isEditing,
                        onValueChange = { fullname = it }
                    )
                    Divider()
                    ProfileItem(
                        icon = Icons.Default.Book,
                        label = "NIM",
                        value = nim,
                        isEditing = isEditing, // Assuming NIM is editable for now based on API updateProfile having it
                        onValueChange = { nim = it }
                    )
                    Divider()
                    ProfileItem(
                        icon = Icons.Default.Book,
                        label = "Program Studi ID",
                        value = studyProgramId,
                        isEditing = false, // ID shouldn't be edited manually as text usually
                        onValueChange = { studyProgramId = it }
                    )
                    Divider()
                    ProfileItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = email,
                        isEditing = isEditing,
                        onValueChange = { email = it }
                    )
                    Divider()
                    ProfileItem(
                        icon = Icons.Default.Phone,
                        label = "Nomor Telepon",
                        value = phone,
                        isEditing = isEditing,
                        onValueChange = { phone = it }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    label: String,
    value: String,
    isEditing: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CustomGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = CustomGray,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            if (isEditing) {
                BasicTextFieldCompat(
                    value = value,
                    onValueChange = onValueChange
                )
            } else {
                Text(
                    text = value.ifEmpty { "-" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomBlack
                )
            }
        }
    }
}

@Composable
fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(CustomBackground)
    )
}

@Composable
private fun BasicTextFieldCompat(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CustomBlack)
    )
}
