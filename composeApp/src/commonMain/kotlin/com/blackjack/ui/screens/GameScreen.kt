// GameScreen.kt — Compose Multiplatform 21点 决胜 UI
package com.blackjack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackjack.game.BlackjackGame
import com.blackjack.game.GamePhase
import com.blackjack.game.RoundResult
import com.blackjack.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(initialChips: Int) {
    val game = remember { BlackjackGame(initialChips = initialChips) }
    var refresh by remember { mutableStateOf(0) } // recompose trigger

    // Dealer auto-play timer
    LaunchedEffect(game.phase) {
        if (game.phase == GamePhase.DealerTurn) {
            while (isActive) {
                delay(700)
                val keepGoing = game.dealerHit()
                refresh++
                if (!keepGoing) break
            }
        }
    }

    val phase = game.phase
    val result = game.currentResult

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1B5E20), Color(0xFF0D3B0F), Color(0xFF062008))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp, bottom = 12.dp)
        ) {
            // Top bar — chips
            ChipDisplay(game.chips)

            Spacer(Modifier.height(16.dp))

            // Dealer area
            Text("庄家", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(4.dp))
            if (phase != GamePhase.Betting) {
                Text(
                    "${game.dealer.score}",
                    color = if (game.dealer.score > 21) Color(0xFFFF3B30) else Color.White,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(Modifier.height(8.dp))
            HandRow(game.dealer.cards)

            Spacer(Modifier.height(12.dp))

            // Info area
            InfoBar(phase, result, game.player.score, game.player, game.currentBet)

            Spacer(Modifier.height(12.dp))

            // Player area
            if (game.split != null) {
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    HandColumn("手1", game.player, game.currentBet, isSplit = false,
                        modifier = Modifier.weight(1f))
                    Divider(
                        Modifier.width(2.dp).height(200.dp),
                        color = Color.Gray.copy(alpha = 0.3f)
                    )
                    HandColumn("手2", game.split!!, game.currentBet, isSplit = true,
                        modifier = Modifier.weight(1f))
                }
            } else {
                HandColumn("你", game.player, game.currentBet, isSplit = false,
                    modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            // Action area
            ActionBar(game, refresh) { refresh++ }
        }
    }
}

// ── Components ──

@Composable
private fun ChipDisplay(chips: Int) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            color = Color(0xFFFFD700).copy(alpha = 0.2f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
        ) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("🪙", fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                Text("$chips", color = Color(0xFFFFD700), fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HandRow(cards: List<Card>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(cards) { card -> CardView(card) }
    }
}

@Composable
private fun HandColumn(label: String, hand: Hand, bet: Int, isSplit: Boolean,
                       modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (bet > 0) {
                Text("$bet", color = Color(0xFFFFD700), fontSize = 12.sp,
                    modifier = Modifier.padding(end = 6.dp))
            }
            Text(
                if (isSplit) "分牌" else label,
                color = if (isSplit) Color(0xFFFFCC00) else Color.Gray,
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold
            )
        }
        Text("${hand.score}",
            color = if (hand.isBust) Color(0xFFFF3B30) else Color.White,
            fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LazyRow(Modifier.height(130.dp), horizontalArrangement = Arrangement.Center) {
            items(hand.cards) { card -> CardView(card) }
        }
    }
}

@Composable
private fun CardView(card: Card) {
    val w = 80.dp; val h = 112.dp
    Surface(
        modifier = Modifier.width(w).height(h).padding(horizontal = 3.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (card.faceUp) Color.White else Color(0xFF1565C0),
        shadowElevation = 4.dp
    ) {
        if (card.faceUp) FaceUpCard(card) else FaceDownCard()
    }
}

@Composable
private fun FaceUpCard(card: Card) {
    val color = if (card.suit.isRed) Color(0xFFD32F2F) else Color(0xFF212121)
    Box(Modifier.fillMaxSize()) {
        // Top-left
        Column(Modifier.align(Alignment.TopStart).padding(4.dp).width(20.dp)) {
            Text(card.rank.shortName, color = color, fontSize = 14.sp,
                fontWeight = FontWeight.Bold)
            Text(card.suit.symbol, color = color, fontSize = 11.sp)
        }
        // Center
        Text(card.suit.symbol, color = color, fontSize = 28.sp,
            modifier = Modifier.align(Alignment.Center))
        // Bottom-right (rotated 180°)
        Column(Modifier.align(Alignment.BottomEnd).padding(4.dp).rotate(180f).width(20.dp)) {
            Text(card.rank.shortName, color = color, fontSize = 14.sp,
                fontWeight = FontWeight.Bold)
            Text(card.suit.symbol, color = color, fontSize = 11.sp)
        }
    }
}

@Composable
private fun FaceDownCard() {
    Surface(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.5.dp, Color(0xFF42A5F5))
    ) {
        Box(Modifier.fillMaxSize().border(1.5.dp, Color(0xFF42A5F5).copy(alpha = 0.5f),
            RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
            Text("?", color = Color(0xFF64B5F6), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoBar(phase: GamePhase, result: RoundResult?, score: Int, hand: Hand, bet: Int) {
    Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
        when {
            result != null -> Text(result.message,
                color = when (result) {
                    RoundResult.Win, RoundResult.Blackjack -> Color(0xFFFFD700)
                    RoundResult.Push -> Color.Gray
                    else -> Color(0xFFFF3B30)
                }, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            phase == GamePhase.Betting -> Text("请下注", color = Color.Gray, fontSize = 18.sp)
            phase == GamePhase.PlayerTurn -> {
                Row {
                    Text("你的回合 · 得分 $score", color = Color.White, fontSize = 16.sp)
                    if (hand.isSoft) Text(" S", color = Color(0xFFFFCC00), fontSize = 14.sp)
                }
            }
            phase == GamePhase.DealerTurn -> Text("庄家回合...", color = Color.Gray, fontSize = 16.sp)
            else -> {}
        }
        if (bet > 0 && result == null && phase != GamePhase.Betting) {
            Text("下注: $bet", color = Color.Gray, fontSize = 13.sp,
                modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun ActionBar(game: BlackjackGame, refresh: Int, onRefresh: () -> Unit) {
    val phase = game.phase
    Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
        when (phase) {
            GamePhase.Betting, GamePhase.RoundOver -> BettingArea(game, onRefresh)
            GamePhase.PlayerTurn -> PlayerControls(game, onRefresh)
            else -> CircularProgressIndicator(color = Color(0xFFFFD700))
        }
    }
}

@Composable
private fun BettingArea(game: BlackjackGame, onRefresh: () -> Unit) {
    val chipValues = listOf(10, 25, 50, 100, 500)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (game.currentBet > 0 && game.phase == GamePhase.Betting) {
            Surface(
                color = Color(0xFFFFD700).copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("下注: ${game.currentBet}",
                    color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
            }
        }

        LazyRow(Modifier.padding(horizontal = 16.dp)) {
            items(chipValues) { value ->
                val canAfford = value <= game.chips
                ChipWidget(value, canAfford, onClick = {
                    if (canAfford) { game.placeBet(value); onRefresh() }
                })
            }
        }

        Spacer(Modifier.height(8.dp))

        if (game.phase == GamePhase.RoundOver) {
            Button(onClick = { game.newRound(); onRefresh() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))) {
                Text("再来一局", color = Color.Black, fontSize = 18.sp)
            }
            if (game.chips == 0) {
                TextButton(onClick = { game.reset(1000); onRefresh() }) {
                    Text("重新开始 (1000筹码)", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun PlayerControls(game: BlackjackGame, onRefresh: () -> Unit) {
    val canDouble = game.player.canDoubleDown && game.chips >= game.currentBet
    val canSplit = game.player.canSplit && game.chips >= game.currentBet
    Row(Modifier.padding(horizontal = 8.dp)) {
        ActionButton("要牌", Color(0xFF34C759)) { game.hit(); onRefresh() }
        Spacer(Modifier.width(8.dp))
        ActionButton("停牌", Color(0xFFFF3B30)) { game.stand(); onRefresh() }
        Spacer(Modifier.width(8.dp))
        ActionButton("双倍", Color(0xFFFF9500),
            enabled = canDouble, onClick = { game.doubleDown(); onRefresh() })
        Spacer(Modifier.width(8.dp))
        ActionButton("分牌", Color(0xFF7C4DFF),
            enabled = canSplit, onClick = { game.split(); onRefresh() })
    }
}

@Composable
private fun ActionButton(label: String, color: Color, enabled: Boolean = true,
                         onClick: () -> Unit = {}) {
    Button(
        onClick = onClick, enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) color else Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(16.dp, 12.dp)
    ) {
        Text(label, color = if (enabled) Color.White else Color.Gray, fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ChipWidget(value: Int, enabled: Boolean, onClick: () -> Unit) {
    val chipColors = mapOf(
        10 to Color(0xFF1565C0), 25 to Color(0xFF2E7D32),
        50 to Color(0xFFE65100), 100 to Color(0xFF212121), 500 to Color(0xFF6A1B9A)
    )
    val bg = chipColors[value]?.copy(alpha = if (enabled) 0.9f else 0.3f) ?: Color.Gray
    Surface(
        modifier = Modifier.padding(horizontal = 4.dp).size(62.dp).clickable(enabled = enabled) { onClick() },
        shape = CircleShape, color = bg,
        border = BorderStroke(2.dp, if (enabled) Color(0xFFFFD700).copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.3f)),
        shadowElevation = if (enabled) 6.dp else 0.dp
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("$value", color = if (enabled) Color.White else Color.Gray,
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
