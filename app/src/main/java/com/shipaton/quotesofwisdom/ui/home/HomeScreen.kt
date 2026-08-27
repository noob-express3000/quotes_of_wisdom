package com.shipaton.quotesofwisdom.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.geometry.Offset
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
                                    durationMillis = 1180,
                                    easing = FastOutSlowInEasing
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
    val proInteractionSource = remember { MutableInteractionSource() }
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
                .clickable(
                    interactionSource = proInteractionSource,
                    indication = null,
                    enabled = accessState == AccessState.PRO
                ) {
                    scope.launch {
                        proRotation.snapTo(0f)
                        proRotation.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(
                                durationMillis = 520,
                                easing = FastOutSlowInEasing
                            )
                        )
                        proRotation.snapTo(0f)
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
        val attack = (progress / 0.36f).coerceIn(0f, 1f)
        val release = ((1f - progress) / 0.24f).coerceIn(0f, 1f)
        val intensity = minOf(attack, release)
        val phase = progress * 8.5f
        val fullHeight = size.height * (0.30f + 1.02f * attack)

        drawRect(accent.copy(alpha = 0.18f * intensity))

        drawFlameWall(
            height = fullHeight,
            tongues = 11,
            valleyRatio = 0.34f,
            phase = phase,
            color = accent.copy(alpha = 0.88f * intensity)
        )
        drawFlameWall(
            height = fullHeight * 0.82f,
            tongues = 14,
            valleyRatio = 0.42f,
            phase = phase + 0.85f,
            color = inner.copy(alpha = 0.78f * intensity)
        )

        repeat(20) { index ->
            val laneWidth = size.width / 20f
            val pulse = 0.74f +
                0.26f * ((sin((index * 1.41f + phase) * PI) + 1.0) * 0.5).toFloat()
            val height = fullHeight * (0.46f + 0.48f * pulse)
            val width = laneWidth * (1.15f + (index % 4) * 0.16f)
            val lean = sin((index * 0.83f + phase * 0.72f) * PI).toFloat() * 0.68f
            val centerX = laneWidth * (index + 0.5f)

            drawFlameTongue(
                centerX = centerX,
                baseY = size.height + 6f,
                width = width,
                height = height,
                lean = lean,
                outer = accent.copy(alpha = 0.92f * intensity),
                inner = inner.copy(alpha = 0.84f * intensity)
            )
        }

        repeat(28) { index ->
            val delay = (index % 9) * 0.035f
            val local = ((progress - delay) * 1.34f).coerceIn(0f, 1f)
            if (local <= 0f || local >= 1f) return@repeat

            val xBase = size.width * ((index * 47 % 101) / 100f)
            val drift = sin((local * 2.8f + index) * PI).toFloat() * 22f
            val y = size.height * (1.04f - local * 1.14f)
            val emberAlpha = intensity * (1f - local) * 0.92f

            drawCircle(
                color = accent.copy(alpha = emberAlpha),
                radius = 2.5f + (index % 4) * 1.4f,
                center = Offset(xBase + drift, y)
            )
        }
    }
}

private fun DrawScope.drawFlameWall(
    height: Float,
    tongues: Int,
    valleyRatio: Float,
    phase: Float,
    color: Color
) {
    val bottom = size.height + 8f
    val laneWidth = size.width / tongues

    fun valleyY(index: Int): Float {
        val wobble = sin((index * 1.19f + phase * 0.54f) * PI).toFloat() * 0.055f
        return bottom - height * (valleyRatio + wobble)
    }

    val path = Path().apply {
        moveTo(0f, bottom)
        lineTo(0f, valleyY(0))

        repeat(tongues) { index ->
            val x0 = index * laneWidth
            val tipX = x0 + laneWidth * 0.5f
            val x1 = x0 + laneWidth
            val pulse =
                0.76f +
                    0.24f *
                    ((sin((index * 1.73f + phase) * PI) + 1.0) * 0.5).toFloat()
            val tipY = bottom - height * pulse
            val nextValleyY = valleyY(index + 1)

            cubicTo(
                x0 + laneWidth * 0.18f,
                valleyY(index) - height * 0.10f,
                tipX - laneWidth * 0.14f,
                tipY + height * 0.10f,
                tipX,
                tipY
            )
            cubicTo(
                tipX + laneWidth * 0.16f,
                tipY + height * 0.12f,
                x1 - laneWidth * 0.18f,
                nextValleyY - height * 0.08f,
                x1,
                nextValleyY
            )
        }

        lineTo(size.width, bottom)
        close()
    }

    drawPath(path, color)
}

private fun DrawScope.drawFlameTongue(
    centerX: Float,
    baseY: Float,
    width: Float,
    height: Float,
    lean: Float,
    outer: Color,
    inner: Color
) {
    val tipX = centerX + lean * width

    val outerPath = Path().apply {
        moveTo(centerX - width * 0.62f, baseY)
        cubicTo(
            centerX - width * 0.92f,
            baseY - height * 0.24f,
            tipX - width * 0.30f,
            baseY - height * 0.68f,
            tipX,
            baseY - height
        )
        cubicTo(
            tipX + width * 0.38f,
            baseY - height * 0.64f,
            centerX + width * 0.92f,
            baseY - height * 0.22f,
            centerX + width * 0.62f,
            baseY
        )
        close()
    }
    drawPath(outerPath, outer)

    val innerTipX = centerX + lean * width * 0.48f
    val innerPath = Path().apply {
        moveTo(centerX - width * 0.34f, baseY)
        cubicTo(
            centerX - width * 0.45f,
            baseY - height * 0.17f,
            innerTipX - width * 0.18f,
            baseY - height * 0.44f,
            innerTipX,
            baseY - height * 0.68f
        )
        cubicTo(
            innerTipX + width * 0.22f,
            baseY - height * 0.42f,
            centerX + width * 0.48f,
            baseY - height * 0.16f,
            centerX + width * 0.34f,
            baseY
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
