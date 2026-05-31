package com.example.repository

import com.example.db.WatchHistoryDao
import com.example.db.WatchHistoryEntity
import com.example.model.PipedVideoInfo
import com.example.model.TrendingVideo
import com.example.network.PipedApi
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val pipedApi: PipedApi,
    private val watchHistoryDao: WatchHistoryDao
) {
    suspend fun getTrending(): List<TrendingVideo> {
        return pipedApi.getTrending()
    }

    suspend fun getVideoStreams(videoId: String): PipedVideoInfo {
        return pipedApi.getStreams(videoId)
    }

    fun getWatchHistory(): Flow<List<WatchHistoryEntity>> {
        return watchHistoryDao.getAllHistory()
    }

    suspend fun addToHistory(video: WatchHistoryEntity) {
        watchHistoryDao.insertHistory(video)
    }
}
