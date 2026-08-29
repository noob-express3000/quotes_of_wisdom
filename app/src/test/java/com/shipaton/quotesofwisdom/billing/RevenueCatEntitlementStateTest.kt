package com.shipaton.quotesofwisdom.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RevenueCatEntitlementStateTest {
    @Test
    fun confirmedProSeedsStartupWithoutShowingFreeState() {
        val state = RevenueCatUiState()
            .seedFromLastConfirmedEntitlement(lastConfirmedHasPro = true)

        assertTrue(state.entitlementResolved)
        assertTrue(state.hasPro)
    }

    @Test
    fun startupWithoutAConfirmedSnapshotRemainsUnresolved() {
        val state = RevenueCatUiState()
            .seedFromLastConfirmedEntitlement(lastConfirmedHasPro = null)

        assertFalse(state.entitlementResolved)
        assertFalse(state.hasPro)
    }

    @Test
    fun lastConfirmedFreeStillWaitsForFreshStartupResolution() {
        val state = RevenueCatUiState()
            .seedFromLastConfirmedEntitlement(lastConfirmedHasPro = false)

        assertFalse(state.entitlementResolved)
        assertFalse(state.hasPro)
    }

    @Test
    fun transientFailureCannotDowngradeLastConfirmedPro() {
        val state = RevenueCatUiState(hasPro = false)
            .resolveEntitlementFailure(
                errorMessage = "Network unavailable",
                lastConfirmedHasPro = true
            )

        assertTrue(state.entitlementResolved)
        assertTrue(state.hasPro)
        assertFalse(state.entitlementLoading)
    }

    @Test
    fun successfulInactiveStateCanStillReplaceProFallback() {
        val fallbackState = RevenueCatUiState()
            .seedFromLastConfirmedEntitlement(lastConfirmedHasPro = true)
        val confirmedInactiveState = fallbackState
            .resolveConfirmedEntitlement(hasPro = false)

        assertFalse(confirmedInactiveState.hasPro)
        assertTrue(
            shouldShowPaywallAfterConfirmedProLoss(
                lastResolvedHasPro = true,
                currentState = confirmedInactiveState
            )
        )
    }

    @Test
    fun transientFailureDoesNotTriggerPaywallForKnownPro() {
        val fallbackState = RevenueCatUiState()
            .resolveEntitlementFailure(
                errorMessage = "Network unavailable",
                lastConfirmedHasPro = true
            )

        assertFalse(
            shouldShowPaywallAfterConfirmedProLoss(
                lastResolvedHasPro = true,
                currentState = fallbackState
            )
        )
    }
}
