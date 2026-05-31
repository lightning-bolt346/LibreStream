package com.example.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.db.SubscriptionEntity
import com.example.model.ChannelInfo
import com.example.repository.ChannelRepository
import com.example.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChannelViewModel(
    private val channelRepository: ChannelRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState

    private var currentChannelId: String? = null

    // We can observe subscription status
    private var _isSubscribed = MutableStateFlow(false)
    val isSubscribed: StateFlow<Boolean> = _isSubscribed

    fun loadChannel(channelId: String) {
        currentChannelId = channelId
        viewModelScope.launch {
            _uiState.value = ChannelUiState.Loading
            try {
                val info = channelRepository.getChannel(channelId)
                _uiState.value = ChannelUiState.Success(info)
                
                viewModelScope.launch {
                    subscriptionRepository.getSubscriptionStatus(channelId).collect { sub ->
                        _isSubscribed.value = sub != null
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ChannelUiState.Error(e.message ?: "Failed to load channel")
            }
        }
    }

    fun toggleSubscription() {
        val state = _uiState.value
        if (state is ChannelUiState.Success) {
            viewModelScope.launch {
                val info = state.channelInfo
                if (_isSubscribed.value) {
                    subscriptionRepository.unsubscribe(info.id)
                } else {
                    subscriptionRepository.subscribe(
                        SubscriptionEntity(
                            channelId = info.id,
                            name = info.name,
                            avatarUrl = info.avatarUrl
                        )
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            channelRepository: ChannelRepository,
            subscriptionRepository: SubscriptionRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChannelViewModel(channelRepository, subscriptionRepository) as T
                }
            }
    }
}

sealed class ChannelUiState {
    object Loading : ChannelUiState()
    data class Success(val channelInfo: ChannelInfo) : ChannelUiState()
    data class Error(val message: String) : ChannelUiState()
}
