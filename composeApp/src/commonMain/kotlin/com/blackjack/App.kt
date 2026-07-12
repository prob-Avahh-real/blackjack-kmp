// App.kt — Compose Multiplatform entry point
package com.blackjack

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.blackjack.ui.screens.GameScreen

private val DarkGreen = Color(0xFF1B5E20)
private val Gold = Color(0xFFFFD700)

private val BlackjackColorScheme = darkColorScheme(
    primary = Gold,
    secondary = Color(0xFF42A5F5),
    surface = Color(0xFF0D3B0F),
    background = Color(0xFF062008),
    onSurface = Color.White
)

@Composable
fun App() {
    MaterialTheme(colorScheme = BlackjackColorScheme) {
        GameScreen(initialChips = 1000)
    }
}
