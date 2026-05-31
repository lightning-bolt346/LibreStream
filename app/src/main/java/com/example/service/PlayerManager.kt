package com.example.service

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.ContextCompat

class PlayerManager(private val context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    
    private val _mediaController = MutableStateFlow<MediaController?>(null)
    val mediaController: StateFlow<MediaController?> = _mediaController.asStateFlow()

    fun initialize() {
        if (controllerFuture == null) {
            val sessionToken = SessionToken(context, ComponentName(context, OnlinePlayerService::class.java))
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture?.addListener(
                { 
                    try {
                        _mediaController.value = controllerFuture?.get() 
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                ContextCompat.getMainExecutor(context)
            )
        }
    }

    fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
            controllerFuture = null
        }
        _mediaController.value = null
    }
}
