package com.blackjack.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeckTest {
    @Test
    fun `single deck has 52 cards`() {
        val deck = Deck(1)
        assertEquals(52, deck.remaining)
    }

    @Test
    fun `six deck shoe has 312 cards`() {
        val deck = Deck(6)
        assertEquals(312, deck.remaining)
    }

    @Test
    fun `dealing reduces remaining count`() {
        val deck = Deck(1)
        deck.deal()
        assertEquals(51, deck.remaining)
    }

    @Test
    fun `automatic reshuffle when empty`() {
        val deck = Deck(1)
        repeat(52) { deck.deal() }
        assertEquals(0, deck.remaining)
        // Next deal triggers reshuffle
        deck.deal()
        assertEquals(51, deck.remaining)
    }

    @Test
    fun `dealt card is face up by default`() {
        val deck = Deck(1)
        val card = deck.deal()
        assertTrue(card.faceUp)
    }

    @Test
    fun `dealt card can be face down`() {
        val deck = Deck(1)
        val card = deck.deal(faceUp = false)
        assertFalse(card.faceUp)
    }
}
