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
}
