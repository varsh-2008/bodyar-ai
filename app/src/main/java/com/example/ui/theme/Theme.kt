package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val BodyArColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = OnCyanDark,
    primaryContainer = NavyCardElevated,
    onPrimaryContainer = ElectricCyanGlow,
    secondary = CobaltBlue,
    onSecondary = TextPrimary,
    secondaryContainer = NavyCardSurface,
    onSecondaryContainer = SkyBlue,
    tertiary = BioluminescentTeal,
    onTertiary = OnCyanDark,
    background = DeepNavyBg,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderCyan,
    outlineVariant = GlassBorderBright,
    error = CoralRed,
    onError = TextPrimary,
)

private val BodyArShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun BodyArTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = BodyArColorScheme,
        typography = BodyArTypography,
        shapes = BodyArShapes,
        content = content,
    )
}
