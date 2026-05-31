package com.example.ui.player

import android.content.Context
import android.media.AudioManager
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

class PlayerGestureHandler(private val context: Context, private val window: Window?) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    
    private var currentBrightness: Float = 0.5f
    private var currentVolume: Int = 0
    private var accumulatedDragY: Float = 0f
    
    fun onDragStart() {
        accumulatedDragY = 0f
        currentBrightness = window?.attributes?.screenBrightness ?: 0.5f
        if (currentBrightness < 0) currentBrightness = 0.5f
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }
    
    fun onVerticalDrag(deltaY: Float, fullHeight: Float, isLeftSide: Boolean) {
        accumulatedDragY += deltaY
        
        // Only apply if sufficient drag has occurred to prevent flooding WindowManager
        if (abs(accumulatedDragY) > 10f) {
            val percentage = -(accumulatedDragY / fullHeight)
            
            if (isLeftSide) {
                // Brightness
                val newBrightness = (currentBrightness + percentage).coerceIn(0.01f, 1f)
                
                // Only update WindowManager if brightness changed by at least 3% to prevent InputDispatcher flood
                if (abs(newBrightness - currentBrightness) > 0.03f) {
                    currentBrightness = newBrightness // update current
                    window?.let {
                        val layoutParams = it.attributes
                        layoutParams.screenBrightness = newBrightness
                        it.attributes = layoutParams
                    }
                }
            } else {
                // Volume
                val volumeDelta = (percentage * maxVolume).toInt()
                val newVolume = (currentVolume + volumeDelta).coerceIn(0, maxVolume)
                if (newVolume != currentVolume) {
                    currentVolume = newVolume
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
                }
            }
            
            // Reset accumulator after converting to volume/brightness change
            accumulatedDragY = 0f
        }
    }
}

