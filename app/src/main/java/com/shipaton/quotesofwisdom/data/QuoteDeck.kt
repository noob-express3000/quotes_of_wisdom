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
    private var deck: MutableList<Quote> = shuffledAvoiding(previous).toMutableList()
    private var index = 0

    fun next(): Quote = nextPreferred(emptySet(), 0.0)

    fun nextPreferred(
        preferredClassifications: Set<String>,
        preferenceChance: Double = 0.65
    ): Quote {
        if (index >= deck.size) {
            deck = shuffledAvoiding(previous).toMutableList()
            index = 0
        }

        if (
            preferredClassifications.isNotEmpty() &&
            random.nextDouble() < preferenceChance.coerceIn(0.0, 1.0)
        ) {
            val preferredIndex = (index until deck.size).firstOrNull { candidateIndex ->
                deck[candidateIndex].classification in preferredClassifications
            }
            if (preferredIndex != null && preferredIndex != index) {
                val current = deck[index]
                deck[index] = deck[preferredIndex]
                deck[preferredIndex] = current
            }
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
