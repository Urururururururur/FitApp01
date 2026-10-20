package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {

    /** Вся история взвешиваний — новые сверху. */
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAll(): Flow<List<WeightEntryEntity>>

    /** Последнее взвешивание (для отображения в профиле). */
    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 1")
    fun getLast(): Flow<WeightEntryEntity?>

    /**
     * Взвешивание, которое было на конкретный момент времени или до него.
     * Используется для расчёта "исторической" нормы.
     */
    @Query("""
        SELECT * FROM weight_entries 
        WHERE date <= :timestamp 
        ORDER BY date DESC 
        LIMIT 1
    """)
    suspend fun getLatestBefore(timestamp: Long): WeightEntryEntity?

    @Insert
    suspend fun insert(entry: WeightEntryEntity): Long
}