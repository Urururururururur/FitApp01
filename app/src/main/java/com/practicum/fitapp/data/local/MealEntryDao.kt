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

    /** Сумма калорий за день. Возвращает число или 0, если записей нет. */
    @Query("""
        SELECT COALESCE(SUM(grams * productCalories / 100.0), 0.0)
        FROM meal_entries
        WHERE date = :date
    """)
    fun getTotalCaloriesByDate(date: Long): Flow<Double>

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