package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaw(item: HistoryItem): Long

    @Query("DELETE FROM calculation_history WHERE id NOT IN (SELECT id FROM calculation_history ORDER BY timestamp DESC LIMIT 50)")
    suspend fun trimHistory()

    @Transaction
    suspend fun insertHistoryWithTrim(item: HistoryItem) {
        insertRaw(item)
        trimHistory()
    }

    @Query("DELETE FROM calculation_history")
    suspend fun clearHistory()
}
