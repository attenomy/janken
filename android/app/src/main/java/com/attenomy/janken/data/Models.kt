package com.attenomy.janken.data

enum class GameVariant(val title: String, val description: String) {
    CLASSIC("Classic (3 Moves)", "Rock • Paper • Scissors"),
    RPSLS("RPSLS (5 Moves)", "Rock • Paper • Scissors • Lizard • Spock")
}

enum class Move(val title: String, val emoji: String) {
    ROCK("Rock", "✊"),
    PAPER("Paper", "✋"),
    SCISSORS("Scissors", "✌️"),
    LIZARD("Lizard", "🦎"),
    SPOCK("Spock", "🖖");

    fun beats(other: Move): Boolean {
        return when (this) {
            ROCK -> other == SCISSORS || other == LIZARD
            PAPER -> other == ROCK || other == SPOCK
            SCISSORS -> other == PAPER || other == LIZARD
            LIZARD -> other == SPOCK || other == PAPER
            SPOCK -> other == SCISSORS || other == ROCK
        }
    }

    companion object {
        fun classicMoves(): List<Move> = listOf(ROCK, PAPER, SCISSORS)
        fun rpslsMoves(): List<Move> = listOf(ROCK, PAPER, SCISSORS, LIZARD, SPOCK)

        fun forVariant(variant: GameVariant): List<Move> = when (variant) {
            GameVariant.CLASSIC -> classicMoves()
            GameVariant.RPSLS -> rpslsMoves()
        }

        fun getReason(winner: Move, loser: Move): String {
            if (winner == loser) return "Draw! Both picked ${winner.title}"
            return when (winner to loser) {
                ROCK to SCISSORS -> "Rock crushes Scissors!"
                ROCK to LIZARD -> "Rock crushes Lizard!"
                PAPER to ROCK -> "Paper covers Rock!"
                PAPER to SPOCK -> "Paper disproves Spock!"
                SCISSORS to PAPER -> "Scissors cuts Paper!"
                SCISSORS to LIZARD -> "Scissors decapitates Lizard!"
                LIZARD to SPOCK -> "Lizard poisons Spock!"
                LIZARD to PAPER -> "Lizard eats Paper!"
                SPOCK to SCISSORS -> "Spock smashes Scissors!"
                SPOCK to ROCK -> "Spock vaporizes Rock!"
                else -> "${winner.title} beats ${loser.title}!"
            }
        }
    }
}

enum class GameMode(val title: String, val subtitle: String, val icon: String) {
    BOT("vs Computer", "Test your wits against smart Bot AI", "🤖"),
    PASS_AND_PLAY("Pass & Play", "Local 2-Player on 1 phone with privacy lock", "👥"),
    QUICK_DECISION("Quick Decision", "Settle real-world debates instantly", "⚡")
}

enum class MatchFormat(val title: String, val shortTitle: String, val targetScore: Int) {
    BEST_OF_1("Best of 1", "1 Round", 1),
    BEST_OF_3("Best of 3 (First to 2)", "First to 2", 2),
    BEST_OF_5("Best of 5 (First to 3)", "First to 3", 3),
    FIRST_TO_5("First to 5", "First to 5", 5),
    FIRST_TO_10("First to 10", "First to 10", 10),
    ENDLESS("Endless Play", "Endless", Int.MAX_VALUE)
}

enum class BotDifficulty(val title: String, val description: String) {
    EASY("Easy", "Pure random moves"),
    MEDIUM("Medium", "Observant counter-attacker"),
    HARD("Hard", "Markov pattern predictor AI")
}

enum class RoundWinner {
    PLAYER_1,
    PLAYER_2,
    DRAW
}

data class RoundResult(
    val roundNumber: Int,
    val player1Move: Move,
    val player2Move: Move,
    val winner: RoundWinner,
    val reason: String
)

data class MatchRecord(
    val id: String,
    val timestamp: Long,
    val mode: GameMode,
    val variant: GameVariant,
    val player1Name: String,
    val player2Name: String,
    val player1Score: Int,
    val player2Score: Int,
    val targetScore: Int,
    val winnerName: String?,
    val rounds: List<RoundResult>,
    val decisionPrompt: String? = null
)

data class GameStats(
    val totalMatches: Int = 0,
    val totalRounds: Int = 0,
    val player1Wins: Int = 0,
    val player2Wins: Int = 0,
    val draws: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val rockCount: Int = 0,
    val paperCount: Int = 0,
    val scissorsCount: Int = 0,
    val lizardCount: Int = 0,
    val spockCount: Int = 0
) {
    val winRate: Float
        get() {
            val decisive = player1Wins + player2Wins
            return if (decisive == 0) 0f else (player1Wins.toFloat() / decisive) * 100f
        }

    fun getCountForMove(move: Move): Int = when (move) {
        Move.ROCK -> rockCount
        Move.PAPER -> paperCount
        Move.SCISSORS -> scissorsCount
        Move.LIZARD -> lizardCount
        Move.SPOCK -> spockCount
    }
}
