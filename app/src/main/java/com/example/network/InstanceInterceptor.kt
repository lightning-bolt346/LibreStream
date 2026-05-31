package com.example.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class InstanceInterceptor : Interceptor {
    
    private val instances = listOf(
        "https://pipedapi.kavin.rocks/",
        "https://pipedapi.tokhmi.xyz/",
        "https://pipedapi.astartes.nl/",
        "https://pipedapi.smnz.de/"
    )
    
    private var currentIndex = 0
    
    @Synchronized
    private fun getNextInstance(): String {
        currentIndex = (currentIndex + 1) % instances.size
        return instances[currentIndex]
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val currentHost = instances[currentIndex].toHttpUrlOrNull()?.host ?: return chain.proceed(request)
        
        val newUrl = request.url.newBuilder().host(currentHost).build()
        val newRequest = request.newBuilder().url(newUrl).build()
        
        try {
            val response = chain.proceed(newRequest)
            if (!response.isSuccessful) {
                // If the first attempt fails, advance the instance index for the next request
                getNextInstance()
            }
            return response
        } catch (e: Exception) {
            getNextInstance()
            throw e
        }
    }
}

