package com.shipaton.quotesofwisdom.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shipaton.quotesofwisdom.model.AccessState
import com.shipaton.quotesofwisdom.model.Quote
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    ttsReady: Boolean,
    onNextQuote: () -> Unit,
    onReplay: () -> Unit,
    onAutoSpeak: () -> Unit,
    onSettings: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    val access = uiState.effectiveAccessState
    val ttsAllowed = access == AccessState.TRIAL_ACTIVE || access == AccessState.PRO

    LaunchedEffect(uiState.quote?.id, ttsReady, ttsAllowed) {
        if (uiState.quote != null && ttsReady && ttsAllowed) {
            delay(2_000)
            onAutoSpeak()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Header(
                accessState = access,
                streak = uiState.streak,
                onSettings = onSettings
            )

            Spacer(Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> LoadingCard()
                    uiState.errorMessage != null -> MessageCard(uiState.errorMessage)
                    uiState.quote != null -> QuoteCard(
                        quote = uiState.quote,
                        isFavorite = uiState.isCurrentFavorite,
                        onToggleFavorite = onToggleFavorite,
                        onShare = onShare
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Controls(
                replayEnabled = ttsAllowed && ttsReady,
                onReplay = onReplay,
                onNextQuote = onNextQuote
            )
        }
    }
}

@Composable
private fun Header(
    accessState: AccessState,
    streak: Int,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSettings) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

        Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.secondary
        ) {
            Text(
                text = "🔥 $streak",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )
        }

        Text(
            text = when (accessState) {
                AccessState.PRO -> "PRO"
                AccessState.GRACE_TEXT_ONLY -> "GRACE"
                AccessState.LOCKED -> "LOCKED"
                AccessState.TRIAL_ACTIVE -> "FREE"
            },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(quote.id) { scrollState.scrollTo(0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 330.dp)
                    .verticalScroll(scrollState),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quote.text,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "— ${quote.author}",
                color = MaterialTheme.colorScheme.secondary,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        if (isFavorite) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = if (isFavorite) "Remove favorite" else "Save favorite",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }

                IconButton(onClick = onShare) {
                    Icon(
                        Icons.Rounded.Share,
                        contentDescription = "Share quote",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    MessageCard("Loading your quote library…")
}

@Composable
private fun MessageCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 38.dp),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun Controls(
    replayEnabled: Boolean,
    onReplay: () -> Unit,
    onNextQuote: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onReplay,
            enabled = replayEnabled,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.tertiary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary,
                disabledContentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("↻  Replay")
        }

        Button(
            onClick = onNextQuote,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Next  →")
        }
    }
}
