# Jack Score Keeper (حاسبة جاكس)

A native Android score keeper for a house-rules variant of the **Jack** card game, played by 4 players. It replaces the per-card point counting with a simple contract-based system, removes arithmetic mistakes and arguments, and keeps an accurate digital record of the game.

Built with **Kotlin**, **Jetpack Compose**, and **Material 3**. The UI is in Arabic with full RTL support.

---

## Features

- **Scoreboard** showing every player's running total at all times
- **Five contracts** with automatic scoring (see rules below)
- **Strict input validation**: counts must add up exactly, never more and never less
- **Contract locking**: a contract can only be played once per player turn
- **Automatic turn and round flow**: once all five contracts are played, the next player's turn begins and contracts are re-enabled
- **End Game** button replaces "Next Player" when all rounds are finished, returning to the setup screen
- **Round history** with the points each player gained or lost per contract
- **Undo** the last contract to fix input mistakes
- Light and dark themes

---

## Game rules implemented

The game has 4 players. On their turn, each player activates all five contracts, in any order.

| Contract | Arabic | Type | Scoring |
|---|---|---|---|
| No Dame | نو دام | Avoid | -50 per Queen taken |
| No King of Hearts | نو كنغ هارت | Avoid | -75 to the player who took it, 0 for the others |
| No Diamonds | نو كارو | Avoid | -10 per Diamond card taken |
| Estimation | إستيميشن | Collect | +10 per trick won (no bidding) |
| Jack | جاكس | Ranking | 1st: +480, 2nd: +360, 3rd: +240, 4th: +120 |

### Input validation

| Contract | Input | Rule |
|---|---|---|
| No Dame | Queens per player (0-4) | Total must equal **4** |
| No King of Hearts | One player | Exactly one player must be selected |
| No Diamonds | Diamonds per player (0-13) | Total must equal **13** |
| Estimation | Tricks per player (0-13) | Total must equal **13** |
| Jack | Finishing order | Each player takes exactly one position |

The confirm button stays disabled until the totals are correct, and a live counter (for example `Total: 11/13`) shows how far off the input is.

---

## Tech stack

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (single `GameViewModel`) |
| Navigation | Navigation Compose |
| Serialization | kotlinx.serialization |
| Min SDK | 24 (Android 7.0) |
| Target / Compile SDK | 34 |

---

## Project structure

```
app/src/main/kotlin/com/trexscorekeeper/
├── MainActivity.kt              # Entry point and navigation host
├── data/
│   └── Models.kt                # Player, ContractType, ContractResult, GameState
└── ui/
    ├── dialogs/
    │   └── ContractInputDialog.kt   # Input and validation for each contract
    ├── screens/
    │   ├── SetupScreen.kt           # Enter the 4 player names
    │   └── GameScreen.kt            # Scoreboard, contracts, history
    ├── theme/
    │   └── Theme.kt                 # Material 3 light/dark color schemes
    └── viewmodel/
        └── GameViewModel.kt         # Game state, scoring, validation, undo
```

---

## Getting started

### Requirements

- Android Studio (latest stable recommended)
- JDK 17
- An Android device or emulator running Android 7.0 (API 24) or higher

### Run from Android Studio

1. Clone the repository:
   ```bash
   git clone https://github.com/Youssefelsa3ed/JackScoreKeeper.git
   ```
2. Open the project in Android Studio and let Gradle sync.
3. Select a device or emulator and press **Run**.

### Build an APK from the command line

```bash
# Debug APK
./gradlew assembleDebug

# Output:
# app/build/outputs/apk/debug/app-debug.apk
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

You can copy the debug APK to any Android phone and install it directly (you may need to allow installs from unknown sources).

---

## How to play with the app

1. Enter the four player names and tap **Start Game**.
2. The current player activates a contract and enters the result.
3. Repeat until all five contracts are used. Used contracts are greyed out with a checkmark.
4. Tap **Next Player**. Contracts are re-enabled for the next player.
5. When all rounds are done, tap **End Game** to return to the setup screen.

Made a mistake? Use the undo button in the top bar to revert the last contract.

---

## Platform support

| Platform | Status |
|---|---|
| Android | Supported |
| iOS | Not available yet |
| Web | Not available yet |

---

## Contributing

Issues and pull requests are welcome. For larger changes, please open an issue first to discuss what you would like to change.

---

## License

This project is licensed under the [MIT License](LICENSE).
