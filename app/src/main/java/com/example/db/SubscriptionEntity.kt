package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey
    val channelId: String,
    val name: String,
    val avatarUrl: String,
    val isVerified: Boolean = false,
    val groupName: String? = null
)
