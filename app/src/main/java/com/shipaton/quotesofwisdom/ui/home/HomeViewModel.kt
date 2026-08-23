package com.shipaton.quotesofwisdom.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shipaton.quotesofwisdom.data.QuoteDeck
import com.shipaton.quotesofwisdom.data.QuoteRepository
import com.shipaton.quotesofwisdom.model.Quote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val quote: Quote? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val repository: QuoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var deck: QuoteDeck? = null

    init {
        viewModelScope.launch {
            runCatching { repository.loadQuotes() }
                .onSuccess { quotes ->
                    deck = QuoteDeck(quotes)
                    _uiState.value = HomeUiState(
                        quote = deck?.next(),
                        isLoading = false
                    )
                }
                .onFailure {
                    _uiState.value = HomeUiState(
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

    class Factory(
        private val repository: QuoteRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
