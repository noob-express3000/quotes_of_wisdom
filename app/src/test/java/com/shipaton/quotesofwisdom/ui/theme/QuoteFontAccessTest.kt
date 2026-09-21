package com.shipaton.quotesofwisdom.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class QuoteFontAccessTest {
    @Test
    fun libraryContainsExactlyFiftyFonts() {
        assertEquals(50, QuoteFonts.size)
        assertEquals(50, QuoteFonts.map { it.id }.toSet().size)
    }

    @Test
    fun downgradeFallsBackToDefaultWithoutDeletingSavedChoice() {
        val selected = "metal_mania"

        assertEquals(DEFAULT_QUOTE_FONT_ID, effectiveQuoteFontId(selected, hasPro = false))
        assertEquals(selected, effectiveQuoteFontId(selected, hasPro = true))
    }

    @Test
    fun unknownProFontFallsBackToDefault() {
        assertEquals(DEFAULT_QUOTE_FONT_ID, effectiveQuoteFontId("missing_font", hasPro = true))
        assertNotEquals("missing_font", effectiveQuoteFontId("missing_font", hasPro = true))
    }
}
