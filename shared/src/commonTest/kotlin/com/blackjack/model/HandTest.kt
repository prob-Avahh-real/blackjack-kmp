package com.blackjack.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HandTest {
    @Test
    fun `empty hand scores 0`() {
        val hand = Hand()
        assertEquals(0, hand.score)
    }

    @Test
    fun `hand with two cards scores correctly`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.TEN))
        hand.addCard(Card(Suit.HEARTS, Rank.FIVE))
        assertEquals(15, hand.score)
    }

    @Test
    fun `ace counts as 11 when under 21`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.ACE))
        hand.addCard(Card(Suit.HEARTS, Rank.SEVEN))
        assertEquals(18, hand.score)
    }

    @Test
    fun `ace counts as 1 when 21 would be exceeded`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.ACE))
        hand.addCard(Card(Suit.HEARTS, Rank.SEVEN))
        hand.addCard(Card(Suit.DIAMONDS, Rank.EIGHT))
        // 11 + 7 + 8 = 26 -> bust, so ace becomes 1 -> 1 + 7 + 8 = 16
        assertEquals(16, hand.score)
    }

    @Test
    fun `blackjack detection`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.ACE))
        hand.addCard(Card(Suit.HEARTS, Rank.KING))
        assertTrue(hand.isBlackjack)
    }

    @Test
    fun `soft hand detection`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.ACE))
        hand.addCard(Card(Suit.HEARTS, Rank.SIX))
        assertTrue(hand.isSoft)
    }

    @Test
    fun `bust detection`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.TEN))
        hand.addCard(Card(Suit.HEARTS, Rank.KING))
        hand.addCard(Card(Suit.DIAMONDS, Rank.FIVE))
        assertTrue(hand.isBust)
    }

    @Test
    fun `can split same-value cards`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.EIGHT))
        hand.addCard(Card(Suit.HEARTS, Rank.EIGHT))
        assertTrue(hand.canSplit)
    }

    @Test
    fun `face-down cards are excluded from score`() {
        val hand = Hand()
        hand.addCard(Card(Suit.SPADES, Rank.TEN, faceUp = true))
        hand.addCard(Card(Suit.HEARTS, Rank.FIVE, faceUp = false))
        assertEquals(10, hand.score)
    }
}
