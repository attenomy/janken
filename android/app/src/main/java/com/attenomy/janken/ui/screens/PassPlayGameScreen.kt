package com.attenomy.janken.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attenomy.janken.data.Move
import com.attenomy.janken.ui.components.ConfettiEffect
import com.attenomy.janken.ui.components.MoveButton
import com.attenomy.janken.ui.components.RulesDialog
import com.attenomy.janken.ui.components.ScoreBoard
import com.attenomy.janken.ui.theme.AccentEmerald
import com.attenomy.janken.ui.theme.AccentRose
import com.attenomy.janken.ui.theme.PrimaryIndigo
import com.attenomy.janken.ui.viewmodel.GameState
import com.attenomy.janken.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassPlayGameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    var showRulesDialog by remember { mutableStateOf(false) }

    if (showRulesDialog) {
        RulesDialog(onDismiss = { showRulesDialog = false })
    }

    val availableMoves = Move.forVariant(viewModel.variant)
    val isMatchWon = viewModel.gameState == GameState.MATCH_OVER && viewModel.matchWinnerName != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pass & Play (2 Players)",
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
                    IconButton(onClick = { showRulesDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Rules")
                    }
                    IconButton(onClick = {
                        viewModel.startNewGame(
                            newMode = viewModel.mode,
                            newVariant = viewModel.variant,
                            newFormat = viewModel.matchFormat,
                            p1 = viewModel.player1Name,
                            p2 = viewModel.player2Name
                        )
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart")
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
                    // Scoreboard
                    ScoreBoard(
                        p1Name = viewModel.player1Name,
                        p2Name = viewModel.player2Name,
                        p1Score = viewModel.player1Score,
                        p2Score = viewModel.player2Score,
                        targetScore = viewModel.matchFormat.targetScore,
                        roundNumber = viewModel.currentRound
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pass & Play Phases
                    when (viewModel.gameState) {
                        GameState.P1_CHOOSING -> {
                            // Player 1 Move Selection
                            PassPlaySelectionCard(
                                playerName = viewModel.player1Name,
                                playerColor = PrimaryIndigo,
                                moves = availableMoves,
                                onChoose = { viewModel.onPlayer1Choose(it) }
                            )
                        }

                        GameState.WAITING_PASS_PHONE -> {
                            // Privacy Intermediary Screen
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
                                            contentDescription = "Move Locked",
                                            tint = PrimaryIndigo,
                                            modifier = Modifier.size(38.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Text(
                                        text = "Move Locked & Hidden! 🔒",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = PrimaryIndigo
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Pass the phone to ${viewModel.player2Name}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = { viewModel.onPassConfirmedByPlayer2() },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                                    ) {
                                        Text(
                                            text = "I am ${viewModel.player2Name} ➜",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        GameState.P2_CHOOSING -> {
                            // Player 2 Move Selection
                            PassPlaySelectionCard(
                                playerName = viewModel.player2Name,
                                playerColor = AccentEmerald,
                                moves = availableMoves,
                                onChoose = { viewModel.onPlayer2Choose(it) }
                            )
                        }

                        GameState.ANIMATING_COUNTDOWN -> {
                            CountdownArena(text = viewModel.countdownText)
                        }

                        GameState.ROUND_RESOLVED, GameState.MATCH_OVER -> {
                            ResultArena(
                                p1Name = viewModel.player1Name,
                                p2Name = viewModel.player2Name,
                                p1Move = viewModel.p1SelectedMove,
                                p2Move = viewModel.p2SelectedMove,
                                result = viewModel.latestRoundResult,
                                isMatchOver = viewModel.gameState == GameState.MATCH_OVER,
                                matchWinner = viewModel.matchWinnerName,
                                onNextRound = { viewModel.nextRound() },
                                onPlayAgain = {
                                    viewModel.startNewGame(
                                        newMode = viewModel.mode,
                                        newVariant = viewModel.variant,
                                        newFormat = viewModel.matchFormat,
                                        p1 = viewModel.player1Name,
                                        p2 = viewModel.player2Name
                                    )
                                },
                                onExit = onNavigateBack
                            )
                        }
                    }
                }
            }

            if (isMatchWon) {
                ConfettiEffect()
            }
        }
    }
}

@Composable
private fun PassPlaySelectionCard(
    playerName: String,
    playerColor: Color,
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
                text = "$playerName's Turn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = playerColor
            )
            Text(
                text = "Pick your move secretly",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (moves.size <= 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    moves.forEach { move ->
                        MoveButton(
                            move = move,
                            size = 96.dp,
                            onClick = { onChoose(move) }
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    moves.take(3).forEach { move ->
                        MoveButton(
                            move = move,
                            size = 84.dp,
                            onClick = { onChoose(move) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    moves.drop(3).forEach { move ->
                        MoveButton(
                            move = move,
                            size = 84.dp,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            onClick = { onChoose(move) }
                        )
                    }
                }
            }
        }
    }
}
