package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Win7ColorScheme = lightColorScheme(
    primary = Win7AeroSkyBlue,
    onPrimary = Color.White,
    primaryContainer = Win7ExplorerCommandBarBottom,
    onPrimaryContainer = Win7AeroDeepBlue,
    secondary = Win7ControlPanelCategoryGreen,
    onSecondary = Color.White,
    background = Color(0xFF0E294B),
    onBackground = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFFF0F4F9),
    onSurfaceVariant = Color(0xFF334455)
)

private val Win7DarkColorScheme = darkColorScheme(
    primary = Win7AeroSkyBlue,
    onPrimary = Color.White,
    primaryContainer = Win7AeroDeepBlue,
    onPrimaryContainer = Color.White,
    secondary = Win7ControlPanelCategoryGreen,
    onSecondary = Color.White,
    background = Color(0xFF0A1E36),
    onBackground = Color.White,
    surface = Color(0xFF1B324B),
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) Win7DarkColorScheme else Win7ColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
