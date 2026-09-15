package com.jarvis.ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jarvis.ai.JarvisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarvisScreen(viewModel: JarvisViewModel = viewModel()) {
    val messagesState = viewModel.messages.collectAsState()
    val messages = messagesState.value
    val orbState = viewModel.orbState.collectAsState().value
    val amplitude = viewModel.amplitude.collectAsState().value
    val inputTextState = viewModel.inputText.collectAsState()
    val inputText = inputTextState.value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorState = viewModel.error.collectAsState()
    val error = errorState.value

    var showSettings by remember { mutableStateOf(false) }
    var showMemory by remember { mutableStateOf(false) }
    var showWhatsApp by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jarvis") },
                actions = {
                    IconButton(onClick = { showMemory = true }) { Text("M") }
                    IconButton(onClick = { showWhatsApp = true }) { Text("W") }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                contentAlignment = Alignment.Center
            ) {
                OrbComponent(state = orbState, amplitude = amplitude)
            }

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextButton(onClick = { viewModel.clearError() }) { Text("Dismiss") }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.reversed()) { msg ->
                    MessageBubble(message = msg)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.onInputChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message Jarvis...") },
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.sendMessage() },
                    enabled = !isLoading && inputText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
                Spacer(modifier = Modifier.width(4.dp))
                FloatingActionButton(
                    onClick = { viewModel.startListening() },
                    containerColor = if (orbState == OrbState.LISTENING) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Microphone")
                }
            }
        }
    }

    if (showSettings) {
        SettingsDialog(onDismiss = { showSettings = false })
    }
    if (showMemory) {
        MemoryDialog(onDismiss = { showMemory = false })
    }
    if (showWhatsApp) {
        WhatsAppDialog(onDismiss = { showWhatsApp = false })
    }
}

@Composable
fun MessageBubble(message: MessageItem) {
    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser) Color.Black else Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
}
