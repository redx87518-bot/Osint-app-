package com.jarvis.ai.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jarvis.ai.JarvisViewModel
import kotlinx.coroutines.delay

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
        containerColor = Color(0xFF0A0E27),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jarvis AI",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = { showMemory = true }) {
                        Text("M", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { showWhatsApp = true }) {
                        Text("W", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                ModernOrbComponent(state = orbState, amplitude = amplitude)
            }

            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FF5252))
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFFF5252),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                TextButton(onClick = { viewModel.clearError() }) { Text("Dismiss", color = Color.White) }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.reversed()) { msg ->
                    ModernMessageBubble(message = msg)
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
                    enabled = !isLoading,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF00E5FF)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.sendMessage() },
                    enabled = !isLoading && inputText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF00E5FF))
                }
                Spacer(modifier = Modifier.width(4.dp))
                FloatingActionButton(
                    onClick = { viewModel.startListening() },
                    containerColor = if (orbState == OrbState.LISTENING) Color(0xFF00E5FF) else Color(0xFF1A237E),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Microphone", tint = Color.White)
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
fun ModernMessageBubble(message: MessageItem) {
    val backgroundColor = if (message.isUser) {
        Color(0xFF1A237E)
    } else {
        Color(0xFF2A2A4A)
    }
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ModernOrbComponent(state: OrbState, amplitude: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 80f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "radius"
    )

    val glowColor = when (state) {
        OrbState.LISTENING -> Color(0xFF00E5FF)
        OrbState.SPEAKING -> Color(0xFF00E676)
        OrbState.PROCESSING -> Color(0xFFFFD740)
        OrbState.IDLE -> Color(0xFF1A237E)
    }

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = animatedRadius + amplitude * 20

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor.copy(alpha = 0.6f), Color.Transparent),
                    center = center,
                    radius = radius * 1.5f
                ),
                center = center,
                radius = radius * 1.5f
            )

            drawCircle(
                color = glowColor.copy(alpha = 0.3f),
                center = center,
                radius = radius * 1.2f,
                style = Stroke(width = 2.dp.toPx())
            )

            drawCircle(
                color = glowColor,
                center = center,
                radius = radius * 0.8f,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}
