# blackjack-kmp
A complete Blackjack card game built with Kotlin Multiplatform + Compose Multiplatform.

## Structure
- `shared/` — shared business logic: Card, Deck, Hand, BlackjackGame
- `composeApp/` — Compose Multiplatform UI with Material3
- `iosApp/` — iOS entry point

## Build
```bash
./gradlew :composeApp:assembleDebug
```

## Features
- 6-deck shoe with Fisher-Yates shuffle
- Blackjack, hit, stand, double down, split
- Dealer auto-play (hits on soft 17)
- Chip management, multi-round play
- Dark green felt UI with gold accents
