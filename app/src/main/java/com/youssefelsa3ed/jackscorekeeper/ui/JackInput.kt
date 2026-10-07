package com.youssefelsa3ed.jackscorekeeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.youssefelsa3ed.jackscorekeeper.data.ContractType
import com.youssefelsa3ed.jackscorekeeper.data.Player

@Composable
fun JackInput(
    players: List<Player>,
    onSubmit: (ContractType, Any) -> Unit,
    onDismiss: () -> Unit
) {
    var playerRankings by remember { mutableStateOf(players.map { it.id }) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "رتب اللاعبين من الأول إلى الرابع",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        val trexScores = listOf(480, 360, 240, 120)

        LazyColumn(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(playerRankings) { rank, playerId ->
                val player = players.find { it.id == playerId }
                if (player != null) {
                    JackRankingItem(
                        player = player,
                        rank = rank + 1,
                        score = trexScores[rank],
                        onMoveUp = if (rank > 0) {
                            {
                                playerRankings = playerRankings.toMutableList().apply {
                                    val temp = this[rank]
                                    this[rank] = this[rank - 1]
                                    this[rank - 1] = temp
                                }
                            }
                        } else null,
                        onMoveDown = if (rank < playerRankings.size - 1) {
                            {
                                playerRankings = playerRankings.toMutableList().apply {
                                    val temp = this[rank]
                                    this[rank] = this[rank + 1]
                                    this[rank + 1] = temp
                                }
                            }
                        } else null
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("إلغاء")
            }

            Button(
                onClick = {
                    onSubmit(ContractType.JACK, playerRankings)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("تأكيد")
            }
        }
    }
}

@Composable
fun JackRankingItem(
    player: Player,
    rank: Int,
    score: Int,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (rank) {
                1 -> Color(0xFFFFD700).copy(alpha = 0.2f) // Gold
                2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f) // Silver
                3 -> Color(0xFFCD7F32).copy(alpha = 0.2f) // Bronze
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$rank.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (rank) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "+$score نقطة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column {
                IconButton(
                    onClick = onMoveUp ?: {},
                    enabled = onMoveUp != null
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "تحريك لأعلى",
                        tint = if (onMoveUp != null)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                IconButton(
                    onClick = onMoveDown ?: {},
                    enabled = onMoveDown != null
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "تحريك لأسفل",
                        tint = if (onMoveDown != null)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}