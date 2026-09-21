package com.shipaton.quotesofwisdom.notifications

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyWisdomNotificationsTest {
    @Test
    fun sameDaySelectsSameQuote() {
        val first = DailyWisdomNotifications.quoteIndexForDay(2026 * 400 + 264, 1063)
        val second = DailyWisdomNotifications.quoteIndexForDay(2026 * 400 + 264, 1063)

        assertEquals(first, second)
    }

    @Test
    fun selectedQuoteAlwaysFallsInsideDatabase() {
        for (dayToken in -10_000..10_000 step 137) {
            val index = DailyWisdomNotifications.quoteIndexForDay(dayToken, 1063)
            assertTrue(index in 0 until 1063)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyQuoteDatabaseIsRejected() {
        DailyWisdomNotifications.quoteIndexForDay(1, 0)
    }
}
