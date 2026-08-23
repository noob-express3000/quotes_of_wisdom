package com.shipaton.quotesofwisdom.data

import android.content.Context
import com.shipaton.quotesofwisdom.model.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class AssetQuoteRepository(
    context: Context
) : QuoteRepository {

    private val appContext = context.applicationContext

    override suspend fun loadQuotes(): List<Quote> = withContext(Dispatchers.IO) {
        val json = appContext.assets
            .open("quotes.json")
            .bufferedReader()
            .use { it.readText() }

        val array = JSONArray(json)
        val quotes = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    Quote(
                        id = item.getInt("id"),
                        text = item.getString("text"),
                        author = item.getString("author"),
                        classification = item.getString("classification")
                    )
                )
            }
        }

        require(quotes.isNotEmpty()) { "quotes.json must contain at least one quote" }
        quotes
    }
}
