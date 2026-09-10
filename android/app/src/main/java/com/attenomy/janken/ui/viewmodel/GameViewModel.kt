package com.attenomy.janken.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attenomy.janken.data.BotDifficulty
import com.attenomy.janken.data.BotEngine
import com.attenomy.janken.data.GameMode
import com.attenomy.janken.data.GameRepository
import com.attenomy.janken.data.GameVariant
import com.attenomy.janken.data.MatchFormat
import com.attenomy.janken.data.MatchRecord
import com.attenomy.janken.data.Move
import com.attenomy.janken.data.RoundResult
import com.attenomy.janken.data.RoundWinner
import com.attenomy.janken.sound.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

enum class GameState {
    P1_CHOOSING,
    WAITING_PASS_PHONE,
    P2_CHOOSING,
    ANIMATING_COUNTDOWN,
    ROUND_RESOLVED,
    MATCH_OVER
}

class GameViewModel(
    private val repository: GameRepository,
    val soundManager: SoundManager
) : ViewModel() {

    private val botEngine = BotEngine()

    // Game Setup
    var mode by mutableStateOf(GameMode.BOT)
        private set
    var variant by mutableStateOf(GameVariant.CLASSIC)
        private set
    var matchFormat by mutableStateOf(MatchFormat.BEST_OF_3)
        private set
    var botDifficulty by mutableStateOf(BotDifficulty.MEDIUM)
        private set

    var player1Name by mutableStateOf("Player 1")
        private set
    var player2Name by mutableStateOf("Bot")
        private set
    var decisionPrompt by mutableStateOf<String?>(null)
        private set

    // Game Runtime State
    var gameState by mutableStateOf(GameState.P1_CHOOSING)
        private set
    var currentRound by mutableIntStateOf(1)
        private set
    var player1Score by mutableIntStateOf(0)
        private set
    var player2Score by mutableIntStateOf(0)
        private set

    var p1SelectedMove by mutableStateOf<Move?>(null)
        private set
    var p2SelectedMove by mutableStateOf<Move?>(null)
        private set

    var latestRoundResult by mutableStateOf<RoundResult?>(null)
        private set
    val roundHistory = mutableListOf<RoundResult>()

    var matchWinnerName by mutableStateOf<String?>(null)
        private set
    var countdownText by mutableStateOf("")
        private set

    init {
        soundManager.isSoundEnabled = repository.isSoundEnabled
        botDifficulty = repository.defaultBotDifficulty
        variant = repository.defaultVariant
    }

    fun startNewGame(
        newMode: GameMode,
        newVariant: GameVariant = repository.defaultVariant,
        newFormat: MatchFormat = MatchFormat.BEST_OF_3,
        p1: String = "Player 1",
        p2: String = if (newMode == GameMode.BOT) "Bot" else "Player 2",
        difficulty: BotDifficulty = repository.defaultBotDifficulty,
        prompt: String? = null
    ) {
        mode = newMode
        variant = newVariant
        matchFormat = newFormat
        player1Name = p1.ifBlank { "Player 1" }
        player2Name = p2.ifBlank { if (newMode == GameMode.BOT) "Bot" else "Player 2" }
        botDifficulty = difficulty
        decisionPrompt = prompt

        botEngine.reset()
        currentRound = 1
        player1Score = 0
        player2Score = 0
        p1SelectedMove = null
        p2SelectedMove = null
        latestRoundResult = null
        roundHistory.clear()
        matchWinnerName = null
        gameState = GameState.P1_CHOOSING
    }

    fun onPlayer1Choose(move: Move) {
        soundManager.playSelect()
        p1SelectedMove = move

        when (mode) {
            GameMode.BOT -> {
                // Trigger Bot move and reveal animation
                viewModelScope.launch {
                    gameState = GameState.ANIMATING_COUNTDOWN
                    countdownText = "Rock..."
                    soundManager.playCountdownTick()
                    delay(380)

                    countdownText = "Paper..."
                    soundManager.playCountdownTick()
                    delay(380)

                    countdownText = "Scissors..."
                    soundManager.playCountdownTick()
                    delay(380)

                    countdownText = "SHOOT!"
                    soundManager.playShoot()

                    botEngine.recordPlayerMove(move)
                    val botMove = botEngine.chooseMove(variant, botDifficulty)
                    p2SelectedMove = botMove

                    resolveRound(move, botMove)
                }
            }

            GameMode.PASS_AND_PLAY -> {
                gameState = GameState.WAITING_PASS_PHONE
            }

            GameMode.QUICK_DECISION -> {
                // If 2 player quick decision vs bot quick decision
                gameState = GameState.WAITING_PASS_PHONE
            }
        }
    }

    fun onPassConfirmedByPlayer2() {
        soundManager.playTap()
        gameState = GameState.P2_CHOOSING
    }

    fun onPlayer2Choose(move: Move) {
        soundManager.playSelect()
        p2SelectedMove = move
        val p1Move = p1SelectedMove ?: return

        viewModelScope.launch {
            gameState = GameState.ANIMATING_COUNTDOWN
            countdownText = "Ready..."
            soundManager.playCountdownTick()
            delay(300)

            countdownText = "3..."
            soundManager.playCountdownTick()
            delay(300)

            countdownText = "2..."
            soundManager.playCountdownTick()
            delay(300)

            countdownText = "1..."
            soundManager.playCountdownTick()
            delay(300)

            countdownText = "REVEAL!"
            soundManager.playShoot()

            resolveRound(p1Move, move)
        }
    }

    private fun resolveRound(p1Move: Move, p2Move: Move) {
        val winner: RoundWinner
        val reason: String

        if (p1Move == p2Move) {
            winner = RoundWinner.DRAW
            reason = "Draw! Both selected ${p1Move.title} ${p1Move.emoji}"
            soundManager.playDraw()
        } else if (p1Move.beats(p2Move)) {
            winner = RoundWinner.PLAYER_1
            reason = Move.getReason(p1Move, p2Move)
            player1Score++
            soundManager.playWin()
        } else {
            winner = RoundWinner.PLAYER_2
            reason = Move.getReason(p2Move, p1Move)
            player2Score++
            soundManager.playLose()
        }

        val result = RoundResult(
            roundNumber = currentRound,
            player1Move = p1Move,
            player2Move = p2Move,
            winner = winner,
            reason = reason
        )
        latestRoundResult = result
        roundHistory.add(result)

        // Check Match Over conditions
        val target = matchFormat.targetScore
        if (target < Int.MAX_VALUE && (player1Score >= target || player2Score >= target)) {
            val finalWinner = if (player1Score >= target) player1Name else player2Name
            matchWinnerName = finalWinner
            gameState = GameState.MATCH_OVER
            saveMatchRecord(finalWinner)
        } else {
            gameState = GameState.ROUND_RESOLVED
        }
    }

    fun nextRound() {
        soundManager.playTap()
        currentRound++
        p1SelectedMove = null
        p2SelectedMove = null
        latestRoundResult = null
        gameState = GameState.P1_CHOOSING
    }

    fun finishMatchEarly() {
        val winner = when {
            player1Score > player2Score -> player1Name
            player2Score > player1Score -> player2Name
            else -> null
        }
        matchWinnerName = winner
        gameState = GameState.MATCH_OVER
        saveMatchRecord(winner)
    }

    private fun saveMatchRecord(winner: String?) {
        val record = MatchRecord(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            mode = mode,
            variant = variant,
            player1Name = player1Name,
            player2Name = player2Name,
            player1Score = player1Score,
            player2Score = player2Score,
            targetScore = matchFormat.targetScore,
            winnerName = winner,
            rounds = roundHistory.toList(),
            decisionPrompt = decisionPrompt
        )
        repository.recordMatch(record)
    }
}
