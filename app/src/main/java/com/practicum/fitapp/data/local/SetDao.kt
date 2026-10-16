package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {

    /** Все подходы конкретного упражнения в тренировке. */
    @Query("""
        SELECT * FROM sets 
        WHERE workoutExerciseId = :workoutExerciseId 
        ORDER BY orderInExercise
    """)
    fun getByWorkoutExercise(workoutExerciseId: Long): Flow<List<SetEntity>>

    @Insert
    suspend fun insert(set: SetEntity): Long

    @Query("""
        UPDATE sets 
        SET weight = :weight, reps = :reps, distance = :distance, 
            duration = :duration, intensity = :intensity
        WHERE id = :id
    """)
    suspend fun update(
        id: Long,
        weight: Double?,
        reps: Int?,
        distance: Double?,
        duration: Int?,
        intensity: String
    )

    @Query("DELETE FROM sets WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Следующий свободный порядок внутри упражнения. */
    @Query("SELECT COALESCE(MAX(orderInExercise), -1) + 1 FROM sets WHERE workoutExerciseId = :weId")
    suspend fun nextOrder(weId: Long): Int
}