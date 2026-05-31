package com.example.repository

import com.example.model.ChannelInfo
import com.example.network.PipedApi

class ChannelRepository(
    private val pipedApi: PipedApi
) {
    suspend fun getChannel(channelId: String): ChannelInfo {
        return pipedApi.getChannel(channelId)
    }
}
