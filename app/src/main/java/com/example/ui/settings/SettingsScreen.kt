package com.example.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val instanceUrl by viewModel.pipedInstanceUrl.collectAsStateWithLifecycle()
    var showInstanceDialog by remember { mutableStateOf(false) }

    var directMode by remember { mutableStateOf(viewModel.isDirectMode) }
    var localExtraction by remember { mutableStateOf(viewModel.isLocalExtraction) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                Text(
                    text = "Instance",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )

                ListItem(
                    headlineContent = { Text("Current Instance") },
                    supportingContent = { Text(instanceUrl) },
                    modifier = Modifier.clickable { showInstanceDialog = true }
                )

                ListItem(
                    headlineContent = { Text("Full Local Mode") },
                    supportingContent = { Text("Use YouTube directly, no Piped proxy") },
                    trailingContent = {
                        Switch(checked = directMode, onCheckedChange = {
                            directMode = it
                            viewModel.setDirectMode(it)
                        })
                    }
                )

                ListItem(
                    headlineContent = { Text("Local Stream Extraction") },
                    supportingContent = { Text("Extract streams locally instead of using Piped API") },
                    trailingContent = {
                        Switch(checked = localExtraction, onCheckedChange = {
                            localExtraction = it
                            viewModel.setLocalExtraction(it)
                        })
                    }
                )
            }
        }
    }

    if (showInstanceDialog) {
        var inputUrl by remember { mutableStateOf(instanceUrl) }

        AlertDialog(
            onDismissRequest = { showInstanceDialog = false },
            title = { Text("Piped Instance") },
            text = {
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    label = { Text("Instance URL") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setPipedInstanceUrl(inputUrl)
                    showInstanceDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInstanceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
