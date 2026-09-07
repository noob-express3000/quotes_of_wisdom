package com.shipaton.quotesofwisdom.ui.theme

import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

data class QuoteFontOption(
    val id: String,
    val label: String,
    val fontFamily: FontFamily
)

private fun deviceFamily(primary: String, fallback: String = "sans-serif") = FontFamily(
    Font(DeviceFontFamilyName(primary)),
    Font(DeviceFontFamilyName(fallback))
)

val QuoteFonts = listOf(
    QuoteFontOption("default", "Default", FontFamily.Default),
    QuoteFontOption("sans", "Sans", FontFamily.SansSerif),
    QuoteFontOption("serif", "Serif", FontFamily.Serif),
    QuoteFontOption("mono", "Mono", FontFamily.Monospace),
    QuoteFontOption("cursive", "Cursive", FontFamily.Cursive),
    QuoteFontOption("condensed", "Condensed", deviceFamily("sans-serif-condensed")),
    QuoteFontOption("casual", "Casual", deviceFamily("casual")),
    QuoteFontOption("small_caps", "Small Caps", deviceFamily("sans-serif-smallcaps")),
    QuoteFontOption("serif_mono", "Serif Mono", deviceFamily("serif-monospace", "monospace")),
    QuoteFontOption("light", "Light", deviceFamily("sans-serif-light")),
    QuoteFontOption("heavy", "Heavy", deviceFamily("sans-serif-black")),
    QuoteFontOption("condensed_light", "Condensed Light", deviceFamily("sans-serif-condensed-light", "sans-serif-condensed"))
)

fun quoteFontById(id: String): QuoteFontOption =
    QuoteFonts.firstOrNull { it.id == id } ?: QuoteFonts.first()

const val QUOTE_FONT_PREFERENCES = "quote_font_preferences"
const val QUOTE_FONT_ID_KEY = "quote_font_id"
