package com.shipaton.quotesofwisdom.ui.home

import com.shipaton.quotesofwisdom.data.QuoteDeck
import com.shipaton.quotesofwisdom.model.Quote
import com.shipaton.quotesofwisdom.notifications.DailyWisdomNotifications
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationQuoteSelectionTest {
    private val quotes = listOf(
        Quote(11, "Eleven", "Author A", "focus"),
        Quote(22, "Twenty two", "Author B", "hope"),
        Quote(33, "Thirty three", "Author C", "work"),
        Quote(44, "Forty four", "Author D", "courage")
    )

    @Test
    fun notificationQuoteIdResolvesToExactDisplayedQuote() {
        val notificationQuote = DailyWisdomNotifications.quoteForDay(quotes, 2026 * 400 + 264)
        val deck = QuoteDeck(quotes, Random(7))

        val displayed = consumeNotificationQuote(deck, quotes, notificationQuote.id)

        assertEquals(notificationQuote, displayed)
    }

    @Test
    fun notificationOpenedAfterDayChangesStillUsesOriginalQuoteId() {
        val notificationQuote = DailyWisdomNotifications.quoteForDay(quotes, 2026 * 400 + 264)
        val nextDayQuote = DailyWisdomNotifications.quoteForDay(quotes, 2026 * 400 + 265)
        val deck = QuoteDeck(quotes, Random(9))

        val displayed = consumeNotificationQuote(deck, quotes, notificationQuote.id)

        assertNotEquals(notificationQuote.id, nextDayQuote.id)
        assertEquals(notificationQuote, displayed)
    }

    @Test
    fun invalidOrMissingNotificationQuoteIdFallsBackWithoutConsumingDeck() {
        val expectedDeck = QuoteDeck(quotes, Random(13))
        val expectedFirst = expectedDeck.next()

        val invalidDeck = QuoteDeck(quotes, Random(13))
        assertNull(consumeNotificationQuote(invalidDeck, quotes, 999_999))
        assertEquals(expectedFirst, invalidDeck.next())

        val missingDeck = QuoteDeck(quotes, Random(13))
        assertNull(consumeNotificationQuote(missingDeck, quotes, null))
        assertEquals(expectedFirst, missingDeck.next())
    }

    @Test
    fun consumedNotificationQuoteIsNotImmediatelyRepeatedByNext() {
        val deck = QuoteDeck(quotes, Random(21))
        val notificationQuote = quotes[2]

        val displayed = consumeNotificationQuote(deck, quotes, notificationQuote.id)
        val next = deck.next()

        assertEquals(notificationQuote, displayed)
        assertNotEquals(notificationQuote.id, next.id)
    }

    @Test
    fun normalLaunchStillUsesOrdinaryShuffledDeck() {
        val expectedDeck = QuoteDeck(quotes, Random(31))
        val expectedOpening = expectedDeck.next()

        val launchDeck = QuoteDeck(quotes, Random(31))
        assertNull(consumeNotificationQuote(launchDeck, quotes, null))
        val actualOpening = launchDeck.next()

        assertEquals(expectedOpening, actualOpening)
    }
}
