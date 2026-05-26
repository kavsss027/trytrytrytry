package com.gujaratifitness.app.core.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Premium HSL Color Palette
// Primary: HSL(14, 90%, 55%) -> Vibrant Orange-Red (#FA522D)
// Background: HSL(220, 25%, 8%) -> Deep Dark Gray-Blue (#0B0E14)
// Surface: HSL(220, 20%, 14%) -> Dark Card Surface (#1D222E)
val PrimaryColor = Color(0xFFFA522D)
val PrimaryContainerColor = Color(0xFF3D150B)
val SecondaryColor = Color(0xFF222B38)
val BackgroundColor = Color(0xFF0B0E14)
val SurfaceColor = Color(0xFF161A24)
val TextPrimaryColor = Color(0xFFF1F2F4)
val TextSecondaryColor = Color(0xFF8E9AA8)
val AccentColor = Color(0xFF2D8CFF) // Active light blue for accents

val AppColorScheme: ColorScheme = darkColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryContainerColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = Color.White,
    onSecondary = TextPrimaryColor,
    onBackground = TextPrimaryColor,
    onSurface = TextPrimaryColor
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
