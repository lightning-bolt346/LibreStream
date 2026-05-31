package com.example.ui.player

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage

import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

import androidx.media3.exoplayer.DefaultRenderersFactory
import com.example.LibreTubeApp

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    videoId: String,
    viewModel: PlayerViewModel,
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mediaController by viewModel.mediaController.collectAsStateWithLifecycle()
    val playerState = rememberPlayerState(mediaController = mediaController)
    var showControls by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isPip = rememberPipMode()

    // Hide controls when entering PiP
    LaunchedEffect(isPip) {
        if (isPip) {
            showControls = false
        }
    }

    // Load video on start
    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Video Player Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isFullscreen) Modifier.fillMaxSize() else Modifier.aspectRatio(16f / 9f))
                .background(Color.Black)
        ) {
            val activity = context.findActivity()
            val gestureHandler = remember { PlayerGestureHandler(context, activity?.window) }
            val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.toFloat()
            
            val needsWebView = (uiState as? PlayerUiState.Success)?.videoInfo?.let { 
                it.hls.isEmpty() && it.videoStreams.isEmpty()
            } ?: false

            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

            if (needsWebView) {
                AndroidView(
                    factory = { ctx ->
                        com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            lifecycleOwner.lifecycle.addObserver(this)
                            addYouTubePlayerListener(object : com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                                    youTubePlayer.loadVideo(videoId, 0f)
                                }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = false
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        }
                    },
                    update = { playerView ->
                        if (playerView.player != mediaController) {
                            playerView.player = mediaController
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            try {
                                detectTapGestures(
                                    onTap = { showControls = !showControls },
                                    onDoubleTap = { offset ->
                                        val prefs = com.example.service.PlayerPreferences(context)
                                        if (!prefs.doubleTapSeekEnabled) return@detectTapGestures
                                        val inc = prefs.seekIncrement
                                        val middle = size.width / 2
                                        if (offset.x < middle) {
                                            // Seek backward
                                            mediaController?.seekTo(playerState.currentTime - inc)
                                        } else {
                                            // Seek forward
                                            mediaController?.seekTo(playerState.currentTime + inc)
                                        }
                                    }
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        .pointerInput(Unit) {
                            try {
                                detectDragGestures(
                                    onDragStart = { gestureHandler.onDragStart() },
                                    onDrag = { change, dragAmount ->
                                        val prefs = com.example.service.PlayerPreferences(context)
                                        if (!prefs.swipeControlsEnabled) return@detectDragGestures
                                        val isLeftSide = change.position.x < (size.width / 2)
                                        gestureHandler.onVerticalDrag(dragAmount.y, screenHeight, isLeftSide)
                                    }
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                )
            }
            
            if (!needsWebView) {
                PlayerControls(
                    modifier = Modifier.fillMaxSize(),
                    isVisible = showControls,
                    isPlaying = playerState.isPlaying,
                    title = (uiState as? PlayerUiState.Success)?.videoInfo?.title ?: "",
                    playbackState = playerState.playbackState,
                    onPauseToggle = {
                        if (playerState.isPlaying) {
                            mediaController?.pause()
                        } else {
                            mediaController?.play()
                        }
                    },
                    onBack = { 
                        if (isFullscreen) {
                            isFullscreen = false
                        } else {
                            onNavigateUp() 
                        }
                    },
                    onFullscreenToggle = {
                        isFullscreen = !isFullscreen
                    },
                    isFullscreen = isFullscreen,
                    currentTime = playerState.currentTime,
                    totalTime = playerState.totalTime,
                    onSeekTo = { mediaController?.seekTo(it) },
                    onPipToggle = {
                        val activity = context.findActivity()
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            try {
                                activity?.enterPictureInPictureMode(
                                    android.app.PictureInPictureParams.Builder()
                                        .setAspectRatio(android.util.Rational(16, 9))
                                        .build()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )
            }
        }

        // Details Area
        if (!isPip && !isFullscreen) {
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is PlayerUiState.Loading -> { } // Video area shows loader
                    is PlayerUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is PlayerUiState.Success -> {
                        val info = state.videoInfo
                        
                        // Set media item to player if not yet set
                        LaunchedEffect(info, mediaController) {
                            mediaController?.let { controller ->
                                if (controller.currentMediaItem?.mediaId != videoId) {
                                    val url = info.hls.takeIf { it.isNotEmpty() } ?: info.videoStreams.firstOrNull { it.videoOnly == false }?.url
                                    if (url != null) {
                                        val mediaItem = MediaItem.Builder()
                                            .setMediaId(videoId)
                                            .setUri(Uri.parse(url))
                                            .build()
                                        controller.setMediaItem(mediaItem)
                                        controller.prepare()
                                        controller.playWhenReady = true
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                text = info.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${info.views} views",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (info.uploaderAvatar != null) {
                                    AsyncImage(
                                        model = info.uploaderAvatar,
                                        contentDescription = info.uploader,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = info.uploader,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            Text(
                                text = info.description ?: "No description",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
