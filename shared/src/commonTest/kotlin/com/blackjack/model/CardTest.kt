package com.blackjack.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardTest {
    @Test
    fun `suit colors are correct`() {
        assertTrue(Suit.HEARTS.isRed)
        assertTrue(Suit.DIAMONDS.isRed)
        assertFalse(Suit.SPADES.isRed)
        assertFalse(Suit.CLUBS.isRed)
    }

    @Test
    fun `ace values are 11`() {
        assertEquals(11, Rank.ACE.value)
    }

    @Test
    fun `face cards are worth 10`() {
        assertEquals(10, Rank.JACK.value)
        assertEquals(10, Rank.QUEEN.value)
        assertEquals(10, Rank.KING.value)
    }

    @Test
    fun `card display name uses suit symbol and rank short name`() {
        val card = Card(Suit.SPADES, Rank.ACE)
        assertEquals("♠A", card.displayName)
    }

    @Test
    fun `card defaults to face up`() {
        val card = Card(Suit.HEARTS, Rank.KING)
        assertTrue(card.faceUp)
    }
}
