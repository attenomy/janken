package com.attenomy.janken

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.attenomy.janken.data.BotDifficulty
import com.attenomy.janken.data.GameMode
import com.attenomy.janken.data.GameRepository
import com.attenomy.janken.data.GameVariant
import com.attenomy.janken.data.MatchFormat
import com.attenomy.janken.sound.SoundManager
import com.attenomy.janken.ui.screens.BotGameScreen
import com.attenomy.janken.ui.screens.HomeScreen
import com.attenomy.janken.ui.screens.PassPlayGameScreen
import com.attenomy.janken.ui.screens.QuickDecisionScreen
import com.attenomy.janken.ui.screens.SettingsScreen
import com.attenomy.janken.ui.screens.StatsScreen
import com.attenomy.janken.ui.theme.JankenTheme
import com.attenomy.janken.ui.viewmodel.GameViewModel
import com.attenomy.janken.ui.viewmodel.StatsViewModel

@Composable
fun JankenApp() {
    val context = LocalContext.current
    val repository = remember { GameRepository(context) }
    val soundManager = remember { SoundManager() }
    val gameViewModel = remember { GameViewModel(repository, soundManager) }
    val statsViewModel = remember { StatsViewModel(repository) }

    var themePreference by remember { mutableStateOf(repository.themeMode) }

    JankenTheme(themePreference = themePreference) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "home",
            enterTransition = { fadeIn(animationSpec = tween(250)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(250)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(250)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(250)) }
        ) {
            composable("home") {
                HomeScreen(
                    onStartBotGame = { variant, format, diff ->
                        gameViewModel.startNewGame(
                            newMode = GameMode.BOT,
                            newVariant = variant,
                            newFormat = format,
                            p1 = "Player",
                            p2 = "Bot",
                            difficulty = diff
                        )
                        navController.navigate("bot_game")
                    },
                    onStartPassPlayGame = { variant, format, p1, p2 ->
                        gameViewModel.startNewGame(
                            newMode = GameMode.PASS_AND_PLAY,
                            newVariant = variant,
                            newFormat = format,
                            p1 = p1,
                            p2 = p2
                        )
                        navController.navigate("pass_play_game")
                    },
                    onStartQuickDecision = { prompt, variant ->
                        gameViewModel.startNewGame(
                            newMode = GameMode.QUICK_DECISION,
                            newVariant = variant,
                            newFormat = MatchFormat.BEST_OF_1,
                            p1 = "Player 1",
                            p2 = "Player 2",
                            prompt = prompt
                        )
                        navController.navigate("quick_decision")
                    },
                    onNavigateStats = { navController.navigate("stats") },
                    onNavigateSettings = { navController.navigate("settings") }
                )
            }

            composable("bot_game") {
                BotGameScreen(
                    viewModel = gameViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("pass_play_game") {
                PassPlayGameScreen(
                    viewModel = gameViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("quick_decision") {
                QuickDecisionScreen(
                    viewModel = gameViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("stats") {
                StatsScreen(
                    viewModel = statsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    repository = repository,
                    soundManager = soundManager,
                    onThemeChanged = { themePreference = it },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
