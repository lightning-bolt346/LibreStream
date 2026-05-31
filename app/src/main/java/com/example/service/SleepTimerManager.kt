package com.example.service

import kotlinx.coroutines.*

class SleepTimerManager(private val onTimerComplete: () -> Unit) {
    private var job: Job? = null
    var activeTimerMinutes: Int = 0
        private set
        
    fun startTimer(minutes: Int) {
        job?.cancel()
        activeTimerMinutes = minutes
        if (minutes > 0) {
            job = CoroutineScope(Dispatchers.Main).launch {
                delay(minutes * 60 * 1000L)
                onTimerComplete()
                activeTimerMinutes = 0
            }
        }
    }
    
    fun cancelTimer() {
        job?.cancel()
        job = null
        activeTimerMinutes = 0
    }
}
