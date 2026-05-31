package com.example.repository

import com.example.db.SubscriptionDao
import com.example.db.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

class SubscriptionRepository(
    private val subscriptionDao: SubscriptionDao
) {
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()
    
    fun getSubscriptionStatus(channelId: String): Flow<SubscriptionEntity?> = subscriptionDao.getSubscriptionFlow(channelId)

    suspend fun subscribe(subscription: SubscriptionEntity) {
        subscriptionDao.insertSubscription(subscription)
    }

    suspend fun unsubscribe(channelId: String) {
        subscriptionDao.getSubscription(channelId)?.let {
            subscriptionDao.deleteSubscription(it)
        }
    }
}
