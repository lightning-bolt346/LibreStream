package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val videoId: String,
    val timestampMs: Long = System.currentTimeMillis()
)
