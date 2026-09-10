package com.attenomy.janken.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.attenomy.janken.data.GameMode
import com.attenomy.janken.data.GameRepository
import com.attenomy.janken.data.GameStats
import com.attenomy.janken.data.MatchRecord
import kotlinx.coroutines.flow.StateFlow

class StatsViewModel(private val repository: GameRepository) : ViewModel() {
    val stats: StateFlow<GameStats> = repository.stats
    val history: StateFlow<List<MatchRecord>> = repository.history

    fun clearAllData() {
        repository.clearHistoryAndStats()
    }

    fun getFilterHistory(mode: GameMode?): List<MatchRecord> {
        val list = history.value
        return if (mode == null) list else list.filter { it.mode == mode }
    }
}
