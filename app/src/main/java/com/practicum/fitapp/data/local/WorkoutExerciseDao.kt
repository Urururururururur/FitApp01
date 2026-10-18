package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {
    @Query("""
    SELECT we.id AS workoutExerciseId, e.*
    FROM workout_exercises we
    JOIN exercises e ON e.id = we.exerciseId
    WHERE we.workoutId = :workoutId
    ORDER BY we.orderInWorkout
""")
    fun getExercisesWithId(workoutId: Long): Flow<List<WorkoutExerciseWithExercise>>
    /** Все упражнения тренировки, отсортированные по порядку. */
    @Query("""
        SELECT * FROM workout_exercises 
        WHERE workoutId = :workoutId 
        ORDER BY orderInWorkout
    """)
    fun getByWorkout(workoutId: Long): Flow<List<WorkoutExerciseEntity>>

    /** Получить информацию об упражнении с раскрытием данных из справочника. */
    @Query("""
        SELECT e.* FROM exercises e
        JOIN workout_exercises we ON we.exerciseId = e.id
        WHERE we.workoutId = :workoutId
        ORDER BY we.orderInWorkout
    """)



    fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseEntity>>

    /** Вставить. Возвращает id. */
    @Insert
    suspend fun insert(workoutExercise: WorkoutExerciseEntity): Long

    /** Обновить заметку и порядок. */
    @Query("""
        UPDATE workout_exercises 
        SET note = :note, orderInWorkout = :order
        WHERE id = :id
    """)
    suspend fun update(id: Long, note: String, order: Int)

    /** Удалить по id. */
    @Query("DELETE FROM workout_exercises WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Найти следующий свободный порядок внутри тренировки. */
    @Query("SELECT COALESCE(MAX(orderInWorkout), -1) + 1 FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun nextOrder(workoutId: Long): Int
    @Query("SELECT * FROM workout_exercises WHERE id = :id")
    fun getById(id: Long): Flow<WorkoutExerciseEntity?>
}