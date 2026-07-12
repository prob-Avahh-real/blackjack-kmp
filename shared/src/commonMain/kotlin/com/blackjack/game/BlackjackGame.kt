// BlackjackGame.kt — core game logic (stateless engine + mutable state)
package com.blackjack.game

import com.blackjack.model.*

enum class GamePhase { Betting, Dealing, PlayerTurn, DealerTurn, RoundOver }

enum class RoundResult(val message: String) {
    Win("🎉 你赢了！"),
    Lose("😔 庄家赢"),
    Push("🤝 平局"),
    Blackjack("💎 Blackjack！"),
    Bust("💥 爆牌")
}

class BlackjackGame(initialChips: Int = 1000, numDecks: Int = 6) {
    private val deck = Deck(numDecks)
    private val playerHand = Hand()
    private val dealerHand = Hand()
    private var splitHand: Hand? = null

    var chips: Int = initialChips
        private set
    var currentBet: Int = 0
        private set
    var phase: GamePhase = GamePhase.Betting
        private set

    // Read-only accessors for UI
    val player: Hand get() = playerHand
    val dealer: Hand get() = dealerHand
    val split: Hand? get() = splitHand

    fun placeBet(amount: Int): Boolean {
        if (phase != GamePhase.Betting && phase != GamePhase.RoundOver) return false
        if (amount < 1 || amount > chips) return false
        currentBet = amount
        chips -= amount
        playerHand.clear()
        dealerHand.clear()
        splitHand = null
        playerHand.bet = amount.toDouble()
        phase = GamePhase.Dealing
        dealInitialCards()
        return true
    }

    private fun dealInitialCards() {
        playerHand.addCard(deck.deal())
        dealerHand.addCard(deck.deal())
        playerHand.addCard(deck.deal())
        dealerHand.addCard(deck.deal(faceUp = false))
        checkForBlackjack()
    }

    private fun checkForBlackjack() {
        when {
            playerHand.isBlackjack && dealer.score != 21 -> {
                dealerHand.revealAll()
                settleRound(RoundResult.Blackjack)
            }
            playerHand.isBlackjack && dealer.score == 21 -> {
                dealerHand.revealAll()
                settleRound(RoundResult.Push)
            }
            dealerHand.isBlackjack -> {
                dealerHand.revealAll()
                settleRound(RoundResult.Lose)
            }
            else -> phase = GamePhase.PlayerTurn
        }
    }

    fun hit() {
        if (phase != GamePhase.PlayerTurn || playerHand.isStood) return
        playerHand.addCard(deck.deal())
        if (playerHand.isBust) stand()
    }

    fun stand() {
        if (phase != GamePhase.PlayerTurn) return
        playerHand.stand()
        startDealerTurn()
    }

    fun doubleDown(): Boolean {
        if (phase != GamePhase.PlayerTurn) return false
        if (!playerHand.canDoubleDown) return false
        if (chips < currentBet) return false
        chips -= currentBet
        currentBet *= 2
        playerHand.bet = currentBet.toDouble()
        playerHand.doubleDown()
        playerHand.addCard(deck.deal())
        if (playerHand.isBust) {
            resolvePlayerBust()
        } else {
            playerHand.stand()
            startDealerTurn()
        }
        return true
    }

    fun split(): Boolean {
        if (phase != GamePhase.PlayerTurn) return false
        if (!playerHand.canSplit) return false
        if (chips < currentBet) return false
        chips -= currentBet
        splitHand = Hand().apply {
            bet = currentBet.toDouble()
            addCard(playerHand.cards.removeAt(1))
        }
        playerHand.addCard(deck.deal())
        splitHand!!.addCard(deck.deal())
        playerHand.stand()
        splitHand!!.stand()
        startDealerTurn()
        return true
    }

    private fun startDealerTurn() {
        phase = GamePhase.DealerTurn
        dealerHand.revealAll()
    }

    /** Returns true if dealer took a card, false if dealer stands. */
    fun dealerHit(): Boolean {
        if (dealer.score < 17 || (dealer.score == 17 && dealer.isSoft)) {
            dealerHand.addCard(deck.deal())
            return true
        }
        settleAllHands()
        return false
    }

    private fun resolvePlayerBust() {
        phase = GamePhase.RoundOver
        dealerHand.revealAll()
    }

    private fun settleAllHands() {
        phase = GamePhase.RoundOver
        if (!playerHand.isBust) settleRound(compareHands(playerHand, dealerHand))
    }

    private fun compareHands(player: Hand, dealer: Hand): RoundResult = when {
        player.isBust -> RoundResult.Bust
        dealer.isBust -> RoundResult.Win
        player.score > dealer.score -> RoundResult.Win
        player.score < dealer.score -> RoundResult.Lose
        else -> RoundResult.Push
    }

    private fun settleRound(result: RoundResult) {
        phase = GamePhase.RoundOver
        when (result) {
            RoundResult.Blackjack -> chips += (currentBet * 2.5).toInt()
            RoundResult.Win -> chips += currentBet * 2
            RoundResult.Push -> chips += currentBet
            RoundResult.Lose, RoundResult.Bust -> {}
        }
        splitHand?.let {
            val splitResult = compareHands(it, dealerHand)
            val splitBet = it.bet.toInt()
            when (splitResult) {
                RoundResult.Win -> chips += splitBet * 2
                RoundResult.Push -> chips += splitBet
                else -> {}
            }
        }
    }

    val currentResult: RoundResult? get() {
        if (phase != GamePhase.RoundOver) return null
        if (playerHand.isBust) return RoundResult.Bust
        if (dealerHand.isBust) return RoundResult.Win
        return when {
            player.score > dealer.score ->
                if (playerHand.isBlackjack) RoundResult.Blackjack else RoundResult.Win
            player.score < dealer.score -> RoundResult.Lose
            else -> RoundResult.Push
        }
    }

    fun newRound() {
        playerHand.clear()
        dealerHand.clear()
        splitHand = null
        currentBet = 0
        if (deck.penetration > 0.75) deck.reset()
        phase = GamePhase.Betting
    }

    fun reset(initialChips: Int = 1000) {
        chips = initialChips
        currentBet = 0
        playerHand.clear()
        dealerHand.clear()
        splitHand = null
        deck.reset()
        phase = GamePhase.Betting
    }
}
