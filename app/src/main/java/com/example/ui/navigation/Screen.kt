package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Subscriptions : Screen("subscriptions")
    object Playlists : Screen("playlists")
    object Settings : Screen("settings")
    object History : Screen("history")
    object Channel : Screen("channel/{channelId}") {
        fun createRoute(channelId: String) = "channel/$channelId"
    }
    object Player : Screen("player/{videoId}") {
        fun createRoute(videoId: String) = "player/$videoId"
    }
}
