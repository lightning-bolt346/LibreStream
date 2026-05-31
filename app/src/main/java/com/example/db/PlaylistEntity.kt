package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val playlistId: String,
    val name: String,
    val isPrivate: Boolean = false,
    val thumbnail: String? = null,
    val videoCount: Int = 0
)
