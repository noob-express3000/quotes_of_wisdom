package com.shipaton.quotesofwisdom.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shipaton.quotesofwisdom.BuildConfig
import com.shipaton.quotesofwisdom.data.AppPreferencesRepository
import com.shipaton.quotesofwisdom.data.QuoteDeck
import com.shipaton.quotesofwisdom.data.QuoteRepository
import com.shipaton.quotesofwisdom.model.AccessState
import com.shipaton.quotesofwisdom.model.LocalAccessPolicy
import com.shipaton.quotesofwisdom.model.Quote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val quote: Quote? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val favoriteIds: Set<Int> = emptySet(),
    val favoriteQuotes: List<Quote> = emptyList(),
    val themeId: String = "parchment",
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val streakBrokenOnLaunch: Boolean = false,
    val accessState: AccessState = AccessState.TRIAL_ACTIVE,
    val debugAccessOverride: AccessState? = null,
    val proEnginePackage: String = "",
    val proVoiceName: String = "",
    val proSpeechRate: Float = 1.0f
) {
    val effectiveAccessState: AccessState get() = debugAccessOverride ?: accessState
    val isCurrentFavorite: Boolean get() = quote?.id in favoriteIds
}

class HomeViewModel(
    private val repository: QuoteRepository,
    private val preferencesRepository: AppPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var deck: QuoteDeck? = null
    private var loadedQuotes: List<Quote> = emptyList()
    private var pendingBrokenStreak = false
    private var lastFirstSeenMillis = 0L
    private var hasRevenueCatPro = false

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                lastFirstSeenMillis = prefs.firstSeenMillis
                val favoriteQuotes = loadedQuotes.filter { it.id in prefs.favoriteIds }
                val access = LocalAccessPolicy.stateFor(
                    firstSeenMillis = prefs.firstSeenMillis,
                    hasPro = hasRevenueCatPro
                )
                val debugOverride = if (BuildConfig.BUILD_TYPE == "debug") {
                    prefs.debugAccessOverride
                        .takeIf { it.isNotBlank() }
                        ?.let { runCatching { AccessState.valueOf(it) }.getOrNull() }
                } else {
                    null
                }

                _uiState.value = _uiState.value.copy(
                    favoriteIds = prefs.favoriteIds,
                    favoriteQuotes = favoriteQuotes,
                    themeId = prefs.themeId,
                    streak = prefs.streak,
                    bestStreak = prefs.bestStreak,
                    accessState = access,
                    debugAccessOverride = debugOverride,
                    proEnginePackage = prefs.proEnginePackage,
                    proVoiceName = prefs.proVoiceName,
                    proSpeechRate = prefs.proSpeechRate,
                    streakBrokenOnLaunch = pendingBrokenStreak || _uiState.value.streakBrokenOnLaunch
                )
            }
        }

        viewModelScope.launch {
            preferencesRepository.ensureTrialStarted()
            pendingBrokenStreak = preferencesRepository.recordColdOpen().brokePreviousStreak

            runCatching { repository.loadQuotes() }
                .onSuccess { quotes ->
                    loadedQuotes = quotes
                    deck = QuoteDeck(quotes)
                    val openingQuote = if (pendingBrokenStreak) {
                        chooseBrokenStreakQuote(quotes) ?: deck?.next()
                    } else {
                        deck?.next()
                    }
                    _uiState.value = _uiState.value.copy(
                        quote = openingQuote,
                        favoriteQuotes = quotes.filter { it.id in _uiState.value.favoriteIds },
                        isLoading = false,
                        errorMessage = null,
                        streakBrokenOnLaunch = pendingBrokenStreak
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Unable to load the local quote library."
                    )
                }
        }
    }

    fun setRevenueCatPro(hasPro: Boolean) {
        if (hasRevenueCatPro == hasPro && _uiState.value.accessState == AccessState.PRO == hasPro) return
        hasRevenueCatPro = hasPro
        _uiState.value = _uiState.value.copy(
            accessState = LocalAccessPolicy.stateFor(
                firstSeenMillis = lastFirstSeenMillis,
                hasPro = hasRevenueCatPro
            )
        )
    }

    fun nextQuote() {
        val preferredClassifications = _uiState.value.favoriteQuotes
            .groupingBy { it.classification }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }
            .toSet()

        val next = deck?.nextPreferred(
            preferredClassifications = preferredClassifications,
            preferenceChance = 0.70
        ) ?: return
        _uiState.value = _uiState.value.copy(quote = next)
    }

    fun toggleFavorite() {
        val id = _uiState.value.quote?.id ?: return
        toggleFavorite(id)
    }

    fun toggleFavorite(quoteId: Int) {
        viewModelScope.launch { preferencesRepository.toggleFavorite(quoteId) }
    }

    fun selectTheme(themeId: String) {
        viewModelScope.launch { preferencesRepository.setTheme(themeId) }
    }

    fun selectProEngine(enginePackage: String) {
        viewModelScope.launch { preferencesRepository.setProEngine(enginePackage) }
    }

    fun selectProVoice(voiceName: String) {
        viewModelScope.launch { preferencesRepository.setProVoice(voiceName) }
    }

    fun setProSpeechRate(rate: Float) {
        viewModelScope.launch { preferencesRepository.setProSpeechRate(rate) }
    }

    fun setDebugAccessOverride(state: AccessState?) {
        if (BuildConfig.BUILD_TYPE != "debug") return
        _uiState.value = _uiState.value.copy(debugAccessOverride = state)
        viewModelScope.launch {
            preferencesRepository.setDebugAccessOverride(state?.name)
        }
    }

    private fun chooseBrokenStreakQuote(quotes: List<Quote>): Quote? {
        val motivationWords = Regex(
            "\\b(hope|courage|brave|strength|persever|endure|begin|better|fear|progress|struggle)\\w*\\b",
            RegexOption.IGNORE_CASE
        )
        val pool = quotes
            .asSequence()
            .filter { it.classification in setOf("resilience", "courage", "hope") }
            .sortedByDescending { motivationWords.findAll(it.text).count() }
            .take(40)
            .toList()
        return pool.randomOrNull()
    }

    class Factory(
        private val repository: QuoteRepository,
        private val preferencesRepository: AppPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository, preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
