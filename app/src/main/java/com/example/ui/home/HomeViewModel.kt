package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.TrendingVideo
import com.example.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadTrending()
    }

    fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val videos = repository.getTrending()
                _uiState.value = HomeUiState.Success(videos)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    companion object {
        fun provideFactory(repository: AppRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val videos: List<TrendingVideo>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
