package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PipedVideoInfo(
    val title: String,
    val description: String?,
    val uploader: String,
    val uploaderAvatar: String?,
    val views: Long? = 0,
    val duration: Long? = 0,
    val thumbnailUrl: String,
    val hls: String?,
    val audioStreams: List<PipedStream>?,
    val videoStreams: List<PipedStream>?
)

@JsonClass(generateAdapter = true)
data class PipedStream(
    val url: String,
    val format: String,
    val quality: String,
    @Json(name = "mimeType") val mimeType: String?,
    val bitrate: Long?,
    val videoOnly: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class TrendingVideo(
    val url: String,
    val type: String,
    val title: String,
    val thumbnailUrl: String,
    val uploaderName: String,
    val uploaderUrl: String,
    val uploaderAvatar: String?,
    val views: Long? = 0,
    val duration: Long? = 0
)

@JsonClass(generateAdapter = true)
data class SearchResult(
    val items: List<SearchItem>,
    val nextpage: String?
)

@JsonClass(generateAdapter = true)
data class SearchItem(
    val url: String,
    val type: String, // "stream", "channel", "playlist"
    val title: String,
    val thumbnailUrl: String,
    val uploaderName: String?,
    val uploaderUrl: String?,
    val uploaderAvatar: String?,
    val views: Long?,
    val duration: Long?,
    val subscribers: Long?,
    val description: String?,
    val videos: Long?
)

@JsonClass(generateAdapter = true)
data class ChannelInfo(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val bannerUrl: String?,
    val description: String,
    val subscriberCount: Long,
    val nextpage: String?,
    val relatedStreams: List<TrendingVideo>
)
