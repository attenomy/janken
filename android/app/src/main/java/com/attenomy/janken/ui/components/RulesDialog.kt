package com.attenomy.janken.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attenomy.janken.data.Move

@Composable
fun RulesDialog(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Game Rules & Guides",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Classic RPS") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("RPSLS (5 Moves)") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Classic Rules
                    Text(
                        text = "Classic Rock Paper Scissors",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The timeless hand game of strategy and intuition:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    RuleItem(winner = Move.ROCK, loser = Move.SCISSORS, reason = "Rock crushes Scissors")
                    RuleItem(winner = Move.SCISSORS, loser = Move.PAPER, reason = "Scissors cuts Paper")
                    RuleItem(winner = Move.PAPER, loser = Move.ROCK, reason = "Paper covers Rock")
                } else {
                    // RPSLS Rules
                    Text(
                        text = "Rock Paper Scissors Lizard Spock",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Created by Sam Kass and Karen Bryla, popularized by The Big Bang Theory. Reduces the probability of ties!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    RuleItem(winner = Move.SCISSORS, loser = Move.PAPER, reason = "Scissors cuts Paper")
                    RuleItem(winner = Move.PAPER, loser = Move.ROCK, reason = "Paper covers Rock")
                    RuleItem(winner = Move.ROCK, loser = Move.LIZARD, reason = "Rock crushes Lizard")
                    RuleItem(winner = Move.LIZARD, loser = Move.SPOCK, reason = "Lizard poisons Spock")
                    RuleItem(winner = Move.SPOCK, loser = Move.SCISSORS, reason = "Spock smashes Scissors")
                    RuleItem(winner = Move.SCISSORS, loser = Move.LIZARD, reason = "Scissors decapitates Lizard")
                    RuleItem(winner = Move.LIZARD, loser = Move.PAPER, reason = "Lizard eats Paper")
                    RuleItem(winner = Move.PAPER, loser = Move.SPOCK, reason = "Paper disproves Spock")
                    RuleItem(winner = Move.SPOCK, loser = Move.ROCK, reason = "Spock vaporizes Rock")
                    RuleItem(winner = Move.ROCK, loser = Move.SCISSORS, reason = "Rock crushes Scissors")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got It!", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun RuleItem(
    winner: Move,
    loser: Move,
    reason: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = winner.emoji, fontSize = 20.sp)
            Text(text = "➜", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = loser.emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = reason,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
