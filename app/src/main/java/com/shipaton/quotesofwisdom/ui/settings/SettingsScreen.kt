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
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Favorite
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
import com.shipaton.quotesofwisdom.speech.TtsEngineOption
import com.shipaton.quotesofwisdom.speech.VoiceOption
import com.shipaton.quotesofwisdom.ui.theme.AppThemePalette
import com.shipaton.quotesofwisdom.ui.theme.AppThemes

@Composable
fun SettingsScreen(
    selectedThemeId: String,
    accessState: AccessState,
    streak: Int,
    bestStreak: Int,
    favoriteCount: Int,
    ttsEngines: List<TtsEngineOption>,
    selectedEnginePackage: String,
    ttsVoices: List<VoiceOption>,
    selectedVoiceName: String,
    speechRate: Float,
    onBack: () -> Unit,
    onOpenFavorites: () -> Unit,
    onSelectTheme: (String) -> Unit,
    onSelectEngine: (String) -> Unit,
    onSelectVoice: (String) -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onPreviewSpeech: () -> Unit,
    onGetMoreVoices: () -> Unit,
    onOpenPaywall: () -> Unit,
    onDebugAccess: (AccessState?) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                            tint = MaterialTheme.colorScheme.tertiary
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenFavorites),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.size(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Favorites",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                if (favoriteCount == 1) "1 saved quote" else "$favoriteCount saved quotes",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 13.sp
                            )
                        }
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = "Open favorites",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            item {
                InfoCard {
                    Text("Speech", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    if (accessState == AccessState.PRO) {
                        EnginePicker(
                            engines = ttsEngines,
                            selectedEnginePackage = selectedEnginePackage,
                            onSelectEngine = onSelectEngine
                        )

                        Spacer(Modifier.height(16.dp))

                        VoicePicker(
                            voices = ttsVoices,
                            selectedVoiceName = selectedVoiceName,
                            onSelectVoice = onSelectVoice
                        )

                        Spacer(Modifier.height(12.dp))
                        VoiceDataButton(onClick = onGetMoreVoices)

                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Speech speed", color = MaterialTheme.colorScheme.secondary)
                            Text(
                                "%.2fx".format(speechRate),
                                color = MaterialTheme.colorScheme.tertiary,
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
                                contentColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("Preview current quote")
                        }
                    } else {
                        Button(
                            onClick = onOpenPaywall,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) { Text("See Pro") }

                        Spacer(Modifier.height(12.dp))
                        VoiceDataButton(onClick = onGetMoreVoices)
                    }
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

            if (BuildConfig.DEBUG) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Developer access preview",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
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
                                    contentColor = MaterialTheme.colorScheme.tertiary
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
private fun EnginePicker(
    engines: List<TtsEngineOption>,
    selectedEnginePackage: String,
    onSelectEngine: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = engines.firstOrNull { it.packageName == selectedEnginePackage }
        ?: engines.firstOrNull { it.isDefault }
        ?: engines.firstOrNull()

    Text("Engine", color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
    Spacer(Modifier.height(6.dp))
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            enabled = engines.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                selected?.let { if (it.isDefault) "${it.label} · default" else it.label }
                    ?: "No TTS engine found",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            engines.forEach { engine ->
                DropdownMenuItem(
                    text = {
                        Text(
                            if (engine.isDefault) "${engine.label} · default" else engine.label,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelectEngine(engine.packageName)
                    }
                )
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
                contentColor = MaterialTheme.colorScheme.tertiary
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
private fun VoiceDataButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text("Get more voices")
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
        border = BorderStroke(if (selected) 3.dp else 1.dp, palette.accent)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ColorDot(palette.dominant, palette.accent)
                ColorDot(palette.secondary, palette.accent)
                ColorDot(palette.accent, palette.accent)
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}
