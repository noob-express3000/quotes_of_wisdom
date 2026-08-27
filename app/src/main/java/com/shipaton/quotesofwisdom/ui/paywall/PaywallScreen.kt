package com.shipaton.quotesofwisdom.ui.paywall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shipaton.quotesofwisdom.model.AccessState

@Composable
fun PaywallScreen(
    accessState: AccessState,
    canDismiss: Boolean,
    onDismiss: () -> Unit,
    onChoosePlan: (String) -> Unit
) {
    var showInfo by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose your plan",
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(Modifier.height(30.dp))

                PlanCard(
                    title = "Weekly",
                    price = "$0.50 / week",
                    onClick = { onChoosePlan("weekly") }
                )

                Spacer(Modifier.height(18.dp))

                PlanCard(
                    title = "Monthly",
                    price = "$1 / month",
                    emphasized = true,
                    onClick = { onChoosePlan("monthly") }
                )

                Spacer(Modifier.height(18.dp))

                PlanCard(
                    title = "Lifetime",
                    price = "$29 once",
                    onClick = { onChoosePlan("lifetime") }
                )
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
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 12.dp, top = 10.dp, bottom = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pro access",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Close information",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Text(
                    text = when (accessState) {
                        AccessState.TRIAL_ACTIVE -> "Upgrade now to unlock all 100 themes, voice selection and speed control immediately, with no launch upgrade interruption."
                        AccessState.GRACE_TEXT_ONLY -> "Upgrade now to restore speech immediately and unlock all 100 themes, selectable voices and speed control."
                        AccessState.LOCKED -> "Upgrade now to restore app access immediately, including speech, all 100 themes and full voice controls."
                        AccessState.PRO -> "Pro already unlocks all 100 themes, selectable voices, speed control and uninterrupted access."
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    emphasized: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .clickable(onClick = onClick),
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
