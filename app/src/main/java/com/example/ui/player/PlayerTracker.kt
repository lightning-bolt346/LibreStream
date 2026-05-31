package com.example.ui.player

import androidx.compose.runtime.*
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.delay

@Composable
fun rememberPlayerState(mediaController: MediaController?): PlayerTracker {
    val state = remember(mediaController) { PlayerTracker(mediaController) }
    
    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                state.playbackState = playbackState
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                state.isPlaying = isPlaying
            }
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                state.totalTime = player.duration.coerceAtLeast(0L)
            }
        }
        mediaController?.addListener(listener)
        // Initial setup
        mediaController?.let {
            state.playbackState = it.playbackState
            state.isPlaying = it.isPlaying
            state.totalTime = it.duration.coerceAtLeast(0L)
        }
        
        onDispose {
            mediaController?.removeListener(listener)
        }
    }
    
    // Timer to update current time when playing
    LaunchedEffect(state.isPlaying, mediaController) {
        while (state.isPlaying && mediaController != null) {
            state.currentTime = mediaController.currentPosition.coerceAtLeast(0L)
            delay(500)
        }
    }
    
    return state
}

class PlayerTracker(private val mediaController: MediaController?) {
    var isPlaying by mutableStateOf(mediaController?.isPlaying == true)
    var playbackState by mutableStateOf(mediaController?.playbackState ?: Player.STATE_IDLE)
    var currentTime by mutableStateOf(mediaController?.currentPosition?.coerceAtLeast(0L) ?: 0L)
    var totalTime by mutableStateOf(mediaController?.duration?.coerceAtLeast(0L) ?: 0L)
}
