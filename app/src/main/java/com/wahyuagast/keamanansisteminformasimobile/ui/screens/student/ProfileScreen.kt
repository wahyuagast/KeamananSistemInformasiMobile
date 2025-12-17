package com.wahyuagast.keamanansisteminformasimobile.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.*

@Composable
fun ProfileScreen(
    userData: Map<String, String>,
    onBack: () -> Unit
) {
    // Local state for editing to simulate behavior
    var isEditing by remember { mutableStateOf(false) }
    var formData by remember { mutableStateOf(userData) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomBackground)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(top = 24.dp) // Extra top padding
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
                            // Save logic (mock)
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
        
        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFC6C6C8)))

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
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(CustomPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
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
                    text = formData["nama"] ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = CustomBlack
                )
                Text(
                    text = formData["nim"] ?: "",
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
                    value = formData["nama"] ?: "",
                    isEditing = isEditing,
                    onValueChange = { formData = formData.toMutableMap().apply { put("nama", it) } }
                )
                Divider()
                ProfileItem(
                    icon = Icons.Default.Book,
                    label = "NIM",
                    value = formData["nim"] ?: "",
                    isEditing = false // NIM usually not editable
                )
                Divider()
                ProfileItem(
                    icon = Icons.Default.Book, // Using book for Prodi as approx
                    label = "Program Studi",
                    value = formData["prodi"] ?: "",
                    isEditing = isEditing,
                     onValueChange = { formData = formData.toMutableMap().apply { put("prodi", it) } }
                )
                Divider()
                ProfileItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = formData["email"] ?: "",
                    isEditing = isEditing,
                     onValueChange = { formData = formData.toMutableMap().apply { put("email", it) } }
                )
                Divider()
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Nomor Telepon",
                    value = formData["phone"] ?: "",
                    isEditing = isEditing,
                     onValueChange = { formData = formData.toMutableMap().apply { put("phone", it) } }
                )
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
                // Determine keyboard type if needed, basic implementation
                BasicTextFieldCompat(
                     value = value,
                     onValueChange = onValueChange
                )
            } else {
                Text(
                    text = value,
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
    // Custom basic text field to match the plain look
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent, // No line
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = CustomBlack)
    )
}
