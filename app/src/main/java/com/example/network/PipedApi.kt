package com.example.network

import com.example.model.PipedVideoInfo
import com.example.model.TrendingVideo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PipedApi {

    @GET("trending")
    suspend fun getTrending(@Query("region") region: String = "US"): List<TrendingVideo>

    @GET("streams/{videoId}")
    suspend fun getStreams(@Path("videoId") videoId: String): PipedVideoInfo

    @GET("search")
    suspend fun search(@Query("q") query: String, @Query("filter") filter: String = "all"): com.example.model.SearchResult

    @GET("channel/{channelId}")
    suspend fun getChannel(@Path("channelId") channelId: String): com.example.model.ChannelInfo

    @GET("playlists/{playlistId}")
    suspend fun getPlaylistInfo(@Path("playlistId") playlistId: String): com.example.model.ChannelInfo // Reusing ChannelInfo or creating PlaylistInfo if needed

}
