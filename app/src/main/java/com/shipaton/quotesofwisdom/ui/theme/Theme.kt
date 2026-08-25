package com.shipaton.quotesofwisdom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class AppThemePalette(
    val id: String,
    val name: String,
    val dominant: Color,
    val secondary: Color,
    val accent: Color,
    val isFree: Boolean = false
)

val AppThemes = listOf(
    AppThemePalette("parchment", "Parchment", Color(0xFFFFFDF7), Color(0xFF2E2A26), Color(0xFFB65435), true),
    AppThemePalette("midnight_gold", "Midnight Gold", Color(0xFF101820), Color(0xFFF2E9D8), Color(0xFFD9A441), true),
    AppThemePalette("sage_ink", "Sage Ink", Color(0xFFE8EFE5), Color(0xFF24322A), Color(0xFF7D4F50)),
    AppThemePalette("ocean_coral", "Ocean Coral", Color(0xFFE9F4F6), Color(0xFF12343B), Color(0xFFFF6B5E)),
    AppThemePalette("cherry_cream", "Cherry Cream", Color(0xFFFFF4E6), Color(0xFF4A1C2B), Color(0xFFD7263D)),
    AppThemePalette("lavender_ink", "Lavender Ink", Color(0xFFF0EAF7), Color(0xFF2B2140), Color(0xFF8D6AC8)),
    AppThemePalette("teal_copper", "Teal Copper", Color(0xFFE8F2F0), Color(0xFF173F3A), Color(0xFFC66A3D)),
    AppThemePalette("sand_navy", "Sand Navy", Color(0xFFF4E9D8), Color(0xFF172A3A), Color(0xFFCA7B3D)),
    AppThemePalette("rosewood", "Rosewood", Color(0xFFF8ECEA), Color(0xFF45252A), Color(0xFFC57C78)),
    AppThemePalette("forest_ember", "Forest Ember", Color(0xFF14251D), Color(0xFFE8E1D4), Color(0xFFE56B3F)),
    AppThemePalette("ice_cobalt", "Ice Cobalt", Color(0xFFEAF3FF), Color(0xFF16213E), Color(0xFF2F6BFF)),
    AppThemePalette("espresso", "Espresso Cream", Color(0xFFF3E7D3), Color(0xFF33241C), Color(0xFFB8703C)),
    AppThemePalette("plum_champagne", "Plum Champagne", Color(0xFF2F1B2E), Color(0xFFF5E9DD), Color(0xFFD6A85F)),
    AppThemePalette("slate_mint", "Slate Mint", Color(0xFF202A33), Color(0xFFE8F1EC), Color(0xFF6FD0B0)),
    AppThemePalette("sunset_charcoal", "Sunset Charcoal", Color(0xFFFFE9DD), Color(0xFF2C2C2C), Color(0xFFFF6F59)),
    AppThemePalette("burgundy_pearl", "Burgundy Pearl", Color(0xFFF7EFEA), Color(0xFF511F2B), Color(0xFFD6A756)),
    AppThemePalette("moss_clay", "Moss Clay", Color(0xFFEAE8D8), Color(0xFF2F3A2D), Color(0xFFB95C3D)),
    AppThemePalette("azure_lemon", "Azure Lemon", Color(0xFFE9F5FF), Color(0xFF15324A), Color(0xFFE8B923)),
    AppThemePalette("graphite_lilac", "Graphite Lilac", Color(0xFF24242A), Color(0xFFF0EDF7), Color(0xFFB89CFF)),
    AppThemePalette("ivory_jade", "Ivory Jade", Color(0xFFFFFBEE), Color(0xFF18392B), Color(0xFFCC6D3D))
)

val DefaultTheme = AppThemes.first()

fun themeById(id: String): AppThemePalette = AppThemes.firstOrNull { it.id == id } ?: DefaultTheme

private fun appColorScheme(palette: AppThemePalette) = lightColorScheme(
    primary = palette.accent,
    onPrimary = palette.secondary,
    primaryContainer = palette.accent,
    onPrimaryContainer = palette.secondary,
    inversePrimary = palette.accent,
    secondary = palette.secondary,
    onSecondary = palette.accent,
    secondaryContainer = palette.secondary,
    onSecondaryContainer = palette.accent,
    tertiary = palette.accent,
    onTertiary = palette.secondary,
    tertiaryContainer = palette.accent,
    onTertiaryContainer = palette.secondary,
    background = palette.dominant,
    onBackground = palette.secondary,
    surface = palette.dominant,
    onSurface = palette.secondary,
    surfaceVariant = palette.secondary,
    onSurfaceVariant = palette.accent,
    surfaceTint = palette.accent,
    inverseSurface = palette.secondary,
    inverseOnSurface = palette.dominant,
    error = palette.accent,
    onError = palette.secondary,
    errorContainer = palette.accent,
    onErrorContainer = palette.secondary,
    outline = palette.secondary,
    outlineVariant = palette.secondary,
    scrim = palette.secondary,
    surfaceBright = palette.dominant,
    surfaceContainer = palette.dominant,
    surfaceContainerHigh = palette.dominant,
    surfaceContainerHighest = palette.dominant,
    surfaceContainerLow = palette.dominant,
    surfaceContainerLowest = palette.dominant,
    surfaceDim = palette.dominant,
    primaryFixed = palette.accent,
    primaryFixedDim = palette.accent,
    onPrimaryFixed = palette.secondary,
    onPrimaryFixedVariant = palette.secondary,
    secondaryFixed = palette.secondary,
    secondaryFixedDim = palette.secondary,
    onSecondaryFixed = palette.accent,
    onSecondaryFixedVariant = palette.accent,
    tertiaryFixed = palette.accent,
    tertiaryFixedDim = palette.accent,
    onTertiaryFixed = palette.secondary,
    onTertiaryFixedVariant = palette.secondary
)

@Composable
fun QuotesOfWisdomTheme(
    palette: AppThemePalette = DefaultTheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = appColorScheme(palette),
        typography = Typography(),
        content = content
    )
}
