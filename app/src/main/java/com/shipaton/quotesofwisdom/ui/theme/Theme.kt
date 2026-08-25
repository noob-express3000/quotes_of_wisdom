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
    AppThemePalette("ivory_jade", "Ivory Jade", Color(0xFFFFFBEE), Color(0xFF18392B), Color(0xFFCC6D3D)),
    AppThemePalette("neon_noir", "Neon Noir", Color(0xFF0A0A0F), Color(0xFFF5F5F5), Color(0xFF39FF14)),
    AppThemePalette("cobalt_signal", "Cobalt Signal", Color(0xFF0A2463), Color(0xFFF6F7FB), Color(0xFFFFC857)),
    AppThemePalette("crimson_ink", "Crimson Ink", Color(0xFF1C0B0E), Color(0xFFF7E8EA), Color(0xFFFF4D6D)),
    AppThemePalette("electric_violet", "Electric Violet", Color(0xFF171221), Color(0xFFF4EEFF), Color(0xFFB86BFF)),
    AppThemePalette("carbon_cyan", "Carbon Cyan", Color(0xFF111820), Color(0xFFEAFBFF), Color(0xFF00D9FF)),
    AppThemePalette("obsidian_orange", "Obsidian Orange", Color(0xFF111111), Color(0xFFF5EEE7), Color(0xFFFF7A00)),
    AppThemePalette("royal_cyan", "Royal Cyan", Color(0xFF0D1B2A), Color(0xFFE0EAF3), Color(0xFF00B4D8)),
    AppThemePalette("ruby_sand", "Ruby Sand", Color(0xFFFFE8D6), Color(0xFF3B1725), Color(0xFFC9184A)),
    AppThemePalette("acid_plum", "Acid Plum", Color(0xFF24112F), Color(0xFFF6F0F8), Color(0xFFC8FF3D)),
    AppThemePalette("mono_red", "Mono Red", Color(0xFFF3F3F3), Color(0xFF161616), Color(0xFFE10600)),
    AppThemePalette("petrol_amber", "Petrol Amber", Color(0xFF0C2D33), Color(0xFFEAF2F2), Color(0xFFFFB000)),
    AppThemePalette("ink_pink", "Ink Pink", Color(0xFF16131A), Color(0xFFF6EEF4), Color(0xFFFF4FA3)),
    AppThemePalette("navy_mint", "Navy Mint", Color(0xFF071E3D), Color(0xFFEAF4F4), Color(0xFF2DE2A6)),
    AppThemePalette("charcoal_yellow", "Charcoal Yellow", Color(0xFF232323), Color(0xFFF3F1E8), Color(0xFFFFD400)),
    AppThemePalette("deep_teal_pink", "Deep Teal Pink", Color(0xFF062F34), Color(0xFFEAF4F2), Color(0xFFFF5C8A)),
    AppThemePalette("black_cream_blue", "Black Cream Blue", Color(0xFF0D0D10), Color(0xFFF4EFE4), Color(0xFF4D8DFF)),
    AppThemePalette("storm_orange", "Storm Orange", Color(0xFFE9EDF0), Color(0xFF1F2A35), Color(0xFFFF6A00)),
    AppThemePalette("oxblood_ice", "Oxblood Ice", Color(0xFF3A0D18), Color(0xFFF4F7FA), Color(0xFF7FDBFF)),
    AppThemePalette("ultraviolet_sand", "Ultraviolet Sand", Color(0xFFF5E7CF), Color(0xFF231A34), Color(0xFF7B2FFF)),
    AppThemePalette("copper_night", "Copper Night", Color(0xFF101721), Color(0xFFEDE6DD), Color(0xFFC87333)),
    AppThemePalette("mint_black_red", "Mint Black Red", Color(0xFFE5F7EF), Color(0xFF111817), Color(0xFFE63946)),
    AppThemePalette("scarlet_navy", "Scarlet Navy", Color(0xFFF3F6FA), Color(0xFF0B1F3A), Color(0xFFD90429)),
    AppThemePalette("gold_black_cream", "Gold Black Cream", Color(0xFFF8F1E1), Color(0xFF151515), Color(0xFFC89B3C)),
    AppThemePalette("blueprint", "Blueprint", Color(0xFF0A3D91), Color(0xFFF2F7FF), Color(0xFFFFD166)),
    AppThemePalette("noir_rose", "Noir Rose", Color(0xFF0E0C10), Color(0xFFF5EEF2), Color(0xFFE07A9B)),
    AppThemePalette("pine_signal", "Pine Signal", Color(0xFF102A25), Color(0xFFE9F0EB), Color(0xFFFFC400)),
    AppThemePalette("magenta_stone", "Magenta Stone", Color(0xFFECE9ED), Color(0xFF27222A), Color(0xFFD81B60)),
    AppThemePalette("amber_cobalt", "Amber Cobalt", Color(0xFF0B1D51), Color(0xFFF1F5FF), Color(0xFFFFA000)),
    AppThemePalette("steel_lime", "Steel Lime", Color(0xFF1E2732), Color(0xFFE8EDF2), Color(0xFFB6FF3B)),
    AppThemePalette("violet_gold", "Violet Gold", Color(0xFF24113B), Color(0xFFF4EAF8), Color(0xFFFFC857))
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
