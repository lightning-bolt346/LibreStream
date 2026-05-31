package com.example.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class InstanceInterceptor : Interceptor {
    
    private val instances = listOf(
        "https://pipedapi.kavin.rocks/",
        "https://pipedapi.smnz.de/",
        "https://pipedapi.drgns.space/",
        "https://api.piped.yt/",
        "https://pipedapi.moomoo.me/",
        "https://pipedapi.syncpundit.io/"
    )
    
    private var currentIndex = 0
    
    @Synchronized
    private fun getNextInstance(): String {
        currentIndex = (currentIndex + 1) % instances.size
        return instances[currentIndex]
    }
    
    @Synchronized
    private fun getCurrentInstance(): String {
        return instances[currentIndex]
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var currentHost = getCurrentInstance().toHttpUrlOrNull()?.host ?: return chain.proceed(request)
        
        var newUrl = request.url.newBuilder().host(currentHost).build()
        request = request.newBuilder().url(newUrl).build()
        
        var response: Response? = null
        var error: IOException? = null
        
        val maxRetries = instances.size
        var attempt = 0
        
        while (attempt < maxRetries) {
            try {
                response = chain.proceed(request)
                
                val contentType = response.header("Content-Type")
                val isJson = contentType?.contains("json", ignoreCase = true) == true
                
                if (response.isSuccessful && isJson) {
                    return response
                } else {
                    response.close() // Close before retrying
                    if (attempt == maxRetries - 1) {
                        break
                    }
                    currentHost = getNextInstance().toHttpUrlOrNull()?.host ?: currentHost
                    newUrl = request.url.newBuilder().host(currentHost).build()
                    request = request.newBuilder().url(newUrl).build()
                }
            } catch (e: Exception) {
                error = if (e is IOException) e else IOException(e)
                if (e.message?.contains("Canceled", ignoreCase = true) == true) {
                    throw error
                }
                currentHost = getNextInstance().toHttpUrlOrNull()?.host ?: currentHost
                newUrl = request.url.newBuilder().host(currentHost).build()
                request = request.newBuilder().url(newUrl).build()
            }
            attempt++
        }
        
        if (response != null) return response
        throw error ?: IOException("Cannot connect to Piped API: Check network or API availability.")
    }
}
