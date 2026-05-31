package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey
    val videoId: String,
    val title: String,
    val uploaderName: String,
    val thumbnailUrl: String,
    val timestampMs: Long = System.currentTimeMillis()
)
