package com.shipaton.quotesofwisdom.data

import com.shipaton.quotesofwisdom.model.Quote

interface QuoteRepository {
    suspend fun loadQuotes(): List<Quote>
}
