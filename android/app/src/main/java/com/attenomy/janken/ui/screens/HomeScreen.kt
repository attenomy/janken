package com.attenomy.janken.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attenomy.janken.data.BotDifficulty
import com.attenomy.janken.data.GameMode
import com.attenomy.janken.data.GameVariant
import com.attenomy.janken.data.MatchFormat
import com.attenomy.janken.ui.components.RulesDialog
import com.attenomy.janken.ui.theme.AccentCyan
import com.attenomy.janken.ui.theme.AccentEmerald
import com.attenomy.janken.ui.theme.AccentPurple
import com.attenomy.janken.ui.theme.AccentRose
import com.attenomy.janken.ui.theme.PrimaryIndigo
import com.attenomy.janken.ui.theme.PrimaryIndigoLight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onStartBotGame: (GameVariant, MatchFormat, BotDifficulty) -> Unit,
    onStartPassPlayGame: (GameVariant, MatchFormat, String, String) -> Unit,
    onStartQuickDecision: (String, GameVariant) -> Unit,
    onNavigateStats: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    var selectedVariant by remember { mutableStateOf(GameVariant.CLASSIC) }
    var expandedMode by remember { mutableStateOf<GameMode?>(null) }
    var showRulesDialog by remember { mutableStateOf(false) }

    // Bot Config
    var botDifficulty by remember { mutableStateOf(BotDifficulty.MEDIUM) }
    var botMatchFormat by remember { mutableStateOf(MatchFormat.BEST_OF_3) }

    // Pass & Play Config
    var p1Name by remember { mutableStateOf("Player 1") }
    var p2Name by remember { mutableStateOf("Player 2") }
    var passPlayFormat by remember { mutableStateOf(MatchFormat.BEST_OF_3) }

    // Quick Decision Config
    var customPrompt by remember { mutableStateOf("") }
    val promptPresets = listOf(
        "Who gets the last slice?",
        "Who pays for coffee?",
        "Who takes out the trash?",
        "Who picks the movie?",
        "Who washes the dishes?"
    )

    if (showRulesDialog) {
        RulesDialog(onDismiss = { showRulesDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JANKEN",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = PrimaryIndigo
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "✊✋✌️",
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showRulesDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Rules & Guides")
                    }
                    IconButton(onClick = onNavigateStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistics")
                    }
                    IconButton(onClick = onNavigateSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Variant Selector Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Game Rules Variant",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedVariant == GameVariant.CLASSIC,
                            onClick = { selectedVariant = GameVariant.CLASSIC },
                            label = { Text("Classic (3 Moves)") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryIndigo,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedVariant == GameVariant.RPSLS,
                            onClick = { selectedVariant = GameVariant.RPSLS },
                            label = { Text("RPSLS (5 Moves) 🦎🖖") },
                            modifier = Modifier.weight(1.2f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode 1: VS COMPUTER (BOT)
            ModeCard(
                title = "vs Computer",
                subtitle = "Challenge smart bot AI with strategy prediction",
                emoji = "🤖",
                accentColor = PrimaryIndigo,
                isExpanded = expandedMode == GameMode.BOT,
                onToggleExpand = {
                    expandedMode = if (expandedMode == GameMode.BOT) null else GameMode.BOT
                }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Bot Difficulty",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BotDifficulty.entries.forEach { diff ->
                            FilterChip(
                                selected = botDifficulty == diff,
                                onClick = { botDifficulty = diff },
                                label = { Text(diff.title) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Match Format",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(MatchFormat.BEST_OF_1, MatchFormat.BEST_OF_3, MatchFormat.BEST_OF_5, MatchFormat.ENDLESS).forEach { fmt ->
                            FilterChip(
                                selected = botMatchFormat == fmt,
                                onClick = { botMatchFormat = fmt },
                                label = { Text(fmt.shortTitle) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onStartBotGame(selectedVariant, botMatchFormat, botDifficulty) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start vs Computer", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mode 2: PASS & PLAY (LOCAL 2-PLAYER)
            ModeCard(
                title = "Pass & Play",
                subtitle = "Local 2-Player with private blind turns on 1 phone",
                emoji = "👥",
                accentColor = AccentEmerald,
                isExpanded = expandedMode == GameMode.PASS_AND_PLAY,
                onToggleExpand = {
                    expandedMode = if (expandedMode == GameMode.PASS_AND_PLAY) null else GameMode.PASS_AND_PLAY
                }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = p1Name,
                            onValueChange = { p1Name = it },
                            label = { Text("Player 1") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = p2Name,
                            onValueChange = { p2Name = it },
                            label = { Text("Player 2") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Match Format",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(MatchFormat.BEST_OF_1, MatchFormat.BEST_OF_3, MatchFormat.BEST_OF_5, MatchFormat.FIRST_TO_5).forEach { fmt ->
                            FilterChip(
                                selected = passPlayFormat == fmt,
                                onClick = { passPlayFormat = fmt },
                                label = { Text(fmt.shortTitle) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onStartPassPlayGame(selectedVariant, passPlayFormat, p1Name, p2Name) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Pass & Play", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mode 3: QUICK DECISION
            ModeCard(
                title = "Quick Decision",
                subtitle = "Settle real-world debates & coin tosses instantly",
                emoji = "⚡",
                accentColor = AccentRose,
                isExpanded = expandedMode == GameMode.QUICK_DECISION,
                onToggleExpand = {
                    expandedMode = if (expandedMode == GameMode.QUICK_DECISION) null else GameMode.QUICK_DECISION
                }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Select or Enter Question",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        promptPresets.forEach { preset ->
                            FilterChip(
                                selected = customPrompt == preset,
                                onClick = { customPrompt = preset },
                                label = { Text(preset, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customPrompt,
                        onValueChange = { customPrompt = it },
                        placeholder = { Text("e.g. Who pays for dinner tonight?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val prompt = customPrompt.ifBlank { "Who wins the decision?" }
                            onStartQuickDecision(prompt, selectedVariant)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settle It!", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy & Offline Footer Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "🔒 100% Offline • Zero Permissions • Open Source",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    emoji: String,
    accentColor: Color,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}
