package com.shipaton.quotesofwisdom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Dominant = Color(0xFFFFFDF7)
private val Secondary = Color(0xFFF2E8D5)
private val Accent = Color(0xFFB65435)
private val Ink = Color(0xFF2E2A26)

private val AppColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Secondary,
    onSecondary = Ink,
    background = Dominant,
    onBackground = Ink,
    surface = Dominant,
    onSurface = Ink,
    surfaceVariant = Secondary,
    onSurfaceVariant = Ink
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
