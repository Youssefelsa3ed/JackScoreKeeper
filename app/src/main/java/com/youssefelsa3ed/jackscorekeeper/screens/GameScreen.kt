package com.youssefelsa3ed.jackscorekeeper.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.youssefelsa3ed.jackscorekeeper.R
import com.youssefelsa3ed.jackscorekeeper.data.ContractResult
import com.youssefelsa3ed.jackscorekeeper.data.ContractType
import com.youssefelsa3ed.jackscorekeeper.data.Player
import com.youssefelsa3ed.jackscorekeeper.ui.dialog.ContractInputDialog
import com.youssefelsa3ed.jackscorekeeper.ui.dialog.EndGameDialog
import com.youssefelsa3ed.jackscorekeeper.ui.theme.AppColors
import com.youssefelsa3ed.jackscorekeeper.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameViewModel: GameViewModel
) {
    val gameState by gameViewModel.gameState
    val uiState by gameViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "الجولة ${gameState.currentRound} من ${gameState.totalRounds} (${gameState.currentPlayer?.name})",
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(
                    onClick = { gameViewModel.toggleDynamicColor() },
                ) {
                    Icon(
                        imageVector = if (gameState.isDynamicColorEnabled) Icons.Filled.Palette else Icons.Filled.ColorLens,
                        contentDescription = "Toggle Dynamic Theme"
                    )
                }
                IconButton(onClick = { gameViewModel.undoLastContract() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "تراجع")
                }
                IconButton(
                    onClick = { gameViewModel.resetGame() },
                    enabled = gameState.contractHistory.isNotEmpty()
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "إعادة تشغيل"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Scoreboard
            item {
                ScoreboardCard(gameState.players)
            }

            // Contract Buttons
            item {
                ContractButtonsGrid(
                    onContractSelected = { contractType ->
                        gameViewModel.showContractDialog(contractType)
                    },
                    usedContracts = gameState.currentPlayerUsedContracts
                )
            }

            // Next Player Button / End Game Button
            item {
                if (gameState.isAllRoundsCompleted) {
                    Button(
                        onClick = {
                            gameViewModel.showEndGameDialog()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.SuccessContainer,
                            contentColor = AppColors.OnSuccessContainer
                        )
                    ) {
                        Text(
                            text = "إنهاء اللعبة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = { gameViewModel.moveToNextPlayer() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = gameState.allContractsUsedForCurrentPlayer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (gameState.allContractsUsedForCurrentPlayer) {
                                "اللاعب التالي"
                            } else {
                                "استكمل جميع العقود أولاً"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Game History
            if (gameState.contractHistory.isNotEmpty())
                items(gameState.contractHistory.reversed()) { contract ->
                    ContractHistoryItem(contract, gameState.players)
                }
        }
    }
    // Contract Input Dialog
    if (uiState.showContractDialog && uiState.selectedContract != null) {
        @Suppress("UNCHECKED_CAST")
        ContractInputDialog(
            contractType = uiState.selectedContract!!,
            players = gameState.players,
            onDismiss = { gameViewModel.hideContractDialog() },
            onSubmit = { contractType, input ->
                val success = when (contractType) {
                    ContractType.NO_DAME -> gameViewModel.recordNoDame(input as List<Int>)
                    ContractType.NO_KING_HEART -> gameViewModel.recordNoKingHeart(input as Int?)
                    ContractType.NO_DIAMONDS -> gameViewModel.recordNoDiamonds(input as List<Int>)
                    ContractType.ESTIMATION -> gameViewModel.recordEstimation(input as List<Int>)
                    ContractType.JACK -> gameViewModel.recordTrex(input as List<Int>)
                }
                if (success) {
                    gameViewModel.hideContractDialog()
                }
            }
        )
    }
    // End Game Dialog
    if (uiState.showEndGameDialog)
        EndGameDialog(
            playerRankings = gameState.getPlayerRanking(),
            onDismiss = { gameViewModel.endGame() }
        )
}

@Composable
fun ScoreboardCard(players: List<Player>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "النتائج الإجمالية",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            players.forEach { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "${player.totalScore}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (player.totalScore >= 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
                if (player != players.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun ContractButtonsGrid(
    onContractSelected: (ContractType) -> Unit,
    usedContracts: Set<ContractType>
) {
    Column {
        Text(
            text = "اختر نوع العقد",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface
        )

        // First row: Negative contracts
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            listOf(ContractType.NO_DAME, ContractType.NO_KING_HEART, ContractType.NO_DIAMONDS).forEach { contract ->
                ContractButton(
                    contractType = contract,
                    onClick = { onContractSelected(contract) },
                    modifier = Modifier.weight(1f),
                    isUsed = contract in usedContracts
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Second row: Positive contracts
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            listOf(ContractType.ESTIMATION, ContractType.JACK).forEach { contract ->
                ContractButton(
                    contractType = contract,
                    onClick = { onContractSelected(contract) },
                    modifier = Modifier.weight(1f),
                    isUsed = contract in usedContracts
                )
            }
        }
    }
}

@Composable
fun ContractButton(
    contractType: ContractType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUsed: Boolean = false
) {
    val containerColor = when {
        isUsed -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val contentColor = when {
        isUsed -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = modifier,
        onClick = if (!isUsed) onClick else { {} },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUsed) 0.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(
                    when (contractType) {
                        ContractType.NO_DAME -> R.drawable.no_dam
                        ContractType.NO_KING_HEART -> R.drawable.no_rwa
                        ContractType.NO_DIAMONDS -> R.drawable.no_caro
                        ContractType.ESTIMATION -> R.drawable.estimation
                        ContractType.JACK -> R.drawable.jacs
                    }
                ),
                contentDescription = contractType.displayNameAr,
                modifier = Modifier.size(36.dp)
            )

            Text(
                text = if (isUsed) "✓ ${contractType.displayNameAr}" else contractType.displayNameAr,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isUsed) FontWeight.Normal else FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ContractHistoryItem(
    contractResult: ContractResult,
    players: List<Player>
) {
    val activatingPlayer = players.find { it.id == contractResult.activatingPlayerId }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (contractResult.contractType.isNegative) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contractResult.contractType.displayNameAr,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "بواسطة: ${activatingPlayer?.name ?: "غير معروف"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = contractResult.details,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Show score changes
            contractResult.playerScores.forEach { (playerId, score) ->
                val player = players.find { it.id == playerId }
                if (player != null && score != 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${if (score > 0) "+" else ""}$score",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (score >= 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            }
        }
    }
}