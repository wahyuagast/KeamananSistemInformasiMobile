package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.SubmissionDto

@Composable
fun ReviewDialog(
    submission: SubmissionDto,
    onDismiss: () -> Unit,
    onAction: (action: String, comment: String?) -> Unit
) {
    var selectedAction by remember { mutableStateOf("approve") } // "approve" | "reject"
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Review Submission") },
        text = {
            Column {
                Text("Choose an action for this submission.")
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedAction == "approve",
                        onClick = { selectedAction = "approve" }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Approve")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedAction == "reject",
                        onClick = { selectedAction = "reject" }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Reject")
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val c = comment.trim().ifEmpty { null }
                onAction(selectedAction, c)
            }) { Text("Submit") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}