package com.shipaton.quotesofwisdom.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LocalAccessPolicyTest {
    private val dayMillis = 24L * 60L * 60L * 1000L
    private val firstSeen = 1_700_000_000_000L

    @Test
    fun newInstallStartsInTrial() {
        assertEquals(
            AccessState.TRIAL_ACTIVE,
            LocalAccessPolicy.stateFor(firstSeenMillis = 0L, nowMillis = firstSeen)
        )
    }

    @Test
    fun trialEndsAtThirtyDays() {
        assertEquals(
            AccessState.TRIAL_ACTIVE,
            LocalAccessPolicy.stateFor(
                firstSeenMillis = firstSeen,
                nowMillis = firstSeen + 30L * dayMillis - 1L
            )
        )
        assertEquals(
            AccessState.GRACE_TEXT_ONLY,
            LocalAccessPolicy.stateFor(
                firstSeenMillis = firstSeen,
                nowMillis = firstSeen + 30L * dayMillis
            )
        )
    }

    @Test
    fun graceEndsAtThirtyThreeDays() {
        assertEquals(
            AccessState.GRACE_TEXT_ONLY,
            LocalAccessPolicy.stateFor(
                firstSeenMillis = firstSeen,
                nowMillis = firstSeen + 33L * dayMillis - 1L
            )
        )
        assertEquals(
            AccessState.LOCKED,
            LocalAccessPolicy.stateFor(
                firstSeenMillis = firstSeen,
                nowMillis = firstSeen + 33L * dayMillis
            )
        )
    }

    @Test
    fun proAlwaysWins() {
        assertEquals(
            AccessState.PRO,
            LocalAccessPolicy.stateFor(
                firstSeenMillis = firstSeen,
                nowMillis = firstSeen + 365L * dayMillis,
                hasPro = true
            )
        )
    }

    @Test
    fun clockRollbackCannotExtendPreviouslyObservedTrialTime() {
        assertEquals(
            AccessState.GRACE_TEXT_ONLY,
            LocalAccessPolicy.stateFor(
                firstSeenMillis = firstSeen,
                nowMillis = firstSeen - dayMillis,
                latestSeenMillis = firstSeen + 31L * dayMillis
            )
        )
    }
}
