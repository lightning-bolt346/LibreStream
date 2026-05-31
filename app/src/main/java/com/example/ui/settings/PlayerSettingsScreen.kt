package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.service.PlayerPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsScreen(onNavigateUp: () -> Unit) {
    val context = LocalContext.current
    val preferences = remember { PlayerPreferences(context) }

    var swipeControls by remember { mutableStateOf(preferences.swipeControlsEnabled) }
    var doubleTapSeek by remember { mutableStateOf(preferences.doubleTapSeekEnabled) }
    var autoFullscreen by remember { mutableStateOf(preferences.autoFullscreen) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Player Settings") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                ListItem(
                    headlineContent = { Text("Swipe controls") },
                    supportingContent = { Text("Swipe to change volume or brightness") },
                    trailingContent = {
                        Switch(checked = swipeControls, onCheckedChange = { 
                            swipeControls = it
                            preferences.swipeControlsEnabled = it 
                        })
                    }
                )
                ListItem(
                    headlineContent = { Text("Double tap to seek") },
                    trailingContent = {
                        Switch(checked = doubleTapSeek, onCheckedChange = { 
                            doubleTapSeek = it
                            preferences.doubleTapSeekEnabled = it 
                        })
                    }
                )
                ListItem(
                    headlineContent = { Text("Auto fullscreen") },
                    trailingContent = {
                        Switch(checked = autoFullscreen, onCheckedChange = { 
                            autoFullscreen = it
                            preferences.autoFullscreen = it 
                        })
                    }
                )
            }
        }
    }
}
