package com.shipaton.quotesofwisdom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.shipaton.quotesofwisdom.ui.home.HomeScreen
import com.shipaton.quotesofwisdom.ui.theme.QuotesOfWisdomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuotesOfWisdomTheme {
                HomeScreen()
            }
        }
    }
}
