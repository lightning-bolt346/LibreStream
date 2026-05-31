package com.example.db

import androidx.room.Entity

@Entity(tableName = "playlist_videos", primaryKeys = ["playlistId", "videoId"])
data class PlaylistVideoEntity(
    val playlistId: String,
    val videoId: String,
    val position: Int
)
