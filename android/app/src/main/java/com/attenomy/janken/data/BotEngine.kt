package com.attenomy.janken.data

import kotlin.random.Random

class BotEngine {
    // Markov transition table: previousMove -> (nextMove -> count)
    private val transitions = mutableMapOf<Move, MutableMap<Move, Int>>()
    private val moveHistory = mutableListOf<Move>()
    private var lastPlayerMove: Move? = null

    fun reset() {
        transitions.clear()
        moveHistory.clear()
        lastPlayerMove = null
    }

    fun recordPlayerMove(move: Move) {
        lastPlayerMove?.let { prev ->
            val nextMap = transitions.getOrPut(prev) { mutableMapOf() }
            nextMap[move] = (nextMap[move] ?: 0) + 1
        }
        moveHistory.add(move)
        lastPlayerMove = move
    }

    fun chooseMove(
        variant: GameVariant,
        difficulty: BotDifficulty
    ): Move {
        val available = Move.forVariant(variant)

        return when (difficulty) {
            BotDifficulty.EASY -> available.random()

            BotDifficulty.MEDIUM -> {
                // 40% random, 60% counter to previous move
                if (lastPlayerMove == null || Random.nextFloat() < 0.4f) {
                    available.random()
                } else {
                    counterMove(lastPlayerMove!!, available)
                }
            }

            BotDifficulty.HARD -> {
                if (moveHistory.size < 2) {
                    available.random()
                } else {
                    val predictedPlayerMove = predictNextPlayerMove(available)
                    counterMove(predictedPlayerMove, available)
                }
            }
        }
    }

    private fun predictNextPlayerMove(available: List<Move>): Move {
        val prev = lastPlayerMove ?: return available.random()
        val nextMap = transitions[prev]

        if (!nextMap.isNullOrEmpty()) {
            val mostLikely = nextMap.maxByOrNull { it.value }?.key
            if (mostLikely != null && mostLikely in available) {
                return mostLikely
            }
        }

        // Fallback: Win-stay, lose-shift heuristic or global frequency
        val freqMap = moveHistory.groupingBy { it }.eachCount()
        return available.maxByOrNull { freqMap[it] ?: 0 } ?: available.random()
    }

    private fun counterMove(target: Move, available: List<Move>): Move {
        val winningMoves = available.filter { it.beats(target) }
        return if (winningMoves.isNotEmpty()) {
            winningMoves.random()
        } else {
            available.random()
        }
    }
}
