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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attenomy.janken.data.GameMode
import com.attenomy.janken.data.MatchRecord
import com.attenomy.janken.data.Move
import com.attenomy.janken.ui.components.getColor
import com.attenomy.janken.ui.theme.AccentCyan
import com.attenomy.janken.ui.theme.AccentEmerald
import com.attenomy.janken.ui.theme.AccentRose
import com.attenomy.janken.ui.theme.PrimaryIndigo
import com.attenomy.janken.ui.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onNavigateBack: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val history by viewModel.history.collectAsState()
    var selectedFilter by remember { mutableStateOf<GameMode?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Statistics & History?") },
            text = { Text("Are you sure you want to permanently delete all match logs and statistics?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistics & History",
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
                    if (stats.totalMatches > 0 || history.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear All")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        val filteredHistory = if (selectedFilter == null) history else history.filter { it.mode == selectedFilter }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats Overview Cards
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Win Rate",
                        value = "${String.format(Locale.getDefault(), "%.1f", stats.winRate)}%",
                        color = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Matches",
                        value = stats.totalMatches.toString(),
                        color = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Best Streak",
                        value = "🔥 ${stats.bestStreak}",
                        color = AccentRose,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Move Distribution
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Move Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val totalMoves = stats.rockCount + stats.paperCount + stats.scissorsCount + stats.lizardCount + stats.spockCount
                        val safeTotal = if (totalMoves == 0) 1 else totalMoves

                        Move.entries.forEach { move ->
                            val count = stats.getCountForMove(move)
                            if (count > 0 || totalMoves == 0) {
                                val percentage = (count.toFloat() / safeTotal) * 100f
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = move.emoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = move.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.width(65.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { percentage / 100f },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        color = move.getColor(),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${percentage.toInt()}% ($count)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(60.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // History Filter Header
            item {
                Text(
                    text = "Match History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All (${history.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == GameMode.BOT,
                        onClick = { selectedFilter = GameMode.BOT },
                        label = { Text("vs Bot") }
                    )
                    FilterChip(
                        selected = selectedFilter == GameMode.PASS_AND_PLAY,
                        onClick = { selectedFilter = GameMode.PASS_AND_PLAY },
                        label = { Text("Pass & Play") }
                    )
                    FilterChip(
                        selected = selectedFilter == GameMode.QUICK_DECISION,
                        onClick = { selectedFilter = GameMode.QUICK_DECISION },
                        label = { Text("Decisions") }
                    )
                }
            }

            if (filteredHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matches recorded yet.\nPlay a game to see match history!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredHistory, key = { it.id }) { match ->
                    MatchHistoryCard(match = match)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
private fun MatchHistoryCard(match: MatchRecord) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(match.timestamp) { dateFormat.format(Date(match.timestamp)) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = match.mode.icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.mode.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!match.decisionPrompt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "“${match.decisionPrompt}”",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = AccentRose
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${match.player1Name} vs ${match.player2Name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${match.player1Score} - ${match.player2Score}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PrimaryIndigo
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (match.winnerName != null) "Winner: ${match.winnerName}" else "Result: Draw",
                style = MaterialTheme.typography.bodySmall,
                color = if (match.winnerName != null) AccentEmerald else AccentCyan,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
