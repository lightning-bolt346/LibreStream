package com.example.ui.subscriptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: SubscriptionViewModel,
    onChannelClick: (String) -> Unit
) {
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Subscriptions") })
        }
    ) { padding ->
        if (subscriptions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No subscriptions yet")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(subscriptions, key = { it.channelId }) { channel ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChannelClick(channel.channelId) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = channel.avatarUrl,
                            contentDescription = channel.name,
                            modifier = Modifier.size(48.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
