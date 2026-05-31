package com.example.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.db.WatchHistoryEntity
import com.example.model.PipedVideoInfo
import com.example.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val repository: AppRepository,
    private val playerManager: com.example.service.PlayerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState

    val mediaController: StateFlow<androidx.media3.session.MediaController?> = playerManager.mediaController

    fun loadVideo(videoId: String) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            try {
                val videoInfo = repository.getVideoStreams(videoId)
                // Add to history
                repository.addToHistory(
                    WatchHistoryEntity(
                        videoId = videoId,
                        title = videoInfo.title,
                        uploaderName = videoInfo.uploader,
                        thumbnailUrl = videoInfo.thumbnailUrl
                    )
                )
                _uiState.value = PlayerUiState.Success(videoInfo)
            } catch (e: Exception) {
                _uiState.value = PlayerUiState.Error(e.message ?: "Failed to load video")
            }
        }
    }

    companion object {
        fun provideFactory(repository: AppRepository, playerManager: com.example.service.PlayerManager): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayerViewModel(repository, playerManager) as T
                }
            }
    }
}

sealed class PlayerUiState {
    object Loading : PlayerUiState()
    data class Success(val videoInfo: PipedVideoInfo) : PlayerUiState()
    data class Error(val message: String) : PlayerUiState()
}
