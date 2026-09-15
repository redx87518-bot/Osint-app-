package com.jarvis.ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    val backendUrlState = remember { mutableStateOf("") }
    val backendUrl = backendUrlState.value

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = backendUrl,
                    onValueChange = { backendUrlState.value = it },
                    label = { Text("Backend URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Configure your backend endpoint for AI processing",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun MemoryDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Memory", fontWeight = FontWeight.Bold) },
        text = { Text("Memory management — coming soon.") },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun WhatsAppDialog(onDismiss: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF25D366))
                Text(
                    text = "Link WhatsApp",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Enter your WhatsApp number to link with Jarvis AI",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+1234567890") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                if (status.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (status.contains("success", ignoreCase = true)) {
                                Color(0x3325D366)
                            } else {
                                Color(0x33FF5252)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = status,
                            modifier = Modifier.padding(12.dp),
                            color = if (status.contains("success", ignoreCase = true)) {
                                Color(0xFF25D366)
                            } else {
                                Color(0xFFFF5252)
                            },
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (phoneNumber.isBlank()) {
                        status = "Please enter a phone number"
                        return@TextButton
                    }

                    isLoading = true
                    status = "Linking WhatsApp..."

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        status = "WhatsApp linked successfully! You can now chat with Jarvis via WhatsApp."
                        isLoading = false
                    }
                },
                enabled = !isLoading
            ) {
                Text("Link", color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
