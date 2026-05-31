package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.channel.ChannelScreen
import com.example.ui.channel.ChannelViewModel
import com.example.ui.history.HistoryScreen
import com.example.ui.history.HistoryViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.navigation.Screen
import com.example.ui.player.PlayerScreen
import com.example.ui.player.PlayerViewModel
import com.example.ui.playlists.PlaylistViewModel
import com.example.ui.playlists.PlaylistsScreen
import com.example.ui.search.SearchScreen
import com.example.ui.search.SearchViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.subscriptions.SubscriptionViewModel
import com.example.ui.subscriptions.SubscriptionsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val appContainer = (application as LibreTubeApp).container

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomNav = currentDestination?.route in listOf(
                    Screen.Home.route,
                    Screen.Subscriptions.route,
                    Screen.Playlists.route,
                    Screen.Settings.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar {
                                val navItems = listOf(
                                    Triple(Screen.Home, Icons.Filled.Home, "Home"),
                                    Triple(Screen.Subscriptions, Icons.Filled.Face, "Subscriptions"),
                                    Triple(Screen.Playlists, Icons.Filled.PlayArrow, "Playlists"),
                                    Triple(Screen.Settings, Icons.Filled.Settings, "Settings")
                                )
                                navItems.forEach { (screen, icon, label) ->
                                    NavigationBarItem(
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            val viewModel: HomeViewModel = viewModel(
                                factory = HomeViewModel.provideFactory(appContainer.appRepository)
                            )
                            HomeScreen(
                                viewModel = viewModel,
                                onVideoClick = { videoId ->
                                    navController.navigate(Screen.Player.createRoute(videoId))
                                },
                                onSearchClick = {
                                    navController.navigate(Screen.Search.route)
                                }
                            )
                        }

                        composable(Screen.Search.route) {
                            val viewModel: SearchViewModel = viewModel(
                                factory = SearchViewModel.provideFactory(appContainer.searchRepository)
                            )
                            SearchScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() },
                                onVideoClick = { videoId ->
                                    navController.navigate(Screen.Player.createRoute(videoId))
                                }
                            )
                        }

                        composable(Screen.Subscriptions.route) {
                            val viewModel: SubscriptionViewModel = viewModel(
                                factory = SubscriptionViewModel.provideFactory(appContainer.subscriptionRepository)
                            )
                            SubscriptionsScreen(
                                viewModel = viewModel,
                                onChannelClick = { channelId ->
                                    navController.navigate(Screen.Channel.createRoute(channelId))
                                }
                            )
                        }

                        composable(Screen.Playlists.route) {
                            val viewModel: PlaylistViewModel = viewModel(
                                factory = PlaylistViewModel.provideFactory(appContainer.playlistRepository)
                            )
                            PlaylistsScreen(
                                viewModel = viewModel,
                                onPlaylistClick = { playlistId ->
                                    // Navigate to playlist details (TODO)
                                }
                            )
                        }

                        composable(Screen.Settings.route) {
                            val viewModel: SettingsViewModel = viewModel(
                                factory = SettingsViewModel.provideFactory(appContainer.settingsRepository)
                            )
                            SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                        
                        composable(Screen.History.route) {
                            val viewModel: HistoryViewModel = viewModel(
                                factory = HistoryViewModel.provideFactory(appContainer.appRepository)
                            )
                            HistoryScreen(
                                viewModel = viewModel,
                                onVideoClick = { videoId ->
                                    navController.navigate(Screen.Player.createRoute(videoId))
                                }
                            )
                        }

                        composable(Screen.Channel.route) { backStackEntry ->
                            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
                            val viewModel: ChannelViewModel = viewModel(
                                factory = ChannelViewModel.provideFactory(
                                    appContainer.channelRepository,
                                    appContainer.subscriptionRepository
                                )
                            )
                            ChannelScreen(
                                channelId = channelId,
                                viewModel = viewModel,
                                onVideoClick = { videoId ->
                                    navController.navigate(Screen.Player.createRoute(videoId))
                                },
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                        
                        composable(Screen.Player.route) { backStackEntry ->
                            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                            val viewModel: PlayerViewModel = viewModel(
                                factory = PlayerViewModel.provideFactory(appContainer.appRepository)
                            )
                            PlayerScreen(
                                videoId = videoId,
                                viewModel = viewModel,
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}
