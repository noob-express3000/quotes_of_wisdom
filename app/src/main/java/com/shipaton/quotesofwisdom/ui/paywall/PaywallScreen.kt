package com.shipaton.quotesofwisdom.ui.paywall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shipaton.quotesofwisdom.model.AccessState

@Composable
fun PaywallScreen(
    accessState: AccessState,
    canDismiss: Boolean,
    onDismiss: () -> Unit,
    onChoosePlan: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (canDismiss) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Close subscription page",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Choose your plan",
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(32.dp))

            PlanCard(
                eyebrow = "Try It!",
                title = "Weekly",
                price = "$0.50 / week",
                onClick = { onChoosePlan("weekly") }
            )

            Spacer(Modifier.height(18.dp))

            PlanCard(
                eyebrow = "Best Value!",
                title = "Monthly",
                price = "$1 / month",
                emphasized = true,
                onClick = { onChoosePlan("monthly") }
            )

            Spacer(Modifier.height(18.dp))

            PlanCard(
                eyebrow = "Own It!",
                title = "Lifetime",
                price = "$29 once",
                onClick = { onChoosePlan("lifetime") }
            )
        }
    }
}

@Composable
private fun PlanCard(
    eyebrow: String,
    title: String,
    price: String,
    emphasized: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(
            width = if (emphasized) 3.dp else 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = eyebrow,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )

            Text(
                text = title,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp,
                lineHeight = 28.sp
            )

            Text(
                text = price,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
