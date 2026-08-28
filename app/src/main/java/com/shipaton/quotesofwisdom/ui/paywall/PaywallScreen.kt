package com.shipaton.quotesofwisdom.ui.paywall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shipaton.quotesofwisdom.billing.PurchasePlan
import com.shipaton.quotesofwisdom.model.AccessState

@Composable
fun PaywallScreen(
    accessState: AccessState,
    canDismiss: Boolean,
    weeklyPrice: String?,
    monthlyPrice: String?,
    lifetimePrice: String?,
    availablePlans: Set<PurchasePlan>,
    billingLoading: Boolean,
    billingBusy: Boolean,
    billingMessage: String?,
    onDismiss: () -> Unit,
    onChoosePlan: (PurchasePlan) -> Unit,
    onRestorePurchases: () -> Unit,
    onRetryBilling: () -> Unit
) {
    var showInfo by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showInfo = true }) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = "Why upgrade now",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }

                if (canDismiss) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Close subscription page",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                } else {
                    Spacer(Modifier.height(48.dp))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentPadding = PaddingValues(top = 64.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text(
                        text = "Choose your plan",
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                item { Spacer(Modifier.height(30.dp)) }

                item {
                    PlanCard(
                        title = "Weekly",
                        price = planPriceLabel(
                            price = weeklyPrice,
                            cadence = "week",
                            loading = billingLoading,
                            available = PurchasePlan.WEEKLY in availablePlans
                        ),
                        enabled = !billingBusy && weeklyPrice != null &&
                            PurchasePlan.WEEKLY in availablePlans,
                        onClick = { onChoosePlan(PurchasePlan.WEEKLY) }
                    )
                }

                item { Spacer(Modifier.height(18.dp)) }

                item {
                    PlanCard(
                        title = "Monthly",
                        price = planPriceLabel(
                            price = monthlyPrice,
                            cadence = "month",
                            loading = billingLoading,
                            available = PurchasePlan.MONTHLY in availablePlans
                        ),
                        emphasized = true,
                        enabled = !billingBusy && monthlyPrice != null &&
                            PurchasePlan.MONTHLY in availablePlans,
                        onClick = { onChoosePlan(PurchasePlan.MONTHLY) }
                    )
                }

                item { Spacer(Modifier.height(18.dp)) }

                item {
                    PlanCard(
                        title = "Lifetime",
                        price = planPriceLabel(
                            price = lifetimePrice,
                            cadence = "once",
                            loading = billingLoading,
                            available = PurchasePlan.LIFETIME in availablePlans
                        ),
                        enabled = !billingBusy && lifetimePrice != null &&
                            PurchasePlan.LIFETIME in availablePlans,
                        onClick = { onChoosePlan(PurchasePlan.LIFETIME) }
                    )
                }

                item { Spacer(Modifier.height(18.dp)) }

                if (billingMessage != null) {
                    item {
                        Text(
                            text = billingMessage,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                    item {
                        Text(
                            text = "Retry",
                            modifier = Modifier
                                .clickable(
                                    enabled = !billingBusy,
                                    role = Role.Button,
                                    onClick = onRetryBilling
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Text(
                        text = if (billingBusy) "Working..." else "Restore purchases",
                        modifier = Modifier
                            .clickable(
                                enabled = !billingBusy,
                                role = Role.Button,
                                onClick = onRestorePurchases
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showInfo) {
        UpgradeInfoDialog(
            accessState = accessState,
            onDismiss = { showInfo = false }
        )
    }
}

@Composable
private fun UpgradeInfoDialog(
    accessState: AccessState,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 14.dp, top = 14.dp, bottom = 28.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pro access",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Black,
                            fontSize = 25.sp,
                            lineHeight = 30.sp
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Close information",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = when (accessState) {
                            AccessState.TRIAL_ACTIVE -> "Upgrade now to unlock all 100 themes, engine and voice selection, additional voice downloads and speed control immediately, with no launch upgrade interruption."
                            AccessState.GRACE_TEXT_ONLY -> "Upgrade now to restore speech immediately and unlock all 100 themes, engine and voice selection, additional voice downloads and speed control."
                            AccessState.LOCKED -> "Upgrade now to restore app access immediately, including speech, all 100 themes, additional voice downloads and full voice controls."
                            AccessState.PRO -> "Pro already unlocks all 100 themes, engine and voice selection, additional voice downloads, speed control and uninterrupted access."
                        },
                        modifier = Modifier.padding(end = 10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    emphasized: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(
            width = if (emphasized) 3.dp else 1.dp,
            color = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = price,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun planPriceLabel(
    price: String?,
    cadence: String,
    loading: Boolean,
    available: Boolean
): String = when {
    price != null -> "$price / $cadence"
    loading -> "Loading price…"
    available -> "Price unavailable"
    else -> "Unavailable"
}
