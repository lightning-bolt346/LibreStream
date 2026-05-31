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
fun AudioVideoSettingsScreen(onNavigateUp: () -> Unit) {
    val context = LocalContext.current
    val preferences = remember { PlayerPreferences(context) }

    var autoplay by remember { mutableStateOf(preferences.autoplayEnabled) }
    var defaultResolution by remember { mutableStateOf(preferences.defaultResolution) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Audio & Video") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                ListItem(
                    headlineContent = { Text("Autoplay") },
                    supportingContent = { Text("Play next video automatically") },
                    trailingContent = {
                        Switch(checked = autoplay, onCheckedChange = { 
                            autoplay = it
                            preferences.autoplayEnabled = it 
                        })
                    }
                )
                ListItem(
                    headlineContent = { Text("Default Resolution") },
                    supportingContent = { Text(defaultResolution) }
                )
            }
        }
    }
}
