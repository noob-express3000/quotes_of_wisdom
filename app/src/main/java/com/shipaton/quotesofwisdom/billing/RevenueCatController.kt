package com.shipaton.quotesofwisdom.billing

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
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
import java.security.MessageDigest
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
    val loading: Boolean = true,
    val hasPro: Boolean = false,
    val weeklyPrice: String? = null,
    val monthlyPrice: String? = null,
    val lifetimePrice: String? = null,
    val busy: Boolean = false,
    val errorMessage: String? = null
)

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
        if (Purchases.isConfigured) {
            refresh()
            return
        }

        val apiKey = BuildConfig.REVENUECAT_API_KEY
        if (apiKey.isBlank()) {
            _state.value = RevenueCatUiState(
                configured = false,
                loading = false,
                errorMessage = "RevenueCat is not configured for this build."
            )
            return
        }

        Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
        Purchases.configure(
            PurchasesConfiguration.Builder(context, apiKey)
                .appUserID(DeviceScopedRevenueCatId.create(context))
                .build()
        )

        Purchases.sharedInstance.updatedCustomerInfoListener =
            object : UpdatedCustomerInfoListener {
                override fun onReceived(customerInfo: CustomerInfo) {
                    applyCustomerInfo(customerInfo)
                }
            }

        _state.value = _state.value.copy(configured = true)
        refresh()
    }

    fun refresh() {
        if (!Purchases.isConfigured) return
        _state.value = _state.value.copy(loading = true, errorMessage = null)

        Purchases.sharedInstance.getCustomerInfoWith(
            onError = { error ->
                _state.value = _state.value.copy(
                    loading = false,
                    errorMessage = error.message
                )
            },
            onSuccess = ::applyCustomerInfo
        )

        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                _state.value = _state.value.copy(
                    loading = false,
                    errorMessage = error.message
                )
            },
            onSuccess = { offerings ->
                packages.clear()
                offerings.current?.availablePackages.orEmpty().forEach { rcPackage ->
                    planFor(rcPackage)?.let { plan -> packages[plan] = rcPackage }
                }

                _state.value = _state.value.copy(
                    loading = false,
                    weeklyPrice = packages[PurchasePlan.WEEKLY]?.storeProduct?.price?.formatted,
                    monthlyPrice = packages[PurchasePlan.MONTHLY]?.storeProduct?.price?.formatted,
                    lifetimePrice = packages[PurchasePlan.LIFETIME]?.storeProduct?.price?.formatted,
                    errorMessage = if (packages.isEmpty()) {
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
        val rcPackage = packages[plan]
        if (rcPackage == null) {
            val message = "This plan is not configured in the current RevenueCat offering."
            _state.value = _state.value.copy(errorMessage = message)
            onResult(BillingResult.Error(message))
            return
        }

        _state.value = _state.value.copy(busy = true, errorMessage = null)
        val params = PurchaseParams.Builder(activity, rcPackage).build()
        Purchases.sharedInstance.purchaseWith(
            purchaseParams = params,
            onError = { error, userCancelled ->
                _state.value = _state.value.copy(
                    busy = false,
                    errorMessage = if (userCancelled) null else error.message
                )
                onResult(
                    if (userCancelled) BillingResult.Cancelled
                    else BillingResult.Error(error.message)
                )
            },
            onSuccess = { _, customerInfo ->
                applyCustomerInfo(customerInfo)
                _state.value = _state.value.copy(busy = false)
                onResult(BillingResult.Success)
            }
        )
    }

    fun restore(onResult: (BillingResult) -> Unit) {
        if (!Purchases.isConfigured) {
            onResult(BillingResult.Error("RevenueCat is not configured."))
            return
        }

        _state.value = _state.value.copy(busy = true, errorMessage = null)
        Purchases.sharedInstance.restorePurchasesWith(
            onError = { error ->
                _state.value = _state.value.copy(busy = false, errorMessage = error.message)
                onResult(BillingResult.Error(error.message))
            },
            onSuccess = { customerInfo ->
                applyCustomerInfo(customerInfo)
                _state.value = _state.value.copy(busy = false)
                onResult(BillingResult.Success)
            }
        )
    }

    private fun applyCustomerInfo(customerInfo: CustomerInfo) {
        val hasPro = customerInfo.entitlements[PRO_ENTITLEMENT]?.isActive == true
        _state.value = _state.value.copy(
            configured = true,
            loading = false,
            hasPro = hasPro,
            errorMessage = null
        )
    }

    private fun planFor(rcPackage: Package): PurchasePlan? {
        val packageId = rcPackage.identifier.lowercase()
        val productId = rcPackage.storeProduct.id.lowercase()
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

private object DeviceScopedRevenueCatId {

    fun create(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ).orEmpty()
        val signingFingerprint = signingFingerprint(context)
        val material = "$androidId|${context.packageName}|$signingFingerprint"
        return "qow_${sha256(material)}"
    }

    private fun signingFingerprint(context: Context): String {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
        }

        val certificateBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.signingInfo
                ?.apkContentsSigners
                ?.firstOrNull()
                ?.toByteArray()
        } else {
            @Suppress("DEPRECATION")
            packageInfo.signatures?.firstOrNull()?.toByteArray()
        }.orEmpty()

        return certificateBytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString(separator = "") { byte -> "%02x".format(byte) }
}
