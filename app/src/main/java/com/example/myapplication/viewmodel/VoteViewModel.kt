package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.VoteOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class VoteViewModel : ViewModel() {
    private val _options = MutableStateFlow<List<VoteOption>>(emptyList())

    // Список завжди відсортований за спаданням голосів
    val options: StateFlow<List<VoteOption>> = _options
        .map { list -> list.sortedByDescending { it.votes } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Лідер голосування
    val leader: StateFlow<VoteOption?> = _options
        .map { list -> list.maxByOrNull { o -> o.votes }?.takeIf { it.votes > 0 } }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Загальна кількість голосів
    val totalVotes: StateFlow<Int> = _options
        .map { list -> list.sumOf { it.votes } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private var nextId = 0

    fun addOption(title: String) {
        if (title.isBlank()) return
        _options.update { it + VoteOption(id = nextId++, title = title.trim()) }
    }

    fun vote(id: Int) {
        _options.update { list ->
            list.map { if (it.id == id) it.copy(votes = it.votes + 1) else it }
        }
    }

    // Видалення варіанту
    fun removeOption(id: Int) {
        _options.update { list -> list.filterNot { it.id == id } }
    }

    // Обнулення всіх голосів
    fun resetVotes() {
        _options.update { list -> list.map { it.copy(votes = 0) } }
    }
}