package com.example.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        WatchHistoryEntity::class,
        SearchHistoryEntity::class,
        SubscriptionEntity::class,
        PlaylistEntity::class,
        PlaylistVideoEntity::class,
        BookmarkEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun bookmarkDao(): BookmarkDao
}
