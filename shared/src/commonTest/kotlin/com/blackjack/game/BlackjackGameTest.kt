package com.blackjack.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BlackjackGameTest {
    @Test
    fun `game starts in betting phase`() {
        val game = BlackjackGame()
        assertEquals(GamePhase.Betting, game.phase)
    }

    @Test
    fun `place bet transitions to dealing`() {
        val game = BlackjackGame()
        val placed = game.placeBet(100)
        assertTrue(placed)
        assertEquals(GamePhase.PlayerTurn, game.phase)
    }

    @Test
    fun `cannot bet more than chips`() {
        val game = BlackjackGame(initialChips = 50)
        val placed = game.placeBet(100)
        assertFalse(placed)
    }

    @Test
    fun `initial bet deducts chips`() {
        val game = BlackjackGame(initialChips = 1000)
        game.placeBet(100)
        assertEquals(900, game.chips)
    }

    @Test
    fun `hit adds a card to player hand`() {
        val game = BlackjackGame()
        game.placeBet(100)
        assertEquals(2, game.player.cards.size)
        game.hit()
        assertEquals(3, game.player.cards.size)
    }

    @Test
    fun `stand transitions to dealer turn`() {
        val game = BlackjackGame()
        game.placeBet(100)
        game.stand()
        assertEquals(GamePhase.DealerTurn, game.phase)
    }

    @Test
    fun `dealer hit adds card`() {
        val game = BlackjackGame()
        game.placeBet(100)
        game.stand()
        val hadAction = game.dealerHit()
        assertNotNull(hadAction)
    }

    @Test
    fun `new round resets to betting`() {
        val game = BlackjackGame()
        game.placeBet(100)
        game.newRound()
        assertEquals(GamePhase.Betting, game.phase)
        assertEquals(0, game.currentBet)
    }

    @Test
    fun `full round ends with result`() {
        val game = BlackjackGame()
        game.placeBet(100)
        game.stand()
        // Play out dealer hits
        while (game.dealerHit()) {}
        assertEquals(GamePhase.RoundOver, game.phase)
    }

    @Test
    fun `double down doubles bet`() {
        val game = BlackjackGame(initialChips = 1000)
        game.placeBet(100)
        val doubled = game.doubleDown()
        assertTrue(doubled)
    }

    @Test
    fun `reset restores game state`() {
        val game = BlackjackGame()
        game.placeBet(100)
        game.reset()
        assertEquals(GamePhase.Betting, game.phase)
        assertEquals(1000, game.chips)
    }
}
