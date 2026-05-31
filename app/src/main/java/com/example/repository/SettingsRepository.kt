package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("libretube_settings", Context.MODE_PRIVATE)

    private val _pipedInstanceUrl = MutableStateFlow(prefs.getString("piped_instance", "https://pipedapi.smnz.de/")!!)
    val pipedInstanceUrl: StateFlow<String> = _pipedInstanceUrl

    fun setPipedInstanceUrl(url: String) {
        prefs.edit().putString("piped_instance", url).apply()
        _pipedInstanceUrl.value = url
    }
    
    fun isDirectMode(): Boolean = prefs.getBoolean("direct_mode", false)
    fun setDirectMode(enabled: Boolean) = prefs.edit().putBoolean("direct_mode", enabled).apply()

    fun isLocalExtraction(): Boolean = prefs.getBoolean("local_extraction", false)
    fun setLocalExtraction(enabled: Boolean) = prefs.edit().putBoolean("local_extraction", enabled).apply()

    fun isSwipeControlsEnabled(): Boolean = prefs.getBoolean("player_swipe_controls", true)
    fun setSwipeControlsEnabled(enabled: Boolean) = prefs.edit().putBoolean("player_swipe_controls", enabled).apply()

    fun isFullscreenGesturesEnabled(): Boolean = prefs.getBoolean("fullscreen_gestures", true)
    fun setFullscreenGesturesEnabled(enabled: Boolean) = prefs.edit().putBoolean("fullscreen_gestures", enabled).apply()

    fun isDoubleTapSeekEnabled(): Boolean = prefs.getBoolean("double_tap_seek", true)
    fun setDoubleTapSeekEnabled(enabled: Boolean) = prefs.edit().putBoolean("double_tap_seek", enabled).apply()

    fun getBehaviorWhenMinimized(): String = prefs.getString("behavior_when_minimized", "pip") ?: "pip"
    fun setBehaviorWhenMinimized(behavior: String) = prefs.edit().putString("behavior_when_minimized", behavior).apply()
}
