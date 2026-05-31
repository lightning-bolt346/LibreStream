package com.example.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class InstanceInterceptor : Interceptor {
    
    // Updated list of known working public instances from Piped community
    private val instances = listOf(
        "https://pipedapi.kavin.rocks/",
        "https://pipedapi.us.projectsegfau.lt/",
        "https://pipedapi.in.projectsegfau.lt/",
        "https://pipedapi.eu.projectsegfau.lt/",
        "https://pipedapi.asia.projectsegfau.lt/",
        "https://pi.pivpn.moe/",
        "https://pipedapi.syncpundit.io/"
    )
    
    private var currentIndex = 0
    
    @Synchronized
    private fun getNextInstance(): String {
        currentIndex = (currentIndex + 1) % instances.size
        return instances[currentIndex]
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Sometimes the user-initiated request path contains an already resolved host,
        // so we need to override the host to point to our chosen instance.
        var maxTries = instances.size
        var tries = 0
        var lastException: Exception? = null
        var lastResponse: Response? = null
        
        while (tries < maxTries) {
            val currentInstanceUrl = instances[currentIndex].toHttpUrlOrNull() ?: break
            val newUrl = request.url.newBuilder()
                .scheme(currentInstanceUrl.scheme)
                .host(currentInstanceUrl.host)
                .port(currentInstanceUrl.port)
                .build()
                
            val newRequest = request.newBuilder().url(newUrl).build()
            
            try {
                val response = chain.proceed(newRequest)
                val isJson = response.header("Content-Type")?.contains("json") == true
                
                if (response.isSuccessful && isJson) {
                    return response
                } else {
                    if (tries == maxTries - 1) {
                        if (!isJson) {
                            response.close()
                            throw IOException("All Piped instances are down or blocking traffic.")
                        }
                        if (!response.isSuccessful) {
                            response.close()
                            throw IOException("Piped API returned error: " + response.code)
                        }
                        return response
                    } else {
                        // Close the failed response body before retrying
                        response.close()
                        getNextInstance()
                    }
                }
            } catch (e: Exception) {
                if (tries == maxTries - 1) {
                    throw e
                }
                getNextInstance()
            }
            tries++
        }
        
        throw IOException("All Piped instances failed")
    }
}

