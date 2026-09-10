package com.attenomy.janken.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attenomy.janken.data.Move
import com.attenomy.janken.data.RoundWinner
import com.attenomy.janken.ui.components.ConfettiEffect
import com.attenomy.janken.ui.components.MoveButton
import com.attenomy.janken.ui.components.RulesDialog
import com.attenomy.janken.ui.components.ScoreBoard
import com.attenomy.janken.ui.theme.AccentCyan
import com.attenomy.janken.ui.theme.AccentEmerald
import com.attenomy.janken.ui.theme.AccentRose
import com.attenomy.janken.ui.theme.PrimaryIndigo
import com.attenomy.janken.ui.viewmodel.GameState
import com.attenomy.janken.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotGameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    var showRulesDialog by remember { mutableStateOf(false) }

    if (showRulesDialog) {
        RulesDialog(onDismiss = { showRulesDialog = false })
    }

    val availableMoves = Move.forVariant(viewModel.variant)
    val isMatchWon = viewModel.gameState == GameState.MATCH_OVER && viewModel.matchWinnerName == viewModel.player1Name

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "vs Computer (${viewModel.botDifficulty.title})",
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
                            p2 = viewModel.player2Name,
                            difficulty = viewModel.botDifficulty
                        )
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart Match")
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

                    // Arena Display
                    when (viewModel.gameState) {
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
                                        p2 = viewModel.player2Name,
                                        difficulty = viewModel.botDifficulty
                                    )
                                },
                                onExit = onNavigateBack
                            )
                        }

                        else -> {
                            // Move Selection Area
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
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Make Your Move!",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tap to throw your hand",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(22.dp))

                                    // Move Buttons Grid / Row
                                    if (availableMoves.size <= 3) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            availableMoves.forEach { move ->
                                                MoveButton(
                                                    move = move,
                                                    size = 96.dp,
                                                    onClick = { viewModel.onPlayer1Choose(move) }
                                                )
                                            }
                                        }
                                    } else {
                                        // 5 moves for RPSLS
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            availableMoves.take(3).forEach { move ->
                                                MoveButton(
                                                    move = move,
                                                    size = 84.dp,
                                                    onClick = { viewModel.onPlayer1Choose(move) }
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            availableMoves.drop(3).forEach { move ->
                                                MoveButton(
                                                    move = move,
                                                    size = 84.dp,
                                                    modifier = Modifier.padding(horizontal = 10.dp),
                                                    onClick = { viewModel.onPlayer1Choose(move) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Confetti victory explosion
            if (isMatchWon) {
                ConfettiEffect()
            }
        }
    }
}

@Composable
fun CountdownArena(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdown_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(PrimaryIndigo.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            color = PrimaryIndigo,
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
fun ResultArena(
    p1Name: String,
    p2Name: String,
    p1Move: Move?,
    p2Move: Move?,
    result: com.attenomy.janken.data.RoundResult?,
    isMatchOver: Boolean,
    matchWinner: String?,
    onNextRound: () -> Unit,
    onPlayAgain: () -> Unit,
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
            // Dual Moves Reveal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // P1 Move
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = p1Name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = p1Move?.emoji ?: "❓", fontSize = 42.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = p1Move?.title ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                }

                Text(
                    text = "VS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.outline
                )

                // P2 Move
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = p2Name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AccentEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = p2Move?.emoji ?: "❓", fontSize = 42.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = p2Move?.title ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = AccentEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Outcome Banner
            val outcomeColor = when (result?.winner) {
                RoundWinner.PLAYER_1 -> AccentEmerald
                RoundWinner.PLAYER_2 -> AccentRose
                RoundWinner.DRAW -> AccentCyan
                null -> MaterialTheme.colorScheme.primary
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(outcomeColor.copy(alpha = 0.15f))
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isMatchOver) {
                            if (matchWinner != null) "🏆 $matchWinner Wins the Match!" else "Match Draw!"
                        } else {
                            when (result?.winner) {
                                RoundWinner.PLAYER_1 -> "🎉 $p1Name Wins Round!"
                                RoundWinner.PLAYER_2 -> "⚡ $p2Name Wins Round!"
                                RoundWinner.DRAW -> "🤝 Round Draw!"
                                null -> ""
                            }
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = outcomeColor,
                        textAlign = TextAlign.Center
                    )
                    if (!result?.reason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result!!.reason,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Action Buttons
            if (isMatchOver) {
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Play Again", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Main Menu")
                }
            } else {
                Button(
                    onClick = onNextRound,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Next Round ➜", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
