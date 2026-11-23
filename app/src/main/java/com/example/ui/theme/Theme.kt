package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = IndigoDeep,
    onPrimary = Color.White,
    primaryContainer = IndigoSoftContainer,
    onPrimaryContainer = IndigoOnContainer,
    secondary = TealEmerald,
    onSecondary = Color.White,
    secondaryContainer = TealSoftContainer,
    onSecondaryContainer = TealOnContainer,
    tertiary = AmberWarm,
    onTertiary = Color.White,
    tertiaryContainer = AmberSoftContainer,
    onTertiaryContainer = AmberOnContainer,
    background = ParchmentBackground,
    onBackground = InkPrimary,
    surface = ParchmentSurface,
    onSurface = InkPrimary,
    surfaceVariant = ParchmentVariant,
    onSurfaceVariant = InkSecondary,
    outline = ParchmentOutline,
    error = ErrorLight,
    errorContainer = ErrorContainerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkIndigoPrimary,
    onPrimary = Color(0xFF0A1229),
    primaryContainer = DarkIndigoContainer,
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = DarkTealSecondary,
    onSecondary = Color(0xFF032724),
    secondaryContainer = DarkTealContainer,
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = DarkAmberTertiary,
    onTertiary = Color(0xFF2D1B02),
    tertiaryContainer = DarkAmberContainer,
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = DarkBackground,
    onBackground = DarkInkPrimary,
    surface = DarkSurface,
    onSurface = DarkInkPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkInkSecondary,
    outline = DarkOutline,
    error = ErrorDark,
    errorContainer = ErrorContainerDark
)

val RafeeqShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun RafeeqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RafeeqTypography,
        shapes = RafeeqShapes,
        content = content
    )
}
