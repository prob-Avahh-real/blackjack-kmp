// Deck.kt — multi-deck shoe with Fisher-Yates shuffle
package com.blackjack.model

import kotlin.random.Random

class Deck(private val numDecks: Int = 6) {
    private val cards: MutableList<Card> = mutableListOf()
    private val random = Random.Default

    init { reset() }

    fun reset() {
        cards.clear()
        repeat(numDecks) {
            Suit.entries.forEach { suit ->
                Rank.entries.forEach { rank ->
                    cards.add(Card(suit, rank))
                }
            }
        }
        shuffle()
    }

    /** Fisher-Yates shuffle */
    fun shuffle() {
        for (i in cards.lastIndex downTo 1) {
            val j = random.nextInt(i + 1)
            val temp = cards[i]
            cards[i] = cards[j]
            cards[j] = temp
        }
    }

    /** Deal one card. Auto-reshuffles if shoe is empty. */
    fun deal(faceUp: Boolean = true): Card {
        if (cards.isEmpty()) reset()
        val card = cards.removeLast()
        card.faceUp = faceUp
        return card
    }

    val remaining: Int get() = cards.size

    /** Penetration ratio: fraction of cards already dealt */
    val penetration: Double get() = 1.0 - (cards.size.toDouble() / (numDecks * 52))
}
