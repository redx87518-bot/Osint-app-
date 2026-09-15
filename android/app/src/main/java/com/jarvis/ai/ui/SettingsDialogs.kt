package com.jarvis.ai.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    val backendUrlState = remember { mutableStateOf("") }
    val backendUrl = backendUrlState.value

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            OutlinedTextField(
                value = backendUrl,
                onValueChange = { backendUrlState.value = it },
                label = { Text("Backend URL") }
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun MemoryDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Memory") },
        text = { Text("Memory management — coming soon.") },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun WhatsAppDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("WhatsApp") },
        text = { Text("WhatsApp linking — coming soon.") },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}
