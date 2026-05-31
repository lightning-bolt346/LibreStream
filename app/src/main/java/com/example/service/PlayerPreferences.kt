package com.example.service

import android.content.Context
import android.content.SharedPreferences

class PlayerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("player_prefs", Context.MODE_PRIVATE)

    var doubleTapSeekEnabled: Boolean
        get() = prefs.getBoolean("double_tap_seek", true)
        set(value) = prefs.edit().putBoolean("double_tap_seek", value).apply()

    var seekIncrement: Int
        get() = prefs.getInt("seek_increment", 10000)
        set(value) = prefs.edit().putInt("seek_increment", value).apply()

    var swipeControlsEnabled: Boolean
        get() = prefs.getBoolean("player_swipe_controls", true)
        set(value) = prefs.edit().putBoolean("player_swipe_controls", value).apply()

    var fullscreenGesturesEnabled: Boolean
        get() = prefs.getBoolean("fullscreen_gestures", true)
        set(value) = prefs.edit().putBoolean("fullscreen_gestures", value).apply()

    var minimizeBehavior: String
        get() = prefs.getString("minimize_behavior", "pip") ?: "pip"
        set(value) = prefs.edit().putString("minimize_behavior", value).apply()

    var autoFullscreen: Boolean
        get() = prefs.getBoolean("auto_fullscreen", false)
        set(value) = prefs.edit().putBoolean("auto_fullscreen", value).apply()

    var autoplayEnabled: Boolean
        get() = prefs.getBoolean("autoplay", false)
        set(value) = prefs.edit().putBoolean("autoplay", value).apply()

    var defaultPlaybackSpeed: Float
        get() = prefs.getFloat("default_playback_speed", 1.0f)
        set(value) = prefs.edit().putFloat("default_playback_speed", value).apply()
        
    var defaultResolution: String
        get() = prefs.getString("default_resolution", "1080p") ?: "1080p"
        set(value) = prefs.edit().putString("default_resolution", value).apply()
}
