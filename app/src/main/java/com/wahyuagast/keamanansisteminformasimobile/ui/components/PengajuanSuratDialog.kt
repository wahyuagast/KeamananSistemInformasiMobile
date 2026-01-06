@file:OptIn(ExperimentalMaterial3Api::class)

package com.wahyuagast.keamanansisteminformasimobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentType
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPrimary
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomGray

@Composable
fun PengajuanSuratDialog(
    documentTypes: List<DocumentType>,
    isLoading: Boolean,
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var selectedType by remember { mutableStateOf<DocumentType?>(null) }
    var description by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ajukan Surat",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Dropdown
                ExposedDropdownMenuBox(
                    expanded = showDropdown,
                    onExpandedChange = { showDropdown = !showDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedType?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Jenis Surat", style = MaterialTheme.typography.bodyMedium) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                         colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CustomPrimary,
                            focusedLabelColor = CustomPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        documentTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    selectedType = type
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Keterangan/Deskripsi", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                     colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CustomPrimary,
                        focusedLabelColor = CustomPrimary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = CustomGray, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedType != null && description.isNotEmpty()) {
                                onSubmit(selectedType!!.id, description)
                            }
                        },
                        enabled = !isLoading && selectedType != null && description.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomPrimary)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        } else {
                            Text("Ajukan", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
