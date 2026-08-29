package com.shipaton.quotesofwisdom.billing

import android.content.Context

internal class EntitlementSnapshotStore(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun readLastConfirmedPro(): Boolean? =
        if (preferences.contains(KEY_HAS_PRO)) {
            preferences.getBoolean(KEY_HAS_PRO, false)
        } else {
            null
        }

    fun writeLastConfirmedPro(hasPro: Boolean) {
        preferences.edit().putBoolean(KEY_HAS_PRO, hasPro).apply()
    }

    private companion object {
        const val FILE_NAME = "revenuecat_entitlement_cache"
        const val KEY_HAS_PRO = "last_confirmed_has_pro"
    }
}

internal fun RevenueCatUiState.seedFromLastConfirmedEntitlement(
    lastConfirmedHasPro: Boolean?
): RevenueCatUiState = if (lastConfirmedHasPro == true) {
    copy(
        entitlementResolved = true,
        hasPro = true
    )
} else {
    this
}

internal fun RevenueCatUiState.resolveEntitlementFailure(
    errorMessage: String,
    lastConfirmedHasPro: Boolean?
): RevenueCatUiState = copy(
    entitlementLoading = false,
    entitlementResolved = true,
    hasPro = lastConfirmedHasPro ?: hasPro,
    entitlementErrorMessage = errorMessage
)

internal fun RevenueCatUiState.resolveConfirmedEntitlement(
    hasPro: Boolean
): RevenueCatUiState = copy(
    configured = true,
    entitlementLoading = false,
    entitlementResolved = true,
    hasPro = hasPro,
    entitlementErrorMessage = null
)

internal fun shouldShowPaywallAfterConfirmedProLoss(
    lastResolvedHasPro: Boolean?,
    currentState: RevenueCatUiState
): Boolean = lastResolvedHasPro == true &&
    currentState.entitlementResolved &&
    !currentState.hasPro
