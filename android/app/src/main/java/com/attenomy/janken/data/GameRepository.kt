package com.attenomy.janken.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class GameRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("janken_prefs", Context.MODE_PRIVATE)

    private val _stats = MutableStateFlow(loadStats())
    val stats: StateFlow<GameStats> = _stats.asStateFlow()

    private val _history = MutableStateFlow(loadHistory())
    val history: StateFlow<List<MatchRecord>> = _history.asStateFlow()

    // Settings
    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("pref_sound", true)
        set(value) = prefs.edit().putBoolean("pref_sound", value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean("pref_haptics", true)
        set(value) = prefs.edit().putBoolean("pref_haptics", value).apply()

    var themeMode: String
        get() = prefs.getString("pref_theme", "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString("pref_theme", value).apply()

    var defaultBotDifficulty: BotDifficulty
        get() = try {
            BotDifficulty.valueOf(prefs.getString("pref_bot_diff", BotDifficulty.MEDIUM.name)!!)
        } catch (_: Exception) {
            BotDifficulty.MEDIUM
        }
        set(value) = prefs.edit().putString("pref_bot_diff", value.name).apply()

    var defaultVariant: GameVariant
        get() = try {
            GameVariant.valueOf(prefs.getString("pref_variant", GameVariant.CLASSIC.name)!!)
        } catch (_: Exception) {
            GameVariant.CLASSIC
        }
        set(value) = prefs.edit().putString("pref_variant", value.name).apply()

    fun recordMatch(record: MatchRecord) {
        val currentList = _history.value.toMutableList()
        currentList.add(0, record)
        if (currentList.size > 100) {
            currentList.removeAt(currentList.lastIndex)
        }
        _history.value = currentList
        saveHistory(currentList)

        // Update stats
        var currentStats = _stats.value
        var p1Wins = currentStats.player1Wins
        var p2Wins = currentStats.player2Wins
        var draws = currentStats.draws
        var currStreak = currentStats.currentStreak
        var bestStreak = currentStats.bestStreak
        var rock = currentStats.rockCount
        var paper = currentStats.paperCount
        var scissors = currentStats.scissorsCount
        var lizard = currentStats.lizardCount
        var spock = currentStats.spockCount

        if (record.winnerName == record.player1Name) {
            p1Wins++
            currStreak++
            if (currStreak > bestStreak) bestStreak = currStreak
        } else if (record.winnerName == record.player2Name) {
            p2Wins++
            currStreak = 0
        } else {
            draws++
        }

        for (r in record.rounds) {
            when (r.player1Move) {
                Move.ROCK -> rock++
                Move.PAPER -> paper++
                Move.SCISSORS -> scissors++
                Move.LIZARD -> lizard++
                Move.SPOCK -> spock++
            }
            if (r.winner == RoundWinner.DRAW) {
                // don't double count if we track p1 moves or track both
            }
        }

        val updatedStats = currentStats.copy(
            totalMatches = currentStats.totalMatches + 1,
            totalRounds = currentStats.totalRounds + record.rounds.size,
            player1Wins = p1Wins,
            player2Wins = p2Wins,
            draws = draws,
            currentStreak = currStreak,
            bestStreak = bestStreak,
            rockCount = rock,
            paperCount = paper,
            scissorsCount = scissors,
            lizardCount = lizard,
            spockCount = spock
        )
        _stats.value = updatedStats
        saveStats(updatedStats)
    }

    fun clearHistoryAndStats() {
        prefs.edit().remove("history_json").remove("stats_json").apply()
        _history.value = emptyList()
        _stats.value = GameStats()
    }

    private fun loadStats(): GameStats {
        val json = prefs.getString("stats_json", null) ?: return GameStats()
        return try {
            val obj = JSONObject(json)
            GameStats(
                totalMatches = obj.optInt("totalMatches", 0),
                totalRounds = obj.optInt("totalRounds", 0),
                player1Wins = obj.optInt("player1Wins", 0),
                player2Wins = obj.optInt("player2Wins", 0),
                draws = obj.optInt("draws", 0),
                currentStreak = obj.optInt("currentStreak", 0),
                bestStreak = obj.optInt("bestStreak", 0),
                rockCount = obj.optInt("rockCount", 0),
                paperCount = obj.optInt("paperCount", 0),
                scissorsCount = obj.optInt("scissorsCount", 0),
                lizardCount = obj.optInt("lizardCount", 0),
                spockCount = obj.optInt("spockCount", 0)
            )
        } catch (_: Exception) {
            GameStats()
        }
    }

    private fun saveStats(stats: GameStats) {
        val obj = JSONObject().apply {
            put("totalMatches", stats.totalMatches)
            put("totalRounds", stats.totalRounds)
            put("player1Wins", stats.player1Wins)
            put("player2Wins", stats.player2Wins)
            put("draws", stats.draws)
            put("currentStreak", stats.currentStreak)
            put("bestStreak", stats.bestStreak)
            put("rockCount", stats.rockCount)
            put("paperCount", stats.paperCount)
            put("scissorsCount", stats.scissorsCount)
            put("lizardCount", stats.lizardCount)
            put("spockCount", stats.spockCount)
        }
        prefs.edit().putString("stats_json", obj.toString()).apply()
    }

    private fun loadHistory(): List<MatchRecord> {
        val json = prefs.getString("history_json", null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<MatchRecord>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val roundsArr = obj.optJSONArray("rounds") ?: JSONArray()
                val rounds = mutableListOf<RoundResult>()
                for (j in 0 until roundsArr.length()) {
                    val rObj = roundsArr.getJSONObject(j)
                    rounds.add(
                        RoundResult(
                            roundNumber = rObj.getInt("roundNumber"),
                            player1Move = Move.valueOf(rObj.getString("p1Move")),
                            player2Move = Move.valueOf(rObj.getString("p2Move")),
                            winner = RoundWinner.valueOf(rObj.getString("winner")),
                            reason = rObj.getString("reason")
                        )
                    )
                }

                list.add(
                    MatchRecord(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("timestamp"),
                        mode = GameMode.valueOf(obj.getString("mode")),
                        variant = GameVariant.valueOf(obj.getString("variant")),
                        player1Name = obj.getString("p1Name"),
                        player2Name = obj.getString("p2Name"),
                        player1Score = obj.getInt("p1Score"),
                        player2Score = obj.getInt("p2Score"),
                        targetScore = obj.getInt("targetScore"),
                        winnerName = if (obj.isNull("winnerName")) null else obj.getString("winnerName"),
                        rounds = rounds,
                        decisionPrompt = if (obj.isNull("prompt")) null else obj.getString("prompt")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveHistory(list: List<MatchRecord>) {
        val arr = JSONArray()
        for (m in list) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("timestamp", m.timestamp)
                put("mode", m.mode.name)
                put("variant", m.variant.name)
                put("p1Name", m.player1Name)
                put("p2Name", m.player2Name)
                put("p1Score", m.player1Score)
                put("p2Score", m.player2Score)
                put("targetScore", m.targetScore)
                put("winnerName", m.winnerName)
                put("prompt", m.decisionPrompt)

                val roundsArr = JSONArray()
                for (r in m.rounds) {
                    val rObj = JSONObject().apply {
                        put("roundNumber", r.roundNumber)
                        put("p1Move", r.player1Move.name)
                        put("p2Move", r.player2Move.name)
                        put("winner", r.winner.name)
                        put("reason", r.reason)
                    }
                    roundsArr.put(rObj)
                }
                put("rounds", roundsArr)
            }
            arr.put(obj)
        }
        prefs.edit().putString("history_json", arr.toString()).apply()
    }
}
