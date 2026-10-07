package com.youssefelsa3ed.jackscorekeeper.data

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    var totalScore: Int = 0
) {
    fun updateScore(points: Int): Player = copy(totalScore = totalScore + points)
}

@Serializable
data class ContractResult(
    val contractType: ContractType,
    val playerScores: Map<Int, Int>,
    val activatingPlayerId: Int,
    val timestamp: Long,
    val details: String = ""
)

@Serializable
enum class ContractType(val displayNameAr: String) {
    NO_DAME("نو دايم"),
    NO_KING_HEART("نو روا"),
    NO_DIAMONDS("نو كارو"),
    ESTIMATION("إستيميشن"),
    JACK("جاكس");

    val isNegative: Boolean
        get() = this != ESTIMATION && this != JACK
}
data class GameState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val contractHistory: List<ContractResult> = emptyList(),
    val isGameStarted: Boolean = false,
    val currentRound: Int = 1,
    val totalRounds: Int = 4,
    val currentPlayerUsedContracts: Set<ContractType> = emptySet(),
    val isDynamicColorEnabled: Boolean = false
) {
    val currentPlayer: Player?
        get() = if (players.isNotEmpty() && currentPlayerIndex < players.size)
            players[currentPlayerIndex] else null

    val allContractsUsedForCurrentPlayer: Boolean
        get() = currentPlayerUsedContracts.size == ContractType.entries.size

    val isAllRoundsCompleted: Boolean
        get() = currentRound == totalRounds && allContractsUsedForCurrentPlayer

    fun getPlayerRanking(): List<Player> = players.sortedByDescending { it.totalScore }
}