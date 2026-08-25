package com.shipaton.quotesofwisdom.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shipaton.quotesofwisdom.BuildConfig
import com.shipaton.quotesofwisdom.model.AccessState
import com.shipaton.quotesofwisdom.model.Quote
import com.shipaton.quotesofwisdom.speech.VoiceOption
import com.shipaton.quotesofwisdom.ui.theme.AppThemePalette
import com.shipaton.quotesofwisdom.ui.theme.AppThemes

@Composable
fun SettingsScreen(
    selectedThemeId: String,
    accessState: AccessState,
    streak: Int,
    bestStreak: Int,
    favoriteQuotes: List<Quote>,
    ttsVoices: List<VoiceOption>,
    selectedVoiceName: String,
    speechRate: Float,
    onBack: () -> Unit,
    onSelectTheme: (String) -> Unit,
    onSelectVoice: (String) -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onPreviewSpeech: () -> Unit,
    onOpenPaywall: () -> Unit,
    onDebugAccess: (AccessState?) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Settings",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                InfoCard {
                    Text("Daily streak", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (streak == 1) "1 day" else "$streak days",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Best: $bestStreak · Opening the app once today keeps it alive.",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Themes",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    AppThemes.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { palette ->
                                ThemeTile(
                                    palette = palette,
                                    selected = palette.id == selectedThemeId,
                                    locked = !palette.isFree && accessState != AccessState.PRO,
                                    onClick = {
                                        if (palette.isFree || accessState == AccessState.PRO) {
                                            onSelectTheme(palette.id)
                                        } else {
                                            onOpenPaywall()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                InfoCard {
                    Text("Speech", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))

                    if (accessState == AccessState.PRO) {
                        Text(
                            "Choose any English voice installed on this device and tune the narration speed.",
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(14.dp))

                        VoicePicker(
                            voices = ttsVoices,
                            selectedVoiceName = selectedVoiceName,
                            onSelectVoice = onSelectVoice
                        )

                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Speech speed", color = MaterialTheme.colorScheme.secondary)
                            Text(
                                "%.2fx".format(speechRate),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = speechRate.coerceIn(0.7f, 1.4f),
                            onValueChange = onSpeechRateChange,
                            valueRange = 0.7f..1.4f,
                            steps = 6
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Calm", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                            Text("Fast", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onPreviewSpeech,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Preview current quote")
                        }
                    } else {
                        Text(
                            "Trial speech uses one fixed voice and speed. Pro unlocks voice and speed controls.",
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onOpenPaywall,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) { Text("See Pro") }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Favorites (${favoriteQuotes.size})",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (favoriteQuotes.isEmpty()) {
                        Text(
                            "Tap the bookmark under a quote and it will appear here.",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        favoriteQuotes.take(20).forEach { quote ->
                            InfoCard {
                                Text(
                                    quote.text,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "— ${quote.author}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        if (favoriteQuotes.size > 20) {
                            Text(
                                "+${favoriteQuotes.size - 20} more saved locally",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Developer access preview",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Debug build only. Inspect trial, grace, locked and Pro UI without waiting 34 days.",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 13.sp
                        )
                        listOf(
                            "Real state" to null,
                            "Trial" to AccessState.TRIAL_ACTIVE,
                            "Grace" to AccessState.GRACE_TEXT_ONLY,
                            "Locked" to AccessState.LOCKED,
                            "Pro" to AccessState.PRO
                        ).forEach { (label, state) ->
                            Button(
                                onClick = { onDebugAccess(state) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) { Text(label) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoicePicker(
    voices: List<VoiceOption>,
    selectedVoiceName: String,
    onSelectVoice: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = voices.firstOrNull { it.name == selectedVoiceName }?.label
        ?: voices.firstOrNull()?.label
        ?: "No English voices found"

    Text("Voice", color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
    Spacer(Modifier.height(6.dp))
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            enabled = voices.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(selectedLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            voices.forEach { voice ->
                DropdownMenuItem(
                    text = {
                        Text(
                            voice.label,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelectVoice(voice.name)
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeTile(
    palette: AppThemePalette,
    selected: Boolean,
    locked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.dominant),
        border = BorderStroke(if (selected) 3.dp else 1.dp, palette.secondary)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ColorDot(palette.dominant, palette.secondary)
                ColorDot(palette.secondary, palette.secondary)
                ColorDot(palette.accent, palette.secondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    palette.name,
                    modifier = Modifier.weight(1f),
                    color = palette.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (locked) {
                    Icon(
                        Icons.Rounded.Lock,
                        contentDescription = "Pro theme",
                        tint = palette.accent
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color, border: Color) {
    Surface(
        modifier = Modifier.size(20.dp),
        color = color,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, border)
    ) {}
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}
