package com.example.ui.player

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
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
    val context = LocalContext.current

    // Initialize ExoPlayer
    val exoPlayer = remember {
        val appContainer = (context.applicationContext as LibreTubeApp).container
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        val dataSourceFactory = OkHttpDataSource.Factory(appContainer.exoPlayerOkHttpClient)
            .setUserAgent(userAgent)
            
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)

        ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                playWhenReady = true
            }
    }

    // Load video on start
    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId)
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Video Player Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            if (uiState is PlayerUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        // Details Area
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
                    LaunchedEffect(info) {
                        if (info.hls != null) {
                            val mediaItem = MediaItem.fromUri(Uri.parse(info.hls))
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                        } else {
                            val combinedStream = info.videoStreams?.firstOrNull { it.videoOnly == false }
                            if (combinedStream != null) {
                                val mediaItem = MediaItem.fromUri(Uri.parse(combinedStream.url))
                                exoPlayer.setMediaItem(mediaItem)
                                exoPlayer.prepare()
                            } else {
                                // Merging video and audio using ExoPlayer's MergingMediaSource
                                val videoUrl = info.videoStreams?.firstOrNull()?.url
                                val audioUrl = info.audioStreams?.firstOrNull()?.url

                                if (videoUrl != null && audioUrl != null) {
                                    val videoItem = MediaItem.fromUri(Uri.parse(videoUrl))
                                    val audioItem = MediaItem.fromUri(Uri.parse(audioUrl))
                                    
                                    val dataSourceFactory = androidx.media3.datasource.okhttp.OkHttpDataSource.Factory((context.applicationContext as LibreTubeApp).container.exoPlayerOkHttpClient)
                                    val mediaSourceFactory = androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory)
                                    
                                    val videoSource = mediaSourceFactory.createMediaSource(videoItem)
                                    val audioSource = mediaSourceFactory.createMediaSource(audioItem)
                                    
                                    val mergedSource = androidx.media3.exoplayer.source.MergingMediaSource(videoSource, audioSource)
                                    exoPlayer.setMediaSource(mergedSource)
                                    exoPlayer.prepare()
                                } else if (videoUrl != null) {
                                    val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.prepare()
                                } else if (audioUrl != null) {
                                    val mediaItem = MediaItem.fromUri(Uri.parse(audioUrl))
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.prepare()
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
