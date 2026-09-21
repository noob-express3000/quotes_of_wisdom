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

const val DEFAULT_QUOTE_FONT_ID = "default"

val QuoteFonts = listOf(
    QuoteFontOption(DEFAULT_QUOTE_FONT_ID, "Lora", bundledFont(R.font.lora_regular)),
    QuoteFontOption("cinzel", "Cinzel", bundledFont(R.font.cinzel_regular)),
    QuoteFontOption("cormorant", "Cormorant", bundledFont(R.font.cormorant_garamond_regular)),
    QuoteFontOption("playfair", "Playfair", bundledFont(R.font.playfair_display_regular)),
    QuoteFontOption("baskerville", "Baskerville", bundledFont(R.font.libre_baskerville_regular)),
    QuoteFontOption("merriweather", "Merriweather", bundledFont(R.font.merriweather_regular)),
    QuoteFontOption("crimson", "Crimson", bundledFont(R.font.crimson_text_regular)),
    QuoteFontOption("alegreya", "Alegreya", bundledFont(R.font.alegreya_regular)),
    QuoteFontOption("roboto_slab", "Roboto Slab", bundledFont(R.font.roboto_slab_regular)),
    QuoteFontOption("caveat", "Caveat", bundledFont(R.font.caveat_regular)),
    QuoteFontOption("noto_serif", "Noto Serif", bundledFont(R.font.noto_serif_regular)),
    QuoteFontOption("source_serif", "Source Serif", bundledFont(R.font.source_serif_pro_regular)),
    QuoteFontOption("libre_caslon", "Libre Caslon", bundledFont(R.font.libre_caslon_text_regular)),
    QuoteFontOption("prata", "Prata", bundledFont(R.font.prata_regular)),
    QuoteFontOption("neuton", "Neuton", bundledFont(R.font.neuton_regular)),
    QuoteFontOption("enriqueta", "Enriqueta", bundledFont(R.font.enriqueta_regular)),
    QuoteFontOption("coustard", "Coustard", bundledFont(R.font.coustard_regular)),
    QuoteFontOption("gilda_display", "Gilda Display", bundledFont(R.font.gilda_display_regular)),
    QuoteFontOption("bree_serif", "Bree Serif", bundledFont(R.font.bree_serif_regular)),
    QuoteFontOption("kurale", "Kurale", bundledFont(R.font.kurale_regular)),
    QuoteFontOption("philosopher", "Philosopher", bundledFont(R.font.philosopher_regular)),
    QuoteFontOption("andada", "Andada", bundledFont(R.font.andada_regular)),
    QuoteFontOption("averia_serif", "Averia Serif", bundledFont(R.font.averia_serif_libre_regular)),
    QuoteFontOption("lusitana", "Lusitana", bundledFont(R.font.lusitana_regular)),
    QuoteFontOption("kotta_one", "Kotta One", bundledFont(R.font.kotta_one_regular)),
    QuoteFontOption("caudex", "Caudex", bundledFont(R.font.caudex_regular)),
    QuoteFontOption("cantata_one", "Cantata One", bundledFont(R.font.cantata_one_regular)),
    QuoteFontOption("abril_fatface", "Abril Fatface", bundledFont(R.font.abril_fatface_regular)),
    QuoteFontOption("josefin_slab", "Josefin Slab", bundledFont(R.font.josefin_slab_regular)),
    QuoteFontOption("roboto_mono", "Roboto Mono", bundledFont(R.font.roboto_mono_regular)),
    QuoteFontOption("cutive_mono", "Cutive Mono", bundledFont(R.font.cutive_mono_regular)),
    QuoteFontOption("special_elite", "Special Elite", bundledFont(R.font.special_elite_regular)),
    QuoteFontOption("architects_daughter", "Architects Daughter", bundledFont(R.font.architects_daughter_regular)),
    QuoteFontOption("permanent_marker", "Permanent Marker", bundledFont(R.font.permanent_marker_regular)),
    QuoteFontOption("shadows_into_light", "Shadows Into Light", bundledFont(R.font.shadows_into_light_regular)),
    QuoteFontOption("dancing_script", "Dancing Script", bundledFont(R.font.dancing_script_regular)),
    QuoteFontOption("great_vibes", "Great Vibes", bundledFont(R.font.great_vibes_regular)),
    QuoteFontOption("kaushan_script", "Kaushan Script", bundledFont(R.font.kaushan_script_regular)),
    QuoteFontOption("italianno", "Italianno", bundledFont(R.font.italianno_regular)),
    QuoteFontOption("almendra", "Almendra", bundledFont(R.font.almendra_regular)),
    QuoteFontOption("medievalsharp", "MedievalSharp", bundledFont(R.font.medievalsharp_regular)),
    QuoteFontOption("new_rocker", "New Rocker", bundledFont(R.font.new_rocker_regular)),
    QuoteFontOption("metal_mania", "Metal Mania", bundledFont(R.font.metal_mania_regular)),
    QuoteFontOption("fruktur", "Fruktur", bundledFont(R.font.fruktur_regular)),
    QuoteFontOption("fondamento", "Fondamento", bundledFont(R.font.fondamento_regular)),
    QuoteFontOption("im_fell_great_primer", "IM FELL Great Primer", bundledFont(R.font.im_fell_great_primer_regular)),
    QuoteFontOption("marcellus_sc", "Marcellus SC", bundledFont(R.font.marcellus_sc_regular)),
    QuoteFontOption("unna", "Unna", bundledFont(R.font.unna_regular)),
    QuoteFontOption("germania_one", "Germania One", bundledFont(R.font.germania_one_regular)),
    QuoteFontOption("nixie_one", "Nixie One", bundledFont(R.font.nixie_one_regular))
)

fun quoteFontById(id: String): QuoteFontOption =
    QuoteFonts.firstOrNull { it.id == id } ?: QuoteFonts.first()

fun effectiveQuoteFontId(selectedId: String, hasPro: Boolean): String =
    if (hasPro) quoteFontById(selectedId).id else DEFAULT_QUOTE_FONT_ID

const val QUOTE_FONT_PREFERENCES = "quote_font_preferences"
const val QUOTE_FONT_ID_KEY = "quote_font_id"
