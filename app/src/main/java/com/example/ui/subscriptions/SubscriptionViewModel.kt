package com.example.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.db.SubscriptionEntity
import com.example.repository.SubscriptionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SubscriptionViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.getAllSubscriptions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        fun provideFactory(repository: SubscriptionRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SubscriptionViewModel(repository) as T
                }
            }
    }
}
