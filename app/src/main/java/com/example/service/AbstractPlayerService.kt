package com.example.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.LibreTubeApp

@OptIn(UnstableApi::class)
abstract class AbstractPlayerService : MediaSessionService() {

    protected var player: ExoPlayer? = null
    protected var mediaSession: MediaSession? = null
    protected lateinit var trackSelector: androidx.media3.exoplayer.trackselection.DefaultTrackSelector

    override fun onCreate() {
        super.onCreate()
        
        val appContainer = (applicationContext as LibreTubeApp).container
        
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        val dataSourceFactory = OkHttpDataSource.Factory(appContainer.exoPlayerOkHttpClient)
            .setUserAgent(userAgent)
            
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        
        trackSelector = androidx.media3.exoplayer.trackselection.DefaultTrackSelector(this)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true // handle audio focus
            )
            .build()
            
        mediaSession = MediaSession.Builder(this, player!!)
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == ExoPlayer.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
