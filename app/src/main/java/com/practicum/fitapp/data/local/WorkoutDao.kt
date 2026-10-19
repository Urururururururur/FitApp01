package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    /** Все тренировки за конкретный день. */
    @Query("""
        SELECT * FROM workouts 
        WHERE date >= :startOfDay AND date < :endOfDay 
        ORDER BY date DESC
    """)
    fun getWorkoutsByDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutEntity>>

    /** Вставка тренировки. Возвращает id новой записи. */
    @Insert
    suspend fun insert(workout: WorkoutEntity): Long

    /** Обновить тренировку. */
    @Query("""
    UPDATE workouts 
    SET name = :name, note = :note, endDate = :endDate, status = :status
    WHERE id = :id
""")
    suspend fun update(
        id: Long,
        name: String,
        note: String,
        endDate: Long?,
        status: String
    )

    /** Удалить тренировку по id (каскадно удалит упражнения и подходы). */
    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Список всех дат, в которые есть тренировки. Для подсветки календаря. */
    @Query("SELECT DISTINCT date FROM workouts ORDER BY date DESC")
    fun getAllWorkoutDates(): Flow<List<Long>>

    /** Одна тренировка по id. */
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getById(id: Long): Flow<WorkoutEntity?>

    /**
     * Уникальные группы мышц для тренировки.
     * Возвращает строки вида "LEGS", "BACK" — по одному значению на строку,
     * потому что мы парсим "LEGS,BACK" через UNION после SELECT.
     * На MVP проще: возвращаем muscleGroups как строки, парсим в Kotlin.
     */
    @Query("""
        SELECT DISTINCT e.muscleGroups
        FROM workout_exercises we
        JOIN exercises e ON e.id = we.exerciseId
        WHERE we.workoutId = :workoutId
    """)
    fun getMuscleGroupsRaw(workoutId: Long): Flow<List<String>>
}