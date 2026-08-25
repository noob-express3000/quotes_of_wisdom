package com.shipaton.quotesofwisdom.ui.paywall

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (canDismiss) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Close upgrade screen",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            ProHeroCard()
            Spacer(Modifier.height(24.dp))

            Text(
                text = when (accessState) {
                    AccessState.GRACE_TEXT_ONLY -> "Your trial ended. Quotes still work for 3 days — speech is waiting in Pro."
                    AccessState.LOCKED -> "Keep your quote ritual going with Pro."
                    else -> "You have full trial access. Upgrade anytime and keep it forever."
                },
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(22.dp))

            PlanCard(
                eyebrow = "Try It!",
                title = "Weekly",
                testPrice = "≈ $0.50 / week",
                onClick = { onChoosePlan("weekly") }
            )
            Spacer(Modifier.height(12.dp))
            PlanCard(
                eyebrow = "Best Value!",
                title = "Monthly",
                testPrice = "≈ $1.00 / month",
                emphasized = true,
                onClick = { onChoosePlan("monthly") }
            )
            Spacer(Modifier.height(12.dp))
            PlanCard(
                eyebrow = "Own It!",
                title = "Lifetime",
                testPrice = "≈ $29 once",
                onClick = { onChoosePlan("lifetime") }
            )

            Spacer(Modifier.height(22.dp))
            Text(
                "Pro includes all 50 themes, multiple local/device TTS voices, adjustable speech speed, and continued access.",
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Product-test build: purchase buttons connect to RevenueCat Test Store in M5; production prices will be localized from the store.",
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun ProHeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "PRO",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 5.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Quotes of Wisdom",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PlanCard(
    eyebrow: String,
    title: String,
    testPrice: String,
    emphasized: Boolean = false,
    onClick: () -> Unit
) {
    var spins by remember { mutableIntStateOf(0) }
    val rotation by animateFloatAsState(
        targetValue = spins * 360f,
        animationSpec = spring(dampingRatio = 0.68f, stiffness = 190f),
        label = "plan-card-spin"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = rotation }
            .clickable { spins += 1 },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(if (emphasized) 3.dp else 1.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    eyebrow,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
                Text(
                    title,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(testPrice, color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Choose")
            }
        }
    }
}
