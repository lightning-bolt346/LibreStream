package com.example.ui.channel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.components.VideoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(
    channelId: String,
    viewModel: ChannelViewModel,
    onVideoClick: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSubscribed by viewModel.isSubscribed.collectAsStateWithLifecycle()

    LaunchedEffect(channelId) {
        viewModel.loadChannel(channelId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState is ChannelUiState.Success) (uiState as ChannelUiState.Success).channelInfo.name else "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ChannelUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ChannelUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ChannelUiState.Success -> {
                    val info = state.channelInfo
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            if (info.bannerUrl != null) {
                                AsyncImage(
                                    model = info.bannerUrl,
                                    contentDescription = "Banner",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth().aspectRatio(16f/3f)
                                )
                            }
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = info.avatarUrl,
                                    contentDescription = info.name,
                                    modifier = Modifier.size(80.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = info.name, style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    text = "${info.subscriberCount} subscribers",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = { viewModel.toggleSubscription() }) {
                                    Text(if (isSubscribed) "Unsubscribe" else "Subscribe")
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = info.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 3 // Simple truncation for now
                                )
                            }
                            HorizontalDivider()
                        }
                        
                        items(info.relatedStreams) { video ->
                            val videoId = video.url.substringAfter("?v=")
                            VideoCard(
                                title = video.title,
                                uploaderName = video.uploaderName,
                                thumbnailUrl = video.thumbnailUrl,
                                uploaderAvatarUrl = video.uploaderAvatar,
                                views = video.views,
                                durationFormatted = null,
                                onClick = { onVideoClick(videoId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
