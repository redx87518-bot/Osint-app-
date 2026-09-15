package com.jarvis.ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OrbComponent(state: OrbState, amplitude: Float) {
    val color = when (state) {
        OrbState.LISTENING -> Color(0xFF4CAF50)
        OrbState.SPEAKING -> Color(0xFF2196F3)
        OrbState.PROCESSING -> Color(0xFFFFC107)
        else -> Color(0xFF9E9E9E)
    }
    val size = when (state) {
        OrbState.LISTENING -> (80 + amplitude * 40).dp
        else -> 80.dp
    }
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = color
    ) {}
}
