package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestampMs DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE videoId = :videoId LIMIT 1")
    fun getBookmarkFlow(videoId: String): Flow<BookmarkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE videoId = :videoId")
    suspend fun deleteBookmark(videoId: String)
}
