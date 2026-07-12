// Hand.kt — one hand of blackjack cards
package com.blackjack.model

class Hand {
    val cards: MutableList<Card> = mutableListOf()
    var bet: Double = 0.0
    var isDoubled: Boolean = false
        private set
    var isStood: Boolean = false
        private set

    fun addCard(card: Card) { cards.add(card) }

    fun clear() {
        cards.clear()
        bet = 0.0
        isDoubled = false
        isStood = false
    }

    fun stand() { isStood = true }
    fun doubleDown() { isDoubled = true }

    /** Blackjack score — aces count as 11 or 1, whichever is better. */
    val score: Int get() {
        var total = 0
        var aceCount = 0
        for (card in cards) {
            if (!card.faceUp) continue
            total += card.value
            if (card.isAce) aceCount++
        }
        while (total > 21 && aceCount > 0) {
            total -= 10
            aceCount--
        }
        return total
    }

    /** Soft hand — contains an ace counted as 11 */
    val isSoft: Boolean get() {
        var total = 0
        var aceCount = 0
        for (card in cards) {
            if (!card.faceUp) continue
            total += card.value
            if (card.isAce) aceCount++
        }
        return aceCount > 0 && total - (aceCount * 10) <= 11
    }

    val isBust: Boolean get() = score > 21
    val isBlackjack: Boolean get() = cards.size == 2 && score == 21

    /** Can split — first two cards have equal value */
    val canSplit: Boolean get() {
        if (cards.size != 2) return false
        val (a, b) = cards
        if (!a.faceUp || !b.faceUp) return false
        return a.value == b.value
    }

    /** Can double down — only on first two cards */
    val canDoubleDown: Boolean get() = cards.size == 2 && !isDoubled

    fun revealAll() { cards.forEach { it.faceUp = true } }

    override fun toString(): String = cards.joinToString(" ") { it.displayName }
}
