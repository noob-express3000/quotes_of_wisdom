package com.shipaton.quotesofwisdom.ui.theme

import androidx.compose.ui.text.font.FontFamily

data class QuoteFontOption(
    val id: String,
    val label: String,
    val fontFamily: FontFamily
)

val QuoteFonts = listOf(
    QuoteFontOption("default", "Default", FontFamily.Default),
    QuoteFontOption("serif", "Serif", FontFamily.Serif),
    QuoteFontOption("mono", "Mono", FontFamily.Monospace),
    QuoteFontOption("cursive", "Cursive", FontFamily.Cursive)
)

fun quoteFontById(id: String): QuoteFontOption =
    QuoteFonts.firstOrNull { it.id == id } ?: QuoteFonts.first()

const val QUOTE_FONT_PREFERENCES = "quote_font_preferences"
const val QUOTE_FONT_ID_KEY = "quote_font_id"
