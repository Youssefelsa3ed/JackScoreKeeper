package com.youssefelsa3ed.jackscorekeeper.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.youssefelsa3ed.jackscorekeeper.data.ContractResult
import com.youssefelsa3ed.jackscorekeeper.data.ContractType
import com.youssefelsa3ed.jackscorekeeper.data.GameState
import com.youssefelsa3ed.jackscorekeeper.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _gameState = mutableStateOf(GameState())
    val gameState: State<GameState> = _gameState

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val showContractDialog: Boolean = false,
        val showEndGameDialog: Boolean = false,
        val selectedContract: ContractType? = null
    )

    fun toggleDynamicColor() {
        _gameState.value = _gameState.value.copy(
            isDynamicColorEnabled = !_gameState.value.isDynamicColorEnabled
        )
    }

    fun initializePlayers(playerNames: List<String>) {
        if (playerNames.size != 4) return

        _gameState.value = _gameState.value.copy(
            players = playerNames.mapIndexed { index, name ->
                Player(id = index, name = name.trim())
            },
            isGameStarted = true
        )
    }

    fun recordNoDame(queensPerPlayer: List<Int>): Boolean {
        if (!validateQueensInput(queensPerPlayer)) return false

        val scores = mutableMapOf<Int, Int>()
        queensPerPlayer.forEachIndexed { index, queens ->
            scores[index] = queens * -50
        }

        return addContractResult(
            ContractType.NO_DAME,
            scores,
            "بنات: ${queensPerPlayer.joinToString(",")}"
        )
    }

    fun recordNoKingHeart(playerWithKingIndex: Int?): Boolean {
        if (playerWithKingIndex == null || playerWithKingIndex !in 0..3) return false

        val scores = mutableMapOf<Int, Int>()
        _gameState.value.players.forEachIndexed { index, _ ->
            scores[index] = if (index == playerWithKingIndex) -75 else 0
        }

        return addContractResult(
            ContractType.NO_KING_HEART,
            scores,
            "شايب القلب: ${_gameState.value.players[playerWithKingIndex].name}"
        )
    }

    fun recordNoDiamonds(diamondsPerPlayer: List<Int>): Boolean {
        if (!validateDiamondsInput(diamondsPerPlayer)) return false

        val scores = mutableMapOf<Int, Int>()
        diamondsPerPlayer.forEachIndexed { index, diamonds ->
            scores[index] = diamonds * -10
        }

        return addContractResult(
            ContractType.NO_DIAMONDS,
            scores,
            "كارو: ${diamondsPerPlayer.joinToString(",")}"
        )
    }

    fun recordEstimation(tricksPerPlayer: List<Int>): Boolean {
        if (!validateTricksInput(tricksPerPlayer)) return false

        val scores = mutableMapOf<Int, Int>()
        tricksPerPlayer.forEachIndexed { index, tricks ->
            scores[index] = tricks * 15
        }

        return addContractResult(
            ContractType.ESTIMATION,
            scores,
            "لمات: ${tricksPerPlayer.joinToString(",")}"
        )
    }

    fun recordTrex(playerRankings: List<Int>): Boolean {
        if (!validateJackRankings(playerRankings)) return false

        val trexScores = listOf(480, 360, 240, 120)
        val scores = mutableMapOf<Int, Int>()

        playerRankings.forEachIndexed { rank, playerId ->
            scores[playerId] = trexScores[rank]
        }

        val rankingDetails = playerRankings.mapIndexed { rank, playerId ->
            "${rank + 1}. ${_gameState.value.players[playerId].name}"
        }.joinToString(", ")

        return addContractResult(
            ContractType.JACK,
            scores,
            "ترتيب: $rankingDetails"
        )
    }

    private fun addContractResult(
        contractType: ContractType,
        scores: Map<Int, Int>,
        details: String
    ): Boolean {
        val currentState = _gameState.value

        val contractResult = ContractResult(
            contractType = contractType,
            playerScores = scores,
            activatingPlayerId = currentState.currentPlayerIndex,
            timestamp = System.currentTimeMillis(),
            details = details
        )

        // Update player scores
        val updatedPlayers = currentState.players.map { player ->
            val scoreChange = scores[player.id] ?: 0
            player.updateScore(scoreChange)
        }

        // Add contract to used contracts for current player
        val updatedUsedContracts = currentState.currentPlayerUsedContracts + contractType

        // Update game state
        _gameState.value = currentState.copy(
            players = updatedPlayers,
            contractHistory = currentState.contractHistory + contractResult,
            currentPlayerUsedContracts = updatedUsedContracts
        )

        return true
    }

    fun moveToNextPlayer() {
        val currentState = _gameState.value

        // Check if all contracts are used for current player
        if (currentState.allContractsUsedForCurrentPlayer) {
            val nextPlayerIndex = (currentState.currentPlayerIndex + 1) % currentState.players.size

            val newRound = currentState.currentRound + 1

            _gameState.value = currentState.copy(
                currentPlayerIndex = nextPlayerIndex,
                currentRound = newRound,
                currentPlayerUsedContracts = emptySet() // Reset for next player
            )
        }
    }

    fun endGame() {
        _gameState.value = GameState()
        _uiState.value = _uiState.value.copy(
            showEndGameDialog = false
        )
    }

    fun undoLastContract(): Boolean {
        val currentState = _gameState.value
        if (currentState.contractHistory.isEmpty()) return false

        val lastContract = currentState.contractHistory.last()

        // Reverse the score changes
        val updatedPlayers = currentState.players.map { player ->
            val scoreChange = lastContract.playerScores[player.id] ?: 0
            player.updateScore(-scoreChange)
        }

        // Remove last contract from history
        val updatedHistory = currentState.contractHistory.dropLast(1)

        // Remove last used contract
        val usedContracts = currentState.currentPlayerUsedContracts - lastContract.contractType

        _gameState.value = currentState.copy(
            players = updatedPlayers,
            contractHistory = updatedHistory,
            currentPlayerUsedContracts = usedContracts
        )

        return true
    }

    fun resetGame() {
        _gameState.value = GameState()
    }

    fun showContractDialog(contractType: ContractType) {
        _uiState.value = _uiState.value.copy(
            showContractDialog = true,
            selectedContract = contractType
        )
    }

    fun showEndGameDialog() {
        _uiState.value = _uiState.value.copy(
            showEndGameDialog = true
        )
    }

    fun hideContractDialog() {
        _uiState.value = _uiState.value.copy(
            showContractDialog = false,
            selectedContract = null
        )
    }

    // Validation functions
    private fun validateQueensInput(queens: List<Int>): Boolean {
        return queens.size == 4 &&
                queens.all { it in 0..4 } &&
                queens.sum() == 4
    }

    private fun validateDiamondsInput(diamonds: List<Int>): Boolean {
        return diamonds.size == 4 &&
                diamonds.all { it in 0..13 } &&
                diamonds.sum() == 13
    }

    private fun validateTricksInput(tricks: List<Int>): Boolean {
        return tricks.size == 4 &&
                tricks.all { it in 0..13 } &&
                tricks.sum() == 13
    }

    private fun validateJackRankings(rankings: List<Int>): Boolean {
        return rankings.size == 4 &&
                rankings.toSet().size == 4 &&
                rankings.all { it in 0..3 }
    }
}