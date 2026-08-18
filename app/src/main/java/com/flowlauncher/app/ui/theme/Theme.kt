package com.flowlauncher.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8B83FF),
    onPrimary = Color.White,
    secondary = Color(0xFF6C63FF),
    background = Color(0xFF0D0D12),
    surface = Color(0xFF16161F),
    onBackground = Color(0xFFF0F0F5),
    onSurface = Color(0xFFE8E8ED)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6C63FF),
    onPrimary = Color.White,
    secondary = Color(0xFF5A52E0),
    background = Color(0xFFF8F8FC),
    surface = Color.White,
    onBackground = Color(0xFF1A1A24),
    onSurface = Color(0xFF2A2A36)
)

@Composable
fun FlowLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: Long = 0xFF6C63FF,
    content: @Composable () -> Unit
) {
    val accent = Color(accentColor)
    val colorScheme = if (darkTheme) {
        DarkColors.copy(primary = accent, secondary = accent)
    } else {
        LightColors.copy(primary = accent, secondary = accent)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
