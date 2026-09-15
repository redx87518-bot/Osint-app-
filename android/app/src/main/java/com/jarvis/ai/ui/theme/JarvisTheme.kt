package com.jarvis.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JarvisDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    secondary = Color(0xFF1A237E),
    tertiary = Color(0xFF00E676),
    background = Color(0xFF0A0E27),
    surface = Color(0xFF1A1A2E),
    error = Color(0xFFFF5252),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

private val JarvisLightColorScheme = lightColorScheme(
    primary = Color(0xFF1A237E),
    secondary = Color(0xFF00E5FF),
    tertiary = Color(0xFF00E676),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    error = Color(0xFFFF5252),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

@Composable
fun JarvisTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) JarvisDarkColorScheme else JarvisLightColorScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
