package com.example.repository

import com.example.db.BookmarkDao
import com.example.db.BookmarkEntity
import com.example.db.PlaylistDao
import com.example.db.PlaylistEntity
import com.example.db.PlaylistVideoEntity
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val bookmarkDao: BookmarkDao
) {
    // Playlists
    fun getAllPlaylists(): Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()
    
    suspend fun createPlaylist(name: String, isPrivate: Boolean) {
        val id = java.util.UUID.randomUUID().toString()
        playlistDao.insertPlaylist(PlaylistEntity(id, name, isPrivate))
    }
    
    suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }
    
    fun getPlaylistVideos(playlistId: String): Flow<List<PlaylistVideoEntity>> = playlistDao.getPlaylistVideos(playlistId)
    
    suspend fun addVideoToPlaylist(playlistId: String, videoId: String) {
        // we can find position by counting existing
        playlistDao.insertVideoToPlaylist(PlaylistVideoEntity(playlistId, videoId, 0))
    }
    
    suspend fun removeVideoFromPlaylist(playlistId: String, videoId: String) {
        playlistDao.removeVideoFromPlaylist(playlistId, videoId)
    }

    // Bookmarks
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
    
    fun getBookmarkStatus(videoId: String): Flow<BookmarkEntity?> = bookmarkDao.getBookmarkFlow(videoId)

    suspend fun addBookmark(videoId: String) {
        bookmarkDao.insertBookmark(BookmarkEntity(videoId))
    }

    suspend fun removeBookmark(videoId: String) {
        bookmarkDao.deleteBookmark(videoId)
    }
}
