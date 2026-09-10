package com.attenomy.janken.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attenomy.janken.data.Move
import com.attenomy.janken.data.RoundWinner
import com.attenomy.janken.ui.components.ConfettiEffect
import com.attenomy.janken.ui.components.MoveButton
import com.attenomy.janken.ui.theme.AccentCyan
import com.attenomy.janken.ui.theme.AccentEmerald
import com.attenomy.janken.ui.theme.AccentRose
import com.attenomy.janken.ui.theme.PrimaryIndigo
import com.attenomy.janken.ui.viewmodel.GameState
import com.attenomy.janken.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickDecisionScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val availableMoves = Move.forVariant(viewModel.variant)
    val prompt = viewModel.decisionPrompt ?: "Who wins the decision?"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quick Decision Showdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.startNewGame(
                            newMode = viewModel.mode,
                            newVariant = viewModel.variant,
                            newFormat = viewModel.matchFormat,
                            p1 = viewModel.player1Name,
                            p2 = viewModel.player2Name,
                            prompt = prompt
                        )
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rematch")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Decision Topic Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AccentRose.copy(alpha = 0.12f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "DECISION QUESTION",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentRose,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "“$prompt”",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    when (viewModel.gameState) {
                        GameState.P1_CHOOSING -> {
                            DecisionMoveSelection(
                                player = viewModel.player1Name,
                                color = PrimaryIndigo,
                                moves = availableMoves,
                                onChoose = { viewModel.onPlayer1Choose(it) }
                            )
                        }

                        GameState.WAITING_PASS_PHONE -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(26.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(76.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryIndigo.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = "Hidden",
                                            tint = PrimaryIndigo,
                                            modifier = Modifier.size(38.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Text(
                                        text = "Hand Phone to ${viewModel.player2Name}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.height(22.dp))
                                    Button(
                                        onClick = { viewModel.onPassConfirmedByPlayer2() },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                                    ) {
                                        Text(
                                            text = "Ready to Choose ➜",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        GameState.P2_CHOOSING -> {
                            DecisionMoveSelection(
                                player = viewModel.player2Name,
                                color = AccentEmerald,
                                moves = availableMoves,
                                onChoose = { viewModel.onPlayer2Choose(it) }
                            )
                        }

                        GameState.ANIMATING_COUNTDOWN -> {
                            CountdownArena(text = viewModel.countdownText)
                        }

                        GameState.ROUND_RESOLVED, GameState.MATCH_OVER -> {
                            DecisionOutcomeCard(
                                prompt = prompt,
                                p1Name = viewModel.player1Name,
                                p2Name = viewModel.player2Name,
                                p1Move = viewModel.p1SelectedMove,
                                p2Move = viewModel.p2SelectedMove,
                                winner = viewModel.latestRoundResult?.winner,
                                reason = viewModel.latestRoundResult?.reason,
                                onSettleAgain = {
                                    viewModel.startNewGame(
                                        newMode = viewModel.mode,
                                        newVariant = viewModel.variant,
                                        newFormat = viewModel.matchFormat,
                                        p1 = viewModel.player1Name,
                                        p2 = viewModel.player2Name,
                                        prompt = prompt
                                    )
                                },
                                onExit = onNavigateBack
                            )
                        }
                    }
                }
            }

            if (viewModel.gameState == GameState.MATCH_OVER || viewModel.latestRoundResult?.winner != RoundWinner.DRAW) {
                if (viewModel.latestRoundResult?.winner != null && viewModel.latestRoundResult?.winner != RoundWinner.DRAW) {
                    ConfettiEffect()
                }
            }
        }
    }
}

@Composable
private fun DecisionMoveSelection(
    player: String,
    color: Color,
    moves: List<Move>,
    onChoose: (Move) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$player's Move",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moves.forEach { move ->
                    MoveButton(
                        move = move,
                        size = 92.dp,
                        onClick = { onChoose(move) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DecisionOutcomeCard(
    prompt: String,
    p1Name: String,
    p2Name: String,
    p1Move: Move?,
    p2Move: Move?,
    winner: RoundWinner?,
    reason: String?,
    onSettleAgain: () -> Unit,
    onExit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = p1Name, fontWeight = FontWeight.Bold)
                    Text(text = p1Move?.emoji ?: "❓", fontSize = 42.sp)
                    Text(text = p1Move?.title ?: "", color = PrimaryIndigo, fontWeight = FontWeight.Bold)
                }
                Text(text = "VS", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.outline)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = p2Name, fontWeight = FontWeight.Bold)
                    Text(text = p2Move?.emoji ?: "❓", fontSize = 42.sp)
                    Text(text = p2Move?.title ?: "", color = AccentEmerald, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val bannerColor = when (winner) {
                RoundWinner.PLAYER_1 -> PrimaryIndigo
                RoundWinner.PLAYER_2 -> AccentEmerald
                RoundWinner.DRAW -> AccentCyan
                null -> PrimaryIndigo
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(bannerColor.copy(alpha = 0.15f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (winner) {
                            RoundWinner.PLAYER_1 -> "🎉 $p1Name Decides!"
                            RoundWinner.PLAYER_2 -> "🎉 $p2Name Decides!"
                            RoundWinner.DRAW -> "🤝 Tie Breaker Needed!"
                            null -> ""
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = bannerColor,
                        textAlign = TextAlign.Center
                    )
                    if (!reason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onSettleAgain,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
            ) {
                Text("Decide Again / Rematch", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Back to Home")
            }
        }
    }
}
