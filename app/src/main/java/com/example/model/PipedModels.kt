package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PipedVideoInfo(
    val title: String = "",
    val description: String = "",
    val uploader: String = "",
    val uploaderAvatar: String = "",
    val views: Long = 0,
    val duration: Long = 0,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String = "",
    val hls: String = "",
    val audioStreams: List<PipedStream> = emptyList(),
    val videoStreams: List<PipedStream> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PipedStream(
    val url: String = "",
    val format: String = "",
    val quality: String = "",
    @Json(name = "mimeType") val mimeType: String = "",
    val bitrate: Long = 0,
    val videoOnly: Boolean = false
)

@JsonClass(generateAdapter = true)
data class TrendingVideo(
    val url: String = "",
    val type: String = "",
    val title: String = "",
    @Json(name = "thumbnail") val thumbnail: String = "",
    val uploaderName: String = "",
    val uploaderUrl: String = "",
    val uploaderAvatar: String = "",
    val views: Long = 0,
    val duration: Long = 0
) {
    val thumbnailUrl: String get() = thumbnail
}

@JsonClass(generateAdapter = true)
data class SearchResult(
    val items: List<SearchItem> = emptyList(),
    val nextpage: String = ""
)

@JsonClass(generateAdapter = true)
data class SearchItem(
    val url: String = "",
    val type: String = "", // "stream", "channel", "playlist"
    val title: String = "",
    val name: String = "",
    @Json(name = "thumbnail") val thumbnail: String = "",
    val uploaderName: String = "",
    val uploaderUrl: String = "",
    val uploaderAvatar: String = "",
    val views: Long = 0,
    val duration: Long = 0,
    val subscribers: Long = 0,
    val description: String = "",
    val videos: Long = 0
) {
    val displayTitle: String get() = title.ifEmpty { name }
    val thumbnailUrl: String get() = thumbnail.ifEmpty { uploaderAvatar }
}

@JsonClass(generateAdapter = true)
data class ChannelInfo(
    val id: String = "",
    val name: String = "",
    @Json(name = "avatarUrl") val avatarUrl: String = "",
    @Json(name = "bannerUrl") val bannerUrl: String = "",
    val description: String = "",
    val subscriberCount: Long = 0,
    val nextpage: String = "",
    val relatedStreams: List<TrendingVideo> = emptyList()
)
