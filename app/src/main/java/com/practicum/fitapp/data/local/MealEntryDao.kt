package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealEntryDao {

    /** Все записи за конкретный день (date — timestamp начала дня). */
    @Query("SELECT * FROM meal_entries WHERE date = :date ORDER BY id DESC")
    fun getByDate(date: Long): Flow<List<MealEntryEntity>>

    /** Список всех дат, в которые были записи. */
    @Query("SELECT DISTINCT date FROM meal_entries ORDER BY date DESC")
    fun getAllDates(): Flow<List<Long>>

    /** Вставить запись. */
    @Insert
    suspend fun insert(entry: MealEntryEntity): Long

    /** Удалить запись по id. */
    @Query("DELETE FROM meal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}