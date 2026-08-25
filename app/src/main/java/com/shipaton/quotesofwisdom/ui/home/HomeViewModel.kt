package com.shipaton.quotesofwisdom.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    init {
        viewModelScope.launch {
            preferencesRepository.ensureTrialStarted()
            val streakUpdate = preferencesRepository.recordColdOpen()
            pendingBrokenStreak = streakUpdate.brokePreviousStreak
        }

        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                val access = LocalAccessPolicy.stateFor(prefs.firstSeenMillis)
                _uiState.value = _uiState.value.copy(
                    favoriteIds = prefs.favoriteIds,
                    favoriteQuotes = loadedQuotes.filter { it.id in prefs.favoriteIds },
                    themeId = prefs.themeId,
                    streak = prefs.streak,
                    bestStreak = prefs.bestStreak,
                    accessState = access,
                    proVoiceName = prefs.proVoiceName,
                    proSpeechRate = prefs.proSpeechRate,
                    streakBrokenOnLaunch = pendingBrokenStreak || _uiState.value.streakBrokenOnLaunch
                )
            }
        }

        viewModelScope.launch {
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

    fun nextQuote() {
        val next = deck?.next() ?: return
        _uiState.value = _uiState.value.copy(quote = next)
    }

    fun toggleFavorite() {
        val id = _uiState.value.quote?.id ?: return
        viewModelScope.launch { preferencesRepository.toggleFavorite(id) }
    }

    fun selectTheme(themeId: String) {
        viewModelScope.launch { preferencesRepository.setTheme(themeId) }
    }

    fun selectProVoice(voiceName: String) {
        viewModelScope.launch { preferencesRepository.setProVoice(voiceName) }
    }

    fun setProSpeechRate(rate: Float) {
        viewModelScope.launch { preferencesRepository.setProSpeechRate(rate) }
    }

    fun setDebugAccessOverride(state: AccessState?) {
        _uiState.value = _uiState.value.copy(debugAccessOverride = state)
    }

    private fun chooseBrokenStreakQuote(quotes: List<Quote>): Quote? {
        val motivationWords = Regex("\\b(hope|courage|brave|strength|persever|endure|begin|better|fear|progress|struggle)\\w*\\b", RegexOption.IGNORE_CASE)
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
