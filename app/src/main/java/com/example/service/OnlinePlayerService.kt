package com.example.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

class OnlinePlayerService : AbstractPlayerService() {
    private lateinit var playerPreferences: PlayerPreferences
    private lateinit var queueManager: PlayerQueueManager

    override fun onCreate() {
        super.onCreate()
        playerPreferences = PlayerPreferences(this)
        queueManager = PlayerQueueManager()
        
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    if (playerPreferences.autoplayEnabled) {
                        // In a full implementation, you would load the next video here
                    }
                }
            }
        })
    }
}

class PlayerQueueManager {
    private val queue = mutableListOf<MediaItem>()
    private var currentIndex = -1

    fun setQueue(items: List<MediaItem>) {
        queue.clear()
        queue.addAll(items)
        currentIndex = 0
    }

    fun next(): MediaItem? {
        if (currentIndex < queue.size - 1) {
            return queue[++currentIndex]
        }
        return null
    }

    fun previous(): MediaItem? {
        if (currentIndex > 0) {
            return queue[--currentIndex]
        }
        return null
    }
}
