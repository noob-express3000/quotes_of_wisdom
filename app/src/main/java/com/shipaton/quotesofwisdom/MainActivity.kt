package com.shipaton.quotesofwisdom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.shipaton.quotesofwisdom.data.AssetQuoteRepository
import com.shipaton.quotesofwisdom.ui.home.HomeScreen
import com.shipaton.quotesofwisdom.ui.home.HomeViewModel
import com.shipaton.quotesofwisdom.ui.theme.QuotesOfWisdomTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory(
            AssetQuoteRepository(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by homeViewModel.uiState.collectAsState()

            QuotesOfWisdomTheme {
                HomeScreen(
                    uiState = uiState,
                    onNextQuote = homeViewModel::nextQuote,
                    onReplay = {}
                )
            }
        }
    }
}
