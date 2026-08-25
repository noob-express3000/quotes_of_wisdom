package com.shipaton.quotesofwisdom

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.shipaton.quotesofwisdom.data.AppPreferencesRepository
import com.shipaton.quotesofwisdom.data.AssetQuoteRepository
import com.shipaton.quotesofwisdom.model.AccessState
import com.shipaton.quotesofwisdom.model.LocalAccessPolicy
import com.shipaton.quotesofwisdom.speech.TtsController
import com.shipaton.quotesofwisdom.speech.TtsState
import com.shipaton.quotesofwisdom.ui.home.HomeScreen
import com.shipaton.quotesofwisdom.ui.home.HomeViewModel
import com.shipaton.quotesofwisdom.ui.paywall.PaywallScreen
import com.shipaton.quotesofwisdom.ui.settings.SettingsScreen
import com.shipaton.quotesofwisdom.ui.theme.DefaultTheme
import com.shipaton.quotesofwisdom.ui.theme.QuotesOfWisdomTheme
import com.shipaton.quotesofwisdom.ui.theme.themeById

private enum class AppScreen { HOME, SETTINGS }

class MainActivity : ComponentActivity() {

    private lateinit var ttsController: TtsController

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory(
            repository = AssetQuoteRepository(applicationContext),
            preferencesRepository = AppPreferencesRepository(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsController = TtsController(applicationContext)

        setContent {
            val uiState by homeViewModel.uiState.collectAsState()
            val ttsState by ttsController.state.collectAsState()
            var screenName by rememberSaveable { mutableStateOf(AppScreen.HOME.name) }
            var showPaywall by rememberSaveable { mutableStateOf(true) }

            val access = uiState.effectiveAccessState
            val requestedPalette = themeById(uiState.themeId)
            val palette = if (requestedPalette.isFree || access == AccessState.PRO) {
                requestedPalette
            } else {
                DefaultTheme
            }

            QuotesOfWisdomTheme(palette = palette) {
                when {
                    showPaywall && access != AccessState.PRO -> {
                        PaywallScreen(
                            accessState = access,
                            canDismiss = LocalAccessPolicy.canDismissLaunchPaywall(access),
                            onDismiss = { showPaywall = false },
                            onChoosePlan = { plan ->
                                Toast.makeText(
                                    this,
                                    "${plan.replaceFirstChar { it.uppercase() }} purchase will connect to RevenueCat Test Store in M5.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    screenName == AppScreen.SETTINGS.name -> {
                        SettingsScreen(
                            selectedThemeId = uiState.themeId,
                            accessState = access,
                            streak = uiState.streak,
                            bestStreak = uiState.bestStreak,
                            favoriteQuotes = uiState.favoriteQuotes,
                            onBack = {
                                screenName = AppScreen.HOME.name
                                if (uiState.effectiveAccessState == AccessState.LOCKED) {
                                    showPaywall = true
                                }
                            },
                            onSelectTheme = homeViewModel::selectTheme,
                            onOpenPaywall = { showPaywall = true },
                            onDebugAccess = { state ->
                                homeViewModel.setDebugAccessOverride(state)
                                if (state == AccessState.PRO) showPaywall = false
                                if (state == AccessState.LOCKED) showPaywall = true
                            }
                        )
                    }

                    else -> {
                        HomeScreen(
                            uiState = uiState,
                            ttsReady = ttsState == TtsState.Ready || ttsState == TtsState.Speaking,
                            ttsSpeaking = ttsState == TtsState.Speaking,
                            onNextQuote = {
                                ttsController.stop()
                                homeViewModel.nextQuote()
                            },
                            onReplay = {
                                if (LocalAccessPolicy.canUseTts(access)) {
                                    uiState.quote?.let { ttsController.speak(it.text) }
                                }
                            },
                            onAutoSpeak = {
                                if (LocalAccessPolicy.canUseTts(access)) {
                                    uiState.quote?.let { ttsController.speak(it.text) }
                                }
                            },
                            onSettings = {
                                ttsController.stop()
                                screenName = AppScreen.SETTINGS.name
                            },
                            onToggleFavorite = homeViewModel::toggleFavorite,
                            onShare = { shareCurrentQuote(uiState.quote?.text, uiState.quote?.author) }
                        )
                    }
                }
            }
        }
    }

    private fun shareCurrentQuote(text: String?, author: String?) {
        if (text.isNullOrBlank() || author.isNullOrBlank()) return
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$text\n\n— $author")
        }
        startActivity(Intent.createChooser(shareIntent, "Share quote"))
    }

    override fun onDestroy() {
        if (::ttsController.isInitialized) ttsController.shutdown()
        super.onDestroy()
    }
}
