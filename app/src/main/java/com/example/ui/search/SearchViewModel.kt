package com.example.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.db.SearchHistoryEntity
import com.example.model.SearchItem
import com.example.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState

    val searchHistory: StateFlow<List<SearchHistoryEntity>> = repository.getSearchHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                repository.addSearchHistory(query)
                val result = repository.search(query, "all")
                _uiState.value = SearchUiState.Success(result.items)
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun deleteHistory(query: String) {
        viewModelScope.launch {
            repository.deleteSearchHistory(query)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    fun clearSearch() {
        _uiState.value = SearchUiState.Initial
    }

    companion object {
        fun provideFactory(repository: SearchRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(repository) as T
                }
            }
    }
}

sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val items: List<SearchItem>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
