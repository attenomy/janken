package com.attenomy.janken

import com.attenomy.janken.data.BotDifficulty
import com.attenomy.janken.data.BotEngine
import com.attenomy.janken.data.GameStats
import com.attenomy.janken.data.GameVariant
import com.attenomy.janken.data.Move
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    @Test
    fun testClassicRPSRCheatSheet() {
        // Rock
        assertTrue(Move.ROCK.beats(Move.SCISSORS))
        assertFalse(Move.ROCK.beats(Move.PAPER))
        assertFalse(Move.ROCK.beats(Move.ROCK))

        // Paper
        assertTrue(Move.PAPER.beats(Move.ROCK))
        assertFalse(Move.PAPER.beats(Move.SCISSORS))

        // Scissors
        assertTrue(Move.SCISSORS.beats(Move.PAPER))
        assertFalse(Move.SCISSORS.beats(Move.ROCK))
    }

    @Test
    fun testRPSLSRules() {
        // Rock crushes Lizard
        assertTrue(Move.ROCK.beats(Move.LIZARD))
        // Paper disproves Spock
        assertTrue(Move.PAPER.beats(Move.SPOCK))
        // Scissors decapitates Lizard
        assertTrue(Move.SCISSORS.beats(Move.LIZARD))
        // Lizard poisons Spock
        assertTrue(Move.LIZARD.beats(Move.SPOCK))
        // Lizard eats Paper
        assertTrue(Move.LIZARD.beats(Move.PAPER))
        // Spock smashes Scissors
        assertTrue(Move.SPOCK.beats(Move.SCISSORS))
        // Spock vaporizes Rock
        assertTrue(Move.SPOCK.beats(Move.ROCK))
    }

    @Test
    fun testBotEngineProducesValidMoves() {
        val bot = BotEngine()
        val movesClassic = Move.classicMoves()
        val movesRPSLS = Move.rpslsMoves()

        for (i in 0 until 50) {
            val easyMove = bot.chooseMove(GameVariant.CLASSIC, BotDifficulty.EASY)
            assertTrue(easyMove in movesClassic)

            val medMove = bot.chooseMove(GameVariant.CLASSIC, BotDifficulty.MEDIUM)
            assertTrue(medMove in movesClassic)

            val hardMove = bot.chooseMove(GameVariant.RPSLS, BotDifficulty.HARD)
            assertTrue(hardMove in movesRPSLS)
        }
    }

    @Test
    fun testStatsWinRateCalculation() {
        val statsEmpty = GameStats()
        assertEquals(0f, statsEmpty.winRate, 0.01f)

        val statsWon = GameStats(player1Wins = 3, player2Wins = 1, draws = 1)
        assertEquals(75.0f, statsWon.winRate, 0.01f)
    }

    @Test
    fun testMoveReasonExplanations() {
        assertEquals("Rock crushes Scissors!", Move.getReason(Move.ROCK, Move.SCISSORS))
        assertEquals("Lizard poisons Spock!", Move.getReason(Move.LIZARD, Move.SPOCK))
        assertEquals("Spock vaporizes Rock!", Move.getReason(Move.SPOCK, Move.ROCK))
    }
}
