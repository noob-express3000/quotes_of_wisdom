package com.shipaton.quotesofwisdom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.shipaton.quotesofwisdom.billing.BillingResult
import com.shipaton.quotesofwisdom.billing.RevenueCatController
import com.shipaton.quotesofwisdom.billing.shouldShowPaywallAfterConfirmedProLoss
import com.shipaton.quotesofwisdom.data.AppPreferencesRepository
import com.shipaton.quotesofwisdom.data.AssetQuoteRepository
import com.shipaton.quotesofwisdom.model.AccessState
import com.shipaton.quotesofwisdom.model.LocalAccessPolicy
import com.shipaton.quotesofwisdom.notifications.DailyWisdomNotifications
import com.shipaton.quotesofwisdom.speech.TtsController
import com.shipaton.quotesofwisdom.speech.TtsState
import com.shipaton.quotesofwisdom.ui.favorites.FavoritesScreen
import com.shipaton.quotesofwisdom.ui.home.HomeScreen
import com.shipaton.quotesofwisdom.ui.home.HomeViewModel
import com.shipaton.quotesofwisdom.ui.paywall.PaywallScreen
import com.shipaton.quotesofwisdom.ui.settings.SettingsScreen
import com.shipaton.quotesofwisdom.ui.theme.DefaultTheme
import com.shipaton.quotesofwisdom.ui.theme.QuotesOfWisdomTheme
import com.shipaton.quotesofwisdom.ui.theme.themeById

private enum class AppScreen { HOME, SETTINGS, FAVORITES }

class MainActivity : ComponentActivity() {

