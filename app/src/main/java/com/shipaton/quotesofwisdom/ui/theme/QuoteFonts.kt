package com.shipaton.quotesofwisdom.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.shipaton.quotesofwisdom.R

data class QuoteFontOption(
    val id: String,
    val label: String,
    val fontFamily: FontFamily
)

private fun bundledFont(resourceId: Int) = FontFamily(Font(resourceId))

val QuoteFonts = listOf(
    QuoteFontOption("default", "Lora", bundledFont(R.font.lora_regular)),
    QuoteFontOption("cinzel", "Cinzel", bundledFont(R.font.cinzel_regular)),
    QuoteFontOption("cormorant", "Cormorant", bundledFont(R.font.cormorant_garamond_regular)),
    QuoteFontOption("playfair", "Playfair", bundledFont(R.font.playfair_display_regular)),
    QuoteFontOption("baskerville", "Baskerville", bundledFont(R.font.libre_baskerville_regular)),
    QuoteFontOption("merriweather", "Merriweather", bundledFont(R.font.merriweather_regular)),
    QuoteFontOption("crimson", "Crimson", bundledFont(R.font.crimson_text_regular)),
    QuoteFontOption("alegreya", "Alegreya", bundledFont(R.font.alegreya_regular)),
    QuoteFontOption("roboto_slab", "Roboto Slab", bundledFont(R.font.roboto_slab_regular)),
    QuoteFontOption("caveat", "Caveat", bundledFont(R.font.caveat_regular))
)

fun quoteFontById(id: String): QuoteFontOption =
    QuoteFonts.firstOrNull { it.id == id } ?: QuoteFonts.first()

const val QUOTE_FONT_PREFERENCES = "quote_font_preferences"
const val QUOTE_FONT_ID_KEY = "quote_font_id"
