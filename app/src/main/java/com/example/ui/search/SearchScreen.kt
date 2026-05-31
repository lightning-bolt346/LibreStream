package com.example.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.VideoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onVideoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { 
                    active = false
                    viewModel.search(it)
                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Search YouTube") },
                leadingIcon = {
                    if (active) {
                        IconButton(onClick = { active = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (active) 0.dp else 16.dp, vertical = 8.dp)
            ) {
                LazyColumn {
                    items(searchHistory) { historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    query = historyItem.query
                                    active = false
                                    viewModel.search(historyItem.query)
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.List, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = historyItem.query, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.deleteHistory(historyItem.query) }) {
                                Icon(Icons.Default.Clear, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is SearchUiState.Initial -> {
                    // Empty state (just background)
                }
                is SearchUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SearchUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SearchUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ScrollableTabRow(
                            selectedTabIndex = 0,
                            modifier = Modifier.fillMaxWidth(),
                            edgePadding = 8.dp
                        ) {
                            Tab(selected = true, onClick = {}, text = { Text("All") })
                            Tab(selected = false, onClick = {}, text = { Text("Videos") })
                            Tab(selected = false, onClick = {}, text = { Text("Channels") })
                            Tab(selected = false, onClick = {}, text = { Text("Playlists") })
                            Tab(selected = false, onClick = {}, text = { Text("Today") })
                            Tab(selected = false, onClick = {}, text = { Text("Short") })
                        }
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.items) { item ->
                            if (item.type == "stream" || item.type == "video") { // api uses "stream" or "video" sometimes
                                val videoId = item.url.substringAfter("?v=")
                                VideoCard(
                                    title = item.displayTitle,
                                    uploaderName = item.uploaderName,
                                    thumbnailUrl = item.thumbnailUrl,
                                    uploaderAvatarUrl = item.uploaderAvatar,
                                    views = item.views,
                                    durationFormatted = null, // TODO: format item.duration
                                    onClick = { onVideoClick(videoId) }
                                )
                            } else {
                                // Channel or Playlist rendering not fully implemented here
                                // For MVP we can just show title and skip
                                ListItem(
                                    headlineContent = { Text(item.displayTitle) },
                                    supportingContent = { Text("Type: ${item.type}") }
                                )
                            }
                        }
                    }
                    } // close Column
                }
            }
        }
    }
}
