package com.example.repository

import com.example.db.SearchHistoryDao
import com.example.db.SearchHistoryEntity
import com.example.model.SearchResult
import com.example.network.PipedApi
import kotlinx.coroutines.flow.Flow

class SearchRepository(
    private val pipedApi: PipedApi,
    private val searchHistoryDao: SearchHistoryDao
) {
    suspend fun search(query: String, filter: String = "all"): SearchResult {
        return pipedApi.search(query, filter)
    }

    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return searchHistoryDao.getSearchHistory()
    }

    suspend fun addSearchHistory(query: String) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertQuery(SearchHistoryEntity(query))
        }
    }

    suspend fun deleteSearchHistory(query: String) {
        searchHistoryDao.deleteQuery(query)
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearHistory()
    }
}
