package com.shipaton.quotesofwisdom.data

import com.shipaton.quotesofwisdom.model.Quote
import kotlin.random.Random

class QuoteDeck(
    private val source: List<Quote>,
    private val random: Random = Random.Default
) {
    init {
        require(source.isNotEmpty()) { "QuoteDeck requires at least one quote" }
    }

    private var previous: Quote? = null
    private var deck: List<Quote> = shuffledAvoiding(previous)
    private var index = 0

    fun next(): Quote {
        if (index >= deck.size) {
            deck = shuffledAvoiding(previous)
            index = 0
        }

        return deck[index++].also { previous = it }
    }

    private fun shuffledAvoiding(lastQuote: Quote?): List<Quote> {
        if (source.size == 1) return source

        val shuffled = source.shuffled(random).toMutableList()
        if (lastQuote != null && shuffled.first().id == lastQuote.id) {
            val replacementIndex = shuffled.indexOfFirst { it.id != lastQuote.id }
            if (replacementIndex > 0) {
                val first = shuffled[0]
                shuffled[0] = shuffled[replacementIndex]
                shuffled[replacementIndex] = first
            }
        }
        return shuffled
    }
}
