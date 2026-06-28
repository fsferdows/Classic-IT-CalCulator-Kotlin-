package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory()

    suspend fun addResult(expression: String, result: String) {
        val item = HistoryItem(expression = expression, result = result)
        historyDao.insertHistoryWithTrim(item)
    }

    suspend fun clearHistory() {
        historyDao.clearHistory()
    }
}
