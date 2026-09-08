package com.shipaton.quotesofwisdom.billing

import android.app.Activity
import android.content.Context
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.shipaton.quotesofwisdom.BuildConfig
import com.shipaton.quotesofwisdom.notifications.DailyWisdomNotifications
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PurchasePlan {
    WEEKLY,
    MONTHLY,
    LIFETIME
}

data class RevenueCatUiState(
    val configured: Boolean = false,
    val entitlementLoading: Boolean = true,
    val offeringsLoading: Boolean = true,
    val entitlementResolved: Boolean = false,
    val hasPro: Boolean = false,
    val availablePlans: Set<PurchasePlan> = emptySet(),
    val weeklyPrice: String? = null,
    val monthlyPrice: String? = null,
    val lifetimePrice: String? = null,
    val busy: Boolean = false,
    val entitlementErrorMessage: String? = null,
    val offeringsErrorMessage: String? = null,
    val operationErrorMessage: String? = null
) {
    val billingMessage: String?
        get() = operationErrorMessage ?: offeringsErrorMessage ?: entitlementErrorMessage
}

sealed interface BillingResult {
    data object Success : BillingResult
    data object Cancelled : BillingResult
    data class Error(val message: String) : BillingResult
}

class RevenueCatController(private val context: Context) {

    private val _state = MutableStateFlow(RevenueCatUiState())
    val state: StateFlow<RevenueCatUiState> = _state.asStateFlow()

    private val packages = mutableMapOf<PurchasePlan, Package>()

    fun configure() {
        val apiKey = BuildConfig.REVENUECAT_API_KEY
        if (apiKey.isBlank()) {
            _state.value = RevenueCatUiState(
                configured = false,
                entitlementLoading = false,
                offeringsLoading = false,
                entitlementResolved = true,
                offeringsErrorMessage = "Purchases are unavailable in this build."
            )
            return
        }

        if (!Purchases.isConfigured) {
            Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
            Purchases.configure(
                PurchasesConfiguration.Builder(context, apiKey).build()
            )
        }

        installCustomerInfoListener()
        _state.value = _state.value.copy(configured = true)
        refresh()
    }

