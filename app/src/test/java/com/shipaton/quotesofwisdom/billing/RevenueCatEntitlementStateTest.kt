package com.shipaton.quotesofwisdom.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RevenueCatEntitlementStateTest {
    @Test
    fun startupWithoutCustomerInfoStartsUnresolved() {
        val state = RevenueCatUiState()

        assertFalse(state.entitlementResolved)
        assertFalse(state.hasPro)
        assertTrue(state.entitlementLoading)
    }

    @Test
    fun transientFailurePreservesAlreadyKnownProState() {
        val state = RevenueCatUiState(
            entitlementResolved = true,
            hasPro = true
        ).resolveEntitlementFailure("Network unavailable")

        assertTrue(state.entitlementResolved)
        assertTrue(state.hasPro)
        assertFalse(state.entitlementLoading)
    }

    @Test
    fun transientFailureWithoutKnownProResolvesConservatively() {
        val state = RevenueCatUiState()
            .resolveEntitlementFailure("Network unavailable")

        assertTrue(state.entitlementResolved)
        assertFalse(state.hasPro)
        assertFalse(state.entitlementLoading)
    }

    @Test
    fun successfulInactiveStateCanReplacePreviouslyKnownPro() {
        val confirmedInactiveState = RevenueCatUiState(
            entitlementResolved = true,
            hasPro = true
        ).resolveConfirmedEntitlement(hasPro = false)

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
        val failedState = RevenueCatUiState(
            entitlementResolved = true,
            hasPro = true
        ).resolveEntitlementFailure("Network unavailable")

        assertFalse(
            shouldShowPaywallAfterConfirmedProLoss(
                lastResolvedHasPro = true,
                currentState = failedState
            )
        )
    }
}
