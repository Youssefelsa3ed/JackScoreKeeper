package com.youssefelsa3ed.jackscorekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.youssefelsa3ed.jackscorekeeper.screens.GameScreen
import com.youssefelsa3ed.jackscorekeeper.screens.SetupScreen
import com.youssefelsa3ed.jackscorekeeper.ui.theme.JackScoreKeeperTheme
import com.youssefelsa3ed.jackscorekeeper.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val gameViewModel: GameViewModel = viewModel()
            val gameState by gameViewModel.gameState
            JackScoreKeeperTheme(isDynamicColorEnabled = gameState.isDynamicColorEnabled) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    Surface(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = if (gameState.isGameStarted) "game" else "setup",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("setup") {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    SetupScreen(
                                        gameViewModel = gameViewModel,
                                        onGameStarted = {
                                            navController.navigate("game") {
                                                popUpTo("setup") { inclusive = true }
                                            }
                                        }
                                    )
                                    IconButton(
                                        onClick = { gameViewModel.toggleDynamicColor() },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(16.dp),
                                    ) {
                                        Icon(
                                            imageVector = if (gameState.isDynamicColorEnabled) Icons.Filled.Palette else Icons.Filled.ColorLens,
                                            contentDescription = "Toggle Dynamic Theme"
                                        )
                                    }
                                }

                            }

                            composable("game") {
                                GameScreen(gameViewModel = gameViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}