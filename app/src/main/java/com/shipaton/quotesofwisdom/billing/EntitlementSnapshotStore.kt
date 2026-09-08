package com.shipaton.quotesofwisdom.billing

internal fun RevenueCatUiState.resolveEntitlementFailure(
    errorMessage: String
): RevenueCatUiState = copy(
    entitlementLoading = false,
    entitlementResolved = true,
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
