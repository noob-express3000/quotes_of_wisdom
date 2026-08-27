package com.shipaton.quotesofwisdom.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shipaton.quotesofwisdom.model.AccessState
import com.shipaton.quotesofwisdom.model.Quote
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

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
    val flameProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.quote?.id, ttsReady, ttsAllowed) {
        if (uiState.quote != null && ttsReady && ttsAllowed) {
            onAutoSpeak()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 22.dp, top = 6.dp, end = 22.dp, bottom = 20.dp)
            ) {
                Header(
                    accessState = access,
                    streak = uiState.streak,
                    onSettings = onSettings,
                    onStreakTap = {
                        scope.launch {
                            flameProgress.snapTo(0f)
                            flameProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = 820,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                            flameProgress.snapTo(0f)
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))

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

            FlameSurgeOverlay(
                progress = flameProgress.value,
                accent = MaterialTheme.colorScheme.tertiary,
                inner = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun Header(
    accessState: AccessState,
    streak: Int,
    onSettings: () -> Unit,
    onStreakTap: () -> Unit
) {
    val proRotation = remember { Animatable(0f) }
    val proScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
    ) {
        IconButton(
            onClick = onSettings,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .clickable(enabled = accessState == AccessState.PRO) {
                    scope.launch {
                        proRotation.snapTo(0f)
                        proScale.snapTo(1f)
                        launch {
                            proRotation.animateTo(
                                targetValue = 360f,
                                animationSpec = tween(
                                    durationMillis = 460,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            proRotation.snapTo(0f)
                        }
                        launch {
                            proScale.animateTo(
                                targetValue = 1.14f,
                                animationSpec = tween(durationMillis = 220)
                            )
                            proScale.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 220)
                            )
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (accessState) {
                    AccessState.PRO -> "PRO"
                    AccessState.GRACE_TEXT_ONLY -> "GRACE"
                    AccessState.LOCKED -> "LOCKED"
                    AccessState.TRIAL_ACTIVE -> "FREE"
                },
                modifier = Modifier.graphicsLayer {
                    rotationZ = proRotation.value
                    scaleX = proScale.value
                    scaleY = proScale.value
                },
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable(onClick = onStreakTap),
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
    }
}

@Composable
private fun FlameSurgeOverlay(
    progress: Float,
    accent: Color,
    inner: Color
) {
    if (progress <= 0f) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val envelope = sin(PI * progress).toFloat().coerceIn(0f, 1f)
        drawRect(accent.copy(alpha = 0.09f * envelope))

        repeat(32) { index ->
            val phase = ((index * 37) % 100) / 250f
            val local = (progress * 1.38f - phase).coerceIn(0f, 1f)
            if (local <= 0f || local >= 1f) return@repeat

            val flameHeight = 120f + (index % 7) * 18f
            val flameWidth = 44f + (index % 5) * 9f
            val lane = (index + 0.5f) / 32f
            val sway = sin((local * 3.2f + index) * PI).toFloat() * 18f
            val x = size.width * lane + sway
            val y = size.height + flameHeight - local * (size.height + flameHeight * 2.1f)
            val flameAlpha = (envelope * (0.72f + (index % 4) * 0.06f)).coerceIn(0f, 0.95f)

            drawFlame(
                centerX = x,
                baseY = y,
                width = flameWidth,
                height = flameHeight,
                outer = accent.copy(alpha = flameAlpha),
                inner = inner.copy(alpha = flameAlpha * 0.86f)
            )
        }
    }
}

private fun DrawScope.drawFlame(
    centerX: Float,
    baseY: Float,
    width: Float,
    height: Float,
    outer: Color,
    inner: Color
) {
    val outerPath = Path().apply {
        moveTo(centerX, baseY - height)
        cubicTo(
            centerX + width * 0.58f,
            baseY - height * 0.62f,
            centerX + width * 0.72f,
            baseY - height * 0.18f,
            centerX,
            baseY
        )
        cubicTo(
            centerX - width * 0.72f,
            baseY - height * 0.18f,
            centerX - width * 0.58f,
            baseY - height * 0.62f,
            centerX,
            baseY - height
        )
        close()
    }
    drawPath(outerPath, outer)

    val innerPath = Path().apply {
        moveTo(centerX, baseY - height * 0.68f)
        cubicTo(
            centerX + width * 0.34f,
            baseY - height * 0.42f,
            centerX + width * 0.38f,
            baseY - height * 0.14f,
            centerX,
            baseY - height * 0.05f
        )
        cubicTo(
            centerX - width * 0.38f,
            baseY - height * 0.14f,
            centerX - width * 0.34f,
            baseY - height * 0.42f,
            centerX,
            baseY - height * 0.68f
        )
        close()
    }
    drawPath(innerPath, inner)
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