    fun refresh() {
        if (!Purchases.isConfigured) return
        _state.value = _state.value.copy(
            entitlementLoading = true,
            offeringsLoading = true,
            entitlementErrorMessage = null,
            offeringsErrorMessage = null,
            operationErrorMessage = null
        )

        Purchases.sharedInstance.getCustomerInfoWith(
            onError = { error ->
                _state.value = _state.value.resolveEntitlementFailure(error.message)
            },
            onSuccess = { customerInfo ->
                applyCustomerInfo(customerInfo)
            }
        )

        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                _state.value = _state.value.copy(
                    offeringsLoading = false,
                    offeringsErrorMessage = error.message
                )
            },
            onSuccess = { offerings ->
                packages.clear()
                offerings.current?.availablePackages.orEmpty().forEach { rcPackage ->
                    planFor(rcPackage)?.let { plan -> packages[plan] = rcPackage }
                }

                _state.value = _state.value.copy(
                    offeringsLoading = false,
                    availablePlans = packages.keys.toSet(),
                    weeklyPrice = packages[PurchasePlan.WEEKLY]?.product?.price?.formatted,
                    monthlyPrice = packages[PurchasePlan.MONTHLY]?.product?.price?.formatted,
                    lifetimePrice = packages[PurchasePlan.LIFETIME]?.product?.price?.formatted,
                    offeringsErrorMessage = if (packages.isEmpty()) {
                        "No RevenueCat packages are attached to the current offering."
                    } else {
                        null
                    }
                )
            }
        )
    }

    fun purchase(
        activity: Activity,
        plan: PurchasePlan,
        onResult: (BillingResult) -> Unit
    ) {
        if (!Purchases.isConfigured) {
            onResult(BillingResult.Error("RevenueCat is not configured."))
            return
        }

        val rcPackage = packages[plan]
        if (rcPackage == null) {
            val message = "This plan is not configured in the current RevenueCat offering."
            _state.value = _state.value.copy(operationErrorMessage = message)
            onResult(BillingResult.Error(message))
            return
        }

        _state.value = _state.value.copy(busy = true, operationErrorMessage = null)
        val params = PurchaseParams.Builder(activity, rcPackage).build()
        Purchases.sharedInstance.purchaseWith(
            purchaseParams = params,
            onError = { error, userCancelled ->
                _state.value = _state.value.copy(
                    busy = false,
                    operationErrorMessage = if (userCancelled) null else error.message
                )
                onResult(
                    if (userCancelled) BillingResult.Cancelled
                    else BillingResult.Error(error.message)
                )
            },
            onSuccess = { _, customerInfo ->
                val hasPro = applyCustomerInfo(customerInfo)
                if (hasPro) {
                    _state.value = _state.value.copy(
                        busy = false,
                        operationErrorMessage = null
                    )
                    onResult(BillingResult.Success)
                } else {
                    val message =
                        "Purchase completed, but Pro access is not active. " +
                            "Use Restore purchases. If it still does not appear, retry after checking your connection."
                    _state.value = _state.value.copy(
                        busy = false,
                        operationErrorMessage = message
                    )
                    onResult(BillingResult.Error(message))
                }
            }
        )
    }

    fun restore(onResult: (BillingResult) -> Unit) {
        if (!Purchases.isConfigured) {
            onResult(BillingResult.Error("RevenueCat is not configured."))
            return
        }

        _state.value = _state.value.copy(busy = true, operationErrorMessage = null)
        Purchases.sharedInstance.restorePurchasesWith(
            onError = { error ->
                _state.value = _state.value.copy(
                    busy = false,
                    operationErrorMessage = error.message
                )
                onResult(BillingResult.Error(error.message))
            },
            onSuccess = { customerInfo ->
                val hasPro = applyCustomerInfo(customerInfo)
                if (hasPro) {
                    _state.value = _state.value.copy(
                        busy = false,
                        operationErrorMessage = null
                    )
                    onResult(BillingResult.Success)
                } else {
                    val message = "No active Pro purchase found."
                    _state.value = _state.value.copy(
                        busy = false,
                        operationErrorMessage = message
                    )
                    onResult(BillingResult.Error(message))
                }
            }
        )
    }

    private fun installCustomerInfoListener() {
        Purchases.sharedInstance.updatedCustomerInfoListener =
            object : UpdatedCustomerInfoListener {
                override fun onReceived(customerInfo: CustomerInfo) {
                    applyCustomerInfo(customerInfo)
                }
            }
    }

    private fun applyCustomerInfo(customerInfo: CustomerInfo): Boolean {
        val hasPro = customerInfo.entitlements[PRO_ENTITLEMENT]?.isActive == true
        if (!hasPro) enforceFreeReminderTime()

        _state.value = _state.value.resolveConfirmedEntitlement(hasPro)
        return hasPro
    }

    private fun enforceFreeReminderTime() {
        val currentHour = DailyWisdomNotifications.reminderHour(context)
        val currentMinute = DailyWisdomNotifications.reminderMinute(context)
        if (
            currentHour == DailyWisdomNotifications.DEFAULT_REMINDER_HOUR &&
            currentMinute == DailyWisdomNotifications.DEFAULT_REMINDER_MINUTE
        ) {
            return
        }

        DailyWisdomNotifications.setReminderTime(
            context,
            DailyWisdomNotifications.DEFAULT_REMINDER_HOUR,
            DailyWisdomNotifications.DEFAULT_REMINDER_MINUTE
        )
    }

    private fun planFor(rcPackage: Package): PurchasePlan? {
        val packageId = rcPackage.identifier.lowercase()
        val productId = rcPackage.product.id.lowercase()
        return when {
            packageId.contains("weekly") || productId == PRODUCT_WEEKLY -> PurchasePlan.WEEKLY
            packageId.contains("monthly") || productId == PRODUCT_MONTHLY -> PurchasePlan.MONTHLY
            packageId.contains("lifetime") || productId == PRODUCT_LIFETIME -> PurchasePlan.LIFETIME
            else -> null
        }
    }

    private companion object {
        const val PRO_ENTITLEMENT = "pro_access"
        const val PRODUCT_WEEKLY = "qow_weekly"
        const val PRODUCT_MONTHLY = "qow_monthly"
        const val PRODUCT_LIFETIME = "qow_lifetime"
    }
}
