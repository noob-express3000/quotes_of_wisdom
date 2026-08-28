package com.shipaton.quotesofwisdom.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemePaletteTest {
    @Test
    fun catalogContainsOneHundredUniqueTwoWordThemes() {
        assertEquals(100, AppThemes.size)
        assertEquals(AppThemes.size, AppThemes.map { it.id }.toSet().size)
        assertEquals(AppThemes.size, AppThemes.map { it.name }.toSet().size)
        assertTrue(AppThemes.all { it.name.split(Regex("\\s+")).size <= 2 })
    }

    @Test
    fun bodyTextMeetsWcagAaContrastInEveryTheme() {
        AppThemes.forEach { palette ->
            assertTrue(
                "${palette.name} body contrast was ${contrast(palette.dominant, palette.secondary)}",
                contrast(palette.dominant, palette.secondary) >= 4.5f
            )
        }
    }

    @Test
    fun accentControlsMeetNonTextContrastInEveryTheme() {
        AppThemes.forEach { palette ->
            assertTrue(
                "${palette.name} accent contrast was ${contrast(palette.dominant, palette.accent)}",
                contrast(palette.dominant, palette.accent) >= 3.0f
            )
        }
    }

    private fun contrast(first: Color, second: Color): Float {
        val lighter = maxOf(first.luminance(), second.luminance())
        val darker = minOf(first.luminance(), second.luminance())
        return (lighter + 0.05f) / (darker + 0.05f)
    }
}
