package com.example.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class InstanceInterceptor : Interceptor {
    
    // Updated list of known working public instances from Piped community
    private val instances = listOf(
        "https://api.piped.private.coffee/",
        "https://pipedapi.kavin.rocks/"
    )
    
    private val invidiousInstances = listOf(
        "https://inv.thepixora.com",
        "https://invidious.nerdvpn.de",
        "https://yt.chocolatemoo53.com"
    )
    
    private var currentIndex = 0
    private var invidiousIndex = 0
    
    @Synchronized
    private fun getNextInstance(): String {
        currentIndex = (currentIndex + 1) % instances.size
        return instances[currentIndex]
    }
    
    @Synchronized
    private fun getNextInvidious(): String {
        invidiousIndex = (invidiousIndex + 1) % invidiousInstances.size
        return invidiousInstances[invidiousIndex]
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val pathSegments = request.url.pathSegments
        
        if (pathSegments.size >= 2 && pathSegments[0] == "streams") {
            val videoId = pathSegments[1]
            return fetchFromInvidious(chain, request, videoId)
        }
        
        var maxTries = instances.size
        var tries = 0
        
        while (tries < maxTries) {
            val targetUrl = instances[currentIndex].toHttpUrlOrNull() ?: break
            
            val newUrl = request.url.newBuilder()
                .scheme(targetUrl.scheme)
                .host(targetUrl.host)
                .port(targetUrl.port)
                .build()
                
            val newRequest = request.newBuilder()
                .url(newUrl)
                .header("Host", targetUrl.host)
                .build()
            
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
                            val code = response.code
                            response.close()
                            throw IOException("Piped API returned error: $code")
                        }
                        return response
                    } else {
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

    private fun fetchFromInvidious(chain: Interceptor.Chain, request: okhttp3.Request, videoId: String): Response {
        var maxTries = invidiousInstances.size
        var tries = 0
        
        val exceptions = mutableListOf<String>()
        while (tries < maxTries) {
            val invUrl = invidiousInstances[invidiousIndex]
            val newUrl = "$invUrl/api/v1/videos/$videoId".toHttpUrlOrNull() ?: break
            
            val newRequest = request.newBuilder()
                .url(newUrl)
                .header("Host", newUrl.host)
                .build()
                
            try {
                val response = chain.proceed(newRequest)
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    try {
                        val pipedJson = convertInvidiousToPiped(bodyString, videoId, invUrl)
                        val contentType = "application/json".toMediaType()
                        val responseBody = pipedJson.toResponseBody(contentType)
                        
                        return response.newBuilder()
                            .body(responseBody)
                            .build()
                    } catch (e: Exception) {
                        exceptions.add("$invUrl parse error: ${e.message}")
                    }
                } else {
                    exceptions.add("$invUrl http error: ${response.code}")
                    response.close()
                    getNextInvidious()
                }
            } catch (e: Exception) {
                exceptions.add("$invUrl network error: ${e.message}")
                if (tries == maxTries - 1) {
                    throw IOException("All Invidious instances failed. Errors: $exceptions")
                }
                getNextInvidious()
            }
            tries++
        }
        throw IOException("All Invidious instances failed. Errors: $exceptions")
    }

    private fun convertInvidiousToPiped(invidiousJson: String, videoId: String, invUrl: String): String {
        val jsonObj = JSONObject(invidiousJson)
        val title = jsonObj.optString("title", "")
        val description = jsonObj.optString("description", "")
        val uploader = jsonObj.optString("author", "")
        val views = jsonObj.optLong("viewCount", 0)
        val duration = jsonObj.optLong("lengthSeconds", 0)
        
        var thumbnailUrl = ""
        val thumbs = jsonObj.optJSONArray("videoThumbnails")
        if (thumbs != null && thumbs.length() > 0) {
            thumbnailUrl = thumbs.getJSONObject(0).optString("url", "")
        }
        
        val videoStreamsJson = JSONArray()
        val formatStreams = jsonObj.optJSONArray("formatStreams")
        if (formatStreams != null) {
            for (i in 0 until formatStreams.length()) {
                val stream = formatStreams.getJSONObject(i)
                val pipedStream = JSONObject()
                val itag = stream.optString("itag", "")
                
                // Use proxy URL to avoid IP blocking (403) from YouTube
                val proxyUrl = if (itag.isNotEmpty()) {
                    "$invUrl/latest_version?id=$videoId&itag=$itag&local=true"
                } else {
                    stream.optString("url")
                }
                
                pipedStream.put("url", proxyUrl)
                pipedStream.put("format", stream.optString("container", "MPEG-4"))
                pipedStream.put("quality", stream.optString("qualityLabel", stream.optString("quality")))
                pipedStream.put("mimeType", stream.optString("type"))
                val bitrate = stream.optLong("bitrate", 0)
                pipedStream.put("bitrate", bitrate)
                pipedStream.put("videoOnly", false)
                videoStreamsJson.put(pipedStream)
            }
        }
        
        val pipedJson = JSONObject()
        pipedJson.put("title", title)
        pipedJson.put("description", description)
        pipedJson.put("uploader", uploader)
        pipedJson.put("uploaderAvatar", "")
        pipedJson.put("views", views)
        pipedJson.put("duration", duration)
        pipedJson.put("thumbnailUrl", thumbnailUrl)
        pipedJson.put("hls", "")
        pipedJson.put("audioStreams", JSONArray())
        pipedJson.put("videoStreams", videoStreamsJson)
        
        return pipedJson.toString()
    }
}
