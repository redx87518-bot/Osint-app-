package com.jarvis.ai.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun OrbComponent(state: OrbState, amplitude: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 80f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    val glowColor = when (state) {
        OrbState.LISTENING -> Color(0xFF00E5FF)
        OrbState.SPEAKING -> Color(0xFF00E676)
        OrbState.PROCESSING -> Color(0xFFFFD740)
        OrbState.IDLE -> Color(0xFF1A237E)
    }

    Box(modifier = Modifier.size(200.dp)) {
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
