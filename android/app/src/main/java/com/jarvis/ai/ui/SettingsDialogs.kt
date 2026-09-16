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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarvis.ai.whatsapp.WhatsAppConnectionState
import com.jarvis.ai.whatsapp.WhatsAppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        text = { Text("Settings coming soon.") },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun WhatsAppDialog(onDismiss: () -> Unit) {
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    val repository = remember { WhatsAppRepository(com.jarvis.ai.network.ApiClient.createWhatsAppApi(), scope) }
    val connectionState by repository.state.collectAsState(initial = WhatsAppConnectionState.Disconnected)

    DisposableEffect(Unit) {
        onDispose {
            repository.cancelPolling()
        }
    }

    var phoneNumber by remember { mutableStateOf("") }
    var pairingCode by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isConnecting by remember { mutableStateOf(false) }

    LaunchedEffect(connectionState) {
        when (connectionState) {
            is WhatsAppConnectionState.Pairing -> {
                errorMessage = null
            }
            is WhatsAppConnectionState.Connected -> {
                isConnecting = false
                pairingCode = null
            }
            is WhatsAppConnectionState.Disconnected -> {
                isConnecting = false
                pairingCode = null
            }
            is WhatsAppConnectionState.Error -> {
                isConnecting = false
                errorMessage = (connectionState as WhatsAppConnectionState.Error).message
            }
            else -> {}
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF25D366))
                Text(
                    text = "WhatsApp",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (connectionState) {
                    is WhatsAppConnectionState.Connected -> {
                        val connected = connectionState as WhatsAppConnectionState.Connected
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x3325D366)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Connected", color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                                connected.phone?.let {
                                    Text("Phone: $it", color = Color.White, fontSize = 14.sp)
                                }
                                connected.deviceId?.let {
                                    Text("Device: $it", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    is WhatsAppConnectionState.Pairing -> {
                        pairingCode?.let { code ->
                            Text("Pairing Code", fontWeight = FontWeight.Bold)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = code,
                                    modifier = Modifier.padding(24.dp),
                                    color = Color(0xFF00E5FF),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Open WhatsApp → Linked Devices → Link a Device → Link with phone number",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    is WhatsAppConnectionState.WaitingForPhone -> {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("2348012345678") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            enabled = !isConnecting
                        )
                    }
                    is WhatsAppConnectionState.CreatingDevice -> {
                        Text("Creating device...", color = Color(0xFFFFD740))
                    }
                    else -> {
                        Text("Tap Connect to link WhatsApp", color = Color.Gray)
                    }
                }

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color(0xFFFF5252), fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            when (connectionState) {
                is WhatsAppConnectionState.Connected -> {
                    TextButton(
                        onClick = {
                            scope.launch {
                                repository.disconnect()
                            }
                        }
                    ) {
                        Text("Disconnect", color = Color(0xFFFF5252))
                    }
                }
                    is WhatsAppConnectionState.Pairing -> {
                        TextButton(
                            onClick = {
                                if (phoneNumber.isNotBlank()) {
                                    isConnecting = true
                                    errorMessage = null
                                    scope.launch {
                                        val result = repository.pair(phoneNumber)
                                        result.onSuccess { response ->
                                            pairingCode = response.pairing_code
                                        }.onFailure { e ->
                                            errorMessage = e.message ?: "Pairing failed"
                                            isConnecting = false
                                        }
                                    }
                                }
                            },
                            enabled = !isConnecting
                        ) {
                            Text("New Code", color = Color(0xFF00E5FF))
                        }
                    }
                is WhatsAppConnectionState.WaitingForPhone -> {
                    TextButton(
                        onClick = {
                            if (phoneNumber.isBlank()) {
                                errorMessage = "Please enter a phone number"
                                return@TextButton
                            }
                            isConnecting = true
                            errorMessage = null
                            scope.launch {
                                val result = repository.pair(phoneNumber)
                                result.onSuccess { response ->
                                    pairingCode = response.pairing_code
                                }.onFailure { e ->
                                    errorMessage = e.message ?: "Pairing failed"
                                    isConnecting = false
                                }
                            }
                        },
                        enabled = !isConnecting
                    ) {
                        Text("Pair", color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    TextButton(
                        onClick = {
                            scope.launch {
                                val result = repository.createDevice()
                                result.onSuccess {
                                    errorMessage = null
                                }.onFailure { e ->
                                    errorMessage = e.message ?: "Connection failed"
                                }
                            }
                        }
                    ) {
                        Text("Connect", color = Color(0xFF25D366), fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
