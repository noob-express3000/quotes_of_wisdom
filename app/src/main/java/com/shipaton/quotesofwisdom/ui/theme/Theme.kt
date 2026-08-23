package com.shipaton.quotesofwisdom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Dominant = Color(0xFFFFFDF7)
val Secondary = Color(0xFF2E2A26)
val Accent = Color(0xFFB65435)

private val AppColors = lightColorScheme(
    primary = Accent,
    onPrimary = Dominant,
    primaryContainer = Accent,
    onPrimaryContainer = Dominant,
    secondary = Secondary,
    onSecondary = Dominant,
    secondaryContainer = Secondary,
    onSecondaryContainer = Dominant,
    tertiary = Accent,
    onTertiary = Dominant,
    tertiaryContainer = Accent,
    onTertiaryContainer = Dominant,
    background = Dominant,
    onBackground = Secondary,
    surface = Dominant,
    onSurface = Secondary,
    surfaceVariant = Secondary,
    onSurfaceVariant = Dominant,
    surfaceTint = Accent,
    inverseSurface = Secondary,
    inverseOnSurface = Dominant,
    inversePrimary = Accent,
    outline = Secondary,
    outlineVariant = Secondary,
    scrim = Secondary,
    error = Accent,
    onError = Dominant,
    errorContainer = Accent,
    onErrorContainer = Dominant
)

@Composable
fun QuotesOfWisdomTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = Typography(),
        content = content
    )
}
