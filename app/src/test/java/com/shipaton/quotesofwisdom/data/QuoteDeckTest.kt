package com.shipaton.quotesofwisdom.data

import com.shipaton.quotesofwisdom.model.Quote
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteDeckTest {
    private val quotes = listOf(
        Quote(1, "One", "Author A", "focus"),
        Quote(2, "Two", "Author B", "hope"),
        Quote(3, "Three", "Author C", "work")
    )

    @Test
    fun everyQuoteAppearsBeforeTheDeckRepeats() {
        val deck = QuoteDeck(quotes, Random(17))
        val firstCycle = List(quotes.size) { deck.next().id }

        assertEquals(quotes.map { it.id }.toSet(), firstCycle.toSet())
    }

    @Test
    fun reshuffleNeverRepeatsThePreviousQuoteImmediately() {
        val deck = QuoteDeck(quotes, Random(31))
        val firstCycle = List(quotes.size) { deck.next() }
        val firstAfterReshuffle = deck.next()

        assertNotEquals(firstCycle.last().id, firstAfterReshuffle.id)
    }

    @Test
    fun preferredClassificationCanBeSelectedFromTheRemainingDeck() {
        val deck = QuoteDeck(quotes, Random(9))
        val selected = deck.nextPreferred(setOf("hope"), preferenceChance = 1.0)

        assertTrue(selected.classification == "hope")
    }
}
