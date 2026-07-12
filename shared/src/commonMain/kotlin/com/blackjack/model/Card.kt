// Card.kt — SDP (Standard Design Pattern) declaration mapping
package com.blackjack.model

// Snake-case const → KMP properties
enum class Suit(val symbol: String, val label: String) {
    SPADES("♠", "黑桃"),
    HEARTS("♥", "红心"),
    DIAMONDS("♦", "方块"),
    CLUBS("♣", "梅花");

    val isRed: Boolean get() = this == HEARTS || this == DIAMONDS
}

enum class Rank(
    val shortName: String,
    val displayName: String,
    val value: Int
) {
    ACE("A", "A", 11),
    TWO("2", "2", 2),
    THREE("3", "3", 3),
    FOUR("4", "4", 4),
    FIVE("5", "5", 5),
    SIX("6", "6", 6),
    SEVEN("7", "7", 7),
    EIGHT("8", "8", 8),
    NINE("9", "9", 9),
    TEN("10", "10", 10),
    JACK("J", "J", 10),
    QUEEN("Q", "Q", 10),
    KING("K", "K", 10)
}

// SDP position → kotlinx.serialization data class
// @Serializable annotation via kotlinx.serialization
data class Card(
    val suit: Suit,
    val rank: Rank,
    var faceUp: Boolean = true
) {
    val value: Int get() = rank.value
    val isAce: Boolean get() = rank == Rank.ACE
    val isTenValue: Boolean get() = value == 10
    val displayName: String get() = "${suit.symbol}${rank.shortName}"
}