    private lateinit var ttsController: TtsController
    private var refreshTtsAfterExternalVoiceUi = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            DailyWisdomNotifications.setEnabled(this, granted)
        }

    private val revenueCatController: RevenueCatController by lazy {
        (application as QuotesApplication).revenueCatController
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory(
            repository = AssetQuoteRepository(applicationContext),
            preferencesRepository = AppPreferencesRepository(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterImmersiveMode()
        configureDailyNotifications()
        ttsController = TtsController(applicationContext)
        val initialReminderHour = DailyWisdomNotifications.reminderHour(this)
        val initialReminderMinute = DailyWisdomNotifications.reminderMinute(this)

        setContent {
            val uiState by homeViewModel.uiState.collectAsState()
            val revenueCatState by revenueCatController.state.collectAsState()
            val ttsState by ttsController.state.collectAsState()
            val ttsEngines by ttsController.engines.collectAsState()
            val selectedEnginePackage by ttsController.selectedEnginePackage.collectAsState()
            val ttsVoices by ttsController.voices.collectAsState()
            val selectedVoiceName by ttsController.selectedVoiceName.collectAsState()
            val speechRate by ttsController.speechRate.collectAsState()
            var screenName by rememberSaveable { mutableStateOf(AppScreen.HOME.name) }
            var showPaywall by rememberSaveable { mutableStateOf(false) }
            var initialEntitlementHandled by rememberSaveable { mutableStateOf(false) }
            var lastResolvedRevenueCatPro by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            var reminderHour by rememberSaveable { mutableStateOf(initialReminderHour) }
            var reminderMinute by rememberSaveable { mutableStateOf(initialReminderMinute) }

            val access = uiState.effectiveAccessState
            val paywallRequired = access == AccessState.LOCKED &&
                uiState.debugAccessOverride == null
            val canDismissPaywall = uiState.debugAccessOverride != null ||
                LocalAccessPolicy.canDismissLaunchPaywall(access)
            val requestedPalette = themeById(uiState.themeId)
            val palette = if (requestedPalette.isFree || access == AccessState.PRO) {
                requestedPalette
            } else {
                DefaultTheme
            }
            val ttsReady = ttsState == TtsState.Ready || ttsState == TtsState.Speaking

            LaunchedEffect(
                revenueCatState.entitlementResolved,
                revenueCatState.hasPro,
                uiState.debugAccessOverride
            ) {
                homeViewModel.setRevenueCatPro(revenueCatState.hasPro)

                if (
                    uiState.debugAccessOverride == null &&
                    revenueCatState.entitlementResolved &&
                    !initialEntitlementHandled
                ) {
                    initialEntitlementHandled = true
                    showPaywall = !revenueCatState.hasPro
                }

                if (revenueCatState.hasPro && uiState.debugAccessOverride == null) {
                    showPaywall = false
                }

                if (
                    uiState.debugAccessOverride == null &&
                    initialEntitlementHandled &&
                    shouldShowPaywallAfterConfirmedProLoss(
                        lastResolvedHasPro = lastResolvedRevenueCatPro,
                        currentState = revenueCatState
                    )
                ) {
                    showPaywall = true
                }

                if (
                    uiState.debugAccessOverride == null &&
                    revenueCatState.entitlementResolved
                ) {
                    lastResolvedRevenueCatPro = revenueCatState.hasPro
                }
            }

            SideEffect {
                val useDarkSystemIcons = palette.dominant.luminance() > 0.5f
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = useDarkSystemIcons
                    isAppearanceLightNavigationBars = useDarkSystemIcons
                }
            }

            LaunchedEffect(
                access,
                ttsState,
                uiState.proEnginePackage,
                uiState.proVoiceName,
                uiState.proSpeechRate
            ) {
                if (ttsReady) {
                    if (access == AccessState.PRO) {
                        ttsController.applyProSettings(
                            enginePackage = uiState.proEnginePackage,
                            voiceName = uiState.proVoiceName,
                            rate = uiState.proSpeechRate
                        )
                    } else {
                        ttsController.applyTrialDefaults()
                    }
                }
            }

            QuotesOfWisdomTheme(palette = palette) {
                BackHandler(
                    enabled = showPaywall || paywallRequired ||
                        screenName != AppScreen.HOME.name
                ) {
                    when {
                        showPaywall || paywallRequired -> {
                            if (canDismissPaywall) showPaywall = false
                        }

                        screenName == AppScreen.FAVORITES.name -> {
                            screenName = AppScreen.SETTINGS.name
                        }

                        screenName == AppScreen.SETTINGS.name -> {
                            screenName = AppScreen.HOME.name
                            if (access == AccessState.LOCKED) showPaywall = true
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when {
                        !initialEntitlementHandled && uiState.debugAccessOverride == null -> Unit

                        (showPaywall || paywallRequired) && access != AccessState.PRO -> {
                            PaywallScreen(
                                accessState = access,
                                canDismiss = canDismissPaywall,
                                weeklyPrice = revenueCatState.weeklyPrice,
                                monthlyPrice = revenueCatState.monthlyPrice,
                                lifetimePrice = revenueCatState.lifetimePrice,
                                availablePlans = revenueCatState.availablePlans,
                                billingLoading = revenueCatState.offeringsLoading,
                                billingBusy = revenueCatState.busy,
                                billingMessage = revenueCatState.billingMessage,
                                onDismiss = { showPaywall = false },
                                onChoosePlan = { plan ->
                                    revenueCatController.purchase(
                                        activity = this@MainActivity,
                                        plan = plan
                                    ) { result ->
                                        runOnUiThread {
                                            when (result) {
                                                BillingResult.Success -> {
                                                    if (revenueCatController.state.value.hasPro) {
                                                        homeViewModel.setDebugAccessOverride(null)
                                                        homeViewModel.setRevenueCatPro(true)
                                                        showPaywall = false
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Pro access active.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                                BillingResult.Cancelled -> Unit
                                                is BillingResult.Error -> Toast.makeText(
                                                    this@MainActivity,
                                                    result.message,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                },
                                onRestorePurchases = {
                                    revenueCatController.restore { result ->
                                        runOnUiThread {
                                            when (result) {
                                                BillingResult.Success -> {
                                                    if (revenueCatController.state.value.hasPro) {
                                                        homeViewModel.setDebugAccessOverride(null)
                                                        homeViewModel.setRevenueCatPro(true)
                                                        showPaywall = false
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Pro access restored.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "No active Pro purchase found.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                                BillingResult.Cancelled -> Unit
                                                is BillingResult.Error -> Toast.makeText(
                                                    this@MainActivity,
                                                    result.message,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                },
                                onRetryBilling = revenueCatController::refresh
                            )
                        }

                        screenName == AppScreen.FAVORITES.name -> {
                            FavoritesScreen(
                                favoriteQuotes = uiState.favoriteQuotes,
                                playbackEnabled = LocalAccessPolicy.canUseTts(access) && ttsReady,
                                onClose = { screenName = AppScreen.SETTINGS.name },
                                onPlayFavorite = { quote ->
                                    if (LocalAccessPolicy.canUseTts(access)) {
                                        ttsController.speak(quote.text)
                                    }
                                },
                                onRemoveFavorite = homeViewModel::toggleFavorite
                            )
                        }

                        screenName == AppScreen.SETTINGS.name -> {
                            SettingsScreen(
                                selectedThemeId = uiState.themeId,
                                accessState = access,
                                streak = uiState.streak,
                                bestStreak = uiState.bestStreak,
                                favoriteCount = uiState.favoriteQuotes.size,
                                ttsEngines = ttsEngines,
                                selectedEnginePackage = selectedEnginePackage,
                                ttsVoices = ttsVoices,
                                selectedVoiceName = selectedVoiceName,
                                speechRate = speechRate,
                                reminderHour = reminderHour,
                                reminderMinute = reminderMinute,
                                onBack = {
                                    screenName = AppScreen.HOME.name
                                    if (uiState.effectiveAccessState == AccessState.LOCKED) {
                                        showPaywall = true
                                    }
                                },
                                onOpenFavorites = { screenName = AppScreen.FAVORITES.name },
                                onSelectTheme = homeViewModel::selectTheme,
                                onSelectEngine = { enginePackage ->
                                    ttsController.selectEngine(enginePackage)
                                    homeViewModel.selectProEngine(enginePackage)
                                },
                                onSelectVoice = { voiceName ->
                                    ttsController.setProVoice(voiceName)
                                    homeViewModel.selectProVoice(voiceName)
                                },
                                onSpeechRateChange = { rate ->
                                    ttsController.setProSpeechRate(rate)
                                    homeViewModel.setProSpeechRate(rate)
                                },
                                onReminderTimeChange = { hour, minute ->
                                    if (access == AccessState.PRO) {
                                        reminderHour = hour
                                        reminderMinute = minute
                                        DailyWisdomNotifications.setReminderTime(
                                            this@MainActivity,
                                            hour,
                                            minute
                                        )
                                    }
                                },
                                onPreviewSpeech = {
                                    if (access == AccessState.PRO) {
                                        uiState.quote?.let { ttsController.speak(it.text) }
                                    }
                                },
                                onGetMoreVoices = ::openMoreVoices,
                                onOpenPaywall = { showPaywall = true },
                                onDebugAccess = { state ->
                                    homeViewModel.setDebugAccessOverride(state)
                                    showPaywall = when (state) {
                                        AccessState.PRO -> false
                                        AccessState.TRIAL_ACTIVE,
                                        AccessState.GRACE_TEXT_ONLY,
                                        AccessState.LOCKED -> true
                                        null -> uiState.accessState != AccessState.PRO
                                    }
                                }
                            )
                        }

                        else -> {
                            HomeScreen(
                                uiState = uiState,
                                ttsReady = ttsReady,
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
    }

    private fun configureDailyNotifications() {
        DailyWisdomNotifications.ensureChannels(this)
        val prefs = getSharedPreferences(NOTIFICATION_GATE_PREFS, MODE_PRIVATE)
        val hasLaunchedBefore = prefs.getBoolean(KEY_HAS_LAUNCHED, false)

        if (!hasLaunchedBefore) {
            prefs.edit().putBoolean(KEY_HAS_LAUNCHED, true).apply()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                DailyWisdomNotifications.setEnabled(this, true)
            }
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            DailyWisdomNotifications.setEnabled(this, true)
            return
        }

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            DailyWisdomNotifications.setEnabled(this, true)
            return
        }

        if (prefs.getBoolean(KEY_PERMISSION_PROMPTED, false)) return

        prefs.edit().putBoolean(KEY_PERMISSION_PROMPTED, true).apply()
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun enterImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
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

    private fun openMoreVoices() {
        val enginePackage = ttsController.selectedEnginePackage.value

        if (enginePackage.isNotBlank()) {
            val engineInstaller = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
                setPackage(enginePackage)
            }
            if (tryStartVoiceUi(engineInstaller)) return
        }

        if (tryStartVoiceUi(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))) return

        if (!tryStartVoiceUi(Intent(Settings.ACTION_SETTINGS))) {
            Toast.makeText(
                this,
                "Voice download settings are not available on this device.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun tryStartVoiceUi(intent: Intent): Boolean = try {
        refreshTtsAfterExternalVoiceUi = true
        startActivity(intent)
        true
    } catch (_: Throwable) {
        refreshTtsAfterExternalVoiceUi = false
        false
    }

    override fun onResume() {
        super.onResume()
        enterImmersiveMode()
        if (refreshTtsAfterExternalVoiceUi && ::ttsController.isInitialized) {
            refreshTtsAfterExternalVoiceUi = false
            ttsController.refreshCurrentEngine()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersiveMode()
    }

    override fun onDestroy() {
        if (::ttsController.isInitialized) ttsController.shutdown()
        super.onDestroy()
    }

    private companion object {
        const val NOTIFICATION_GATE_PREFS = "notification_gate"
        const val KEY_HAS_LAUNCHED = "has_launched"
        const val KEY_PERMISSION_PROMPTED = "permission_prompted"
    }
}
