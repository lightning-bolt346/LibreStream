package com.example.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val pipedInstanceUrl: StateFlow<String> = repository.pipedInstanceUrl
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun setPipedInstanceUrl(url: String) {
        repository.setPipedInstanceUrl(url)
    }

    val isDirectMode = repository.isDirectMode()
    fun setDirectMode(enabled: Boolean) = repository.setDirectMode(enabled)

    val isLocalExtraction = repository.isLocalExtraction()
    fun setLocalExtraction(enabled: Boolean) = repository.setLocalExtraction(enabled)

    companion object {
        fun provideFactory(repository: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(repository) as T
                }
            }
    }
}
