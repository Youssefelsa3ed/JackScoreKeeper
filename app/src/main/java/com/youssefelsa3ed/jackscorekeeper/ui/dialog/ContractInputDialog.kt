package com.youssefelsa3ed.jackscorekeeper.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.youssefelsa3ed.jackscorekeeper.data.ContractType
import com.youssefelsa3ed.jackscorekeeper.data.Player
import com.youssefelsa3ed.jackscorekeeper.ui.JackInput

@Composable
fun ContractInputDialog(
    contractType: ContractType,
    players: List<Player>,
    onDismiss: () -> Unit,
    onSubmit: (ContractType, Any) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = contractType.displayNameAr,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (contractType) {
                    ContractType.NO_DAME -> NoDameInput(players, onSubmit, onDismiss)
                    ContractType.NO_KING_HEART -> NoKingHeartInput(players, onSubmit, onDismiss)
                    ContractType.NO_DIAMONDS -> NoDiamondsInput(players, onSubmit, onDismiss)
                    ContractType.ESTIMATION -> EstimationInput(players, onSubmit, onDismiss)
                    ContractType.JACK -> JackInput(players, onSubmit, onDismiss)
                }
            }
        }
    }
}

@Composable
fun NoDameInput(
    players: List<Player>,
    onSubmit: (ContractType, Any) -> Unit,
    onDismiss: () -> Unit
) {
    var queensCounts by remember { mutableStateOf(List(4) { 0 }) }
    var showError by remember { mutableStateOf(false) }
    val totalQueens = queensCounts.sum()

    Column {
        Text(
            text = "أدخل عدد البنات لكل لاعب (المجموع يجب أن يكون 4)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        players.forEachIndexed { index, player ->
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
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = (queensCounts[index].takeIf { it != 0 } ?: "").toString(),
                    onValueChange = { value ->
                        val newValue = if (value.isEmpty()) 0 else value.toIntOrNull()
                        if (newValue != null && newValue in 0..4) {
                            queensCounts = queensCounts.toMutableList().apply {
                                this[index] = newValue
                            }
                            showError = false
                        }
                    },
                    modifier = Modifier.width(80.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = showError
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "المجموع: $totalQueens/4",
            style = MaterialTheme.typography.bodySmall,
            color = if (totalQueens == 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (showError) {
            Text(
                text = "المجموع يجب أن يكون 4 بالضبط",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
                    if (totalQueens == 4) {
                        onSubmit(ContractType.NO_DAME, queensCounts)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = totalQueens == 4
            ) {
                Text("تأكيد")
            }
        }
    }
}

@Composable
fun NoKingHeartInput(
    players: List<Player>,
    onSubmit: (ContractType, Any) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPlayer by remember { mutableStateOf<Int?>(null) }

    Column {
        Text(
            text = "اختر اللاعب الذي أخذ شايب القلب",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        players.forEachIndexed { index, player ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedPlayer == index,
                        onClick = { selectedPlayer = index },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPlayer == index,
                    onClick = { selectedPlayer = index }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyLarge
                )
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
                    selectedPlayer?.let { playerIndex ->
                        onSubmit(ContractType.NO_KING_HEART, playerIndex)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = selectedPlayer != null
            ) {
                Text("تأكيد")
            }
        }
    }
}

@Composable
fun NoDiamondsInput(
    players: List<Player>,
    onSubmit: (ContractType, Any) -> Unit,
    onDismiss: () -> Unit
) {
    var diamondsCounts by remember { mutableStateOf(List(4) { 0 }) }
    var showError by remember { mutableStateOf(false) }
    val totalDiamonds = diamondsCounts.sum()

    Column {
        Text(
            text = "أدخل عدد ورقات الكارو لكل لاعب (المجموع يجب أن يكون 13)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        players.forEachIndexed { index, player ->
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
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = (diamondsCounts[index].takeIf { it != 0 } ?: "").toString(),
                    onValueChange = { value ->
                        val newValue = if (value.isEmpty()) 0 else value.toIntOrNull()
                        if (newValue != null && newValue in 0..13) {
                            diamondsCounts = diamondsCounts.toMutableList().apply {
                                this[index] = newValue
                            }
                            showError = false
                        }
                    },
                    modifier = Modifier.width(80.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = showError
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "المجموع: $totalDiamonds/13",
            style = MaterialTheme.typography.bodySmall,
            color = if (totalDiamonds == 13) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (showError) {
            Text(
                text = "المجموع يجب أن يكون 13 بالضبط",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
                    if (totalDiamonds == 13) {
                        onSubmit(ContractType.NO_DIAMONDS, diamondsCounts)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = totalDiamonds == 13
            ) {
                Text("تأكيد")
            }
        }
    }
}

@Composable
fun EstimationInput(
    players: List<Player>,
    onSubmit: (ContractType, Any) -> Unit,
    onDismiss: () -> Unit
) {
    var tricksCounts by remember { mutableStateOf(List(4) { 0 }) }
    var showError by remember { mutableStateOf(false) }
    val totalTricks = tricksCounts.sum()

    Column {
        Text(
            text = "أدخل عدد اللمات التي فاز بها كل لاعب (المجموع يجب أن يكون 13)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        players.forEachIndexed { index, player ->
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
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = (tricksCounts[index].takeIf { it != 0 } ?: "").toString(),
                    onValueChange = { value ->
                        val newValue = if (value.isEmpty()) 0 else value.toIntOrNull()
                        if (newValue != null && newValue in 0..13) {
                            tricksCounts = tricksCounts.toMutableList().apply {
                                this[index] = newValue
                            }
                            showError = false
                        }
                    },
                    modifier = Modifier.width(80.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = showError
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "المجموع: $totalTricks/13",
            style = MaterialTheme.typography.bodySmall,
            color = if (totalTricks == 13) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (showError) {
            Text(
                text = "المجموع يجب أن يكون 13 بالضبط",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
                    if (totalTricks == 13) {
                        onSubmit(ContractType.ESTIMATION, tricksCounts)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = totalTricks == 13
            ) {
                Text("تأكيد")
            }
        }
    }
}