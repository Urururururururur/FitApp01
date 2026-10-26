package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("""
        SELECT * FROM workouts 
        WHERE date >= :startOfDay AND date < :endOfDay 
        ORDER BY date DESC
    """)
    fun getWorkoutsByDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutEntity>>

    @Insert
    suspend fun insert(workout: WorkoutEntity): Long

    @Query("""
    UPDATE workouts 
    SET date = :date, name = :name, note = :note, endDate = :endDate, status = :status
    WHERE id = :id
""")
    suspend fun update(
        id: Long,
        date: Long, // ← Добавили дату
        name: String,
        note: String,
        endDate: Long?,
        status: String
    )

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT DISTINCT date FROM workouts ORDER BY date DESC")
    fun getAllWorkoutDates(): Flow<List<Long>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getById(id: Long): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("""
        SELECT DISTINCT e.muscleGroups
        FROM workout_exercises we
        JOIN exercises e ON e.id = we.exerciseId
        WHERE we.workoutId = :workoutId
    """)
    fun getMuscleGroupsRaw(workoutId: Long): Flow<List<String>>

    @Query("""
        SELECT we.workoutId AS workoutId, e.muscleGroups AS muscleGroups
        FROM workout_exercises we
        JOIN exercises e ON e.id = we.exerciseId
        JOIN workouts w ON w.id = we.workoutId
        WHERE w.date >= :startOfDay AND w.date < :endOfDay
    """)
    fun getMuscleGroupsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutMuscleGroupsRaw>>

    @Query("""
        SELECT 
            we.id AS workoutExerciseId,
            COALESCE(MAX(s.weight), 0.0) AS maxWeight,
            COALESCE(SUM(s.weight * s.reps), 0.0) AS totalWeight,
            COALESCE(SUM(s.reps), 0) AS totalReps,
            COALESCE(SUM(s.distance), 0.0) AS totalDistance,
            COALESCE(SUM(s.duration), 0) AS totalDuration
        FROM workout_exercises we
        LEFT JOIN sets s ON s.workoutExerciseId = we.id
        WHERE we.workoutId = :workoutId
        GROUP BY we.id
    """)
    fun getExerciseStats(workoutId: Long): Flow<List<ExerciseStats>>

    @Query("""
        SELECT 
            (date / 86400000) * 86400000 AS dayTimestamp,
            COUNT(*) AS count
        FROM workouts
        WHERE date >= :startDate AND date < :endDate
        GROUP BY dayTimestamp
        ORDER BY dayTimestamp
    """)
    fun getDailyWorkoutCounts(startDate: Long, endDate: Long): Flow<List<DailyWorkoutCount>>

    @Query("""
        SELECT 
            (w.date / 86400000) * 86400000 AS dayTimestamp,
            COALESCE(MAX(s.weight), 0.0) AS maxWeight,
            COALESCE(SUM(s.reps), 0) AS totalReps,
            COALESCE(SUM(s.duration), 0) AS totalDuration,
            COALESCE(SUM(s.distance), 0.0) AS totalDistance
        FROM sets s
        JOIN workout_exercises we ON we.id = s.workoutExerciseId
        JOIN workouts w ON w.id = we.workoutId
        WHERE we.exerciseId = :exerciseId
        GROUP BY dayTimestamp
        ORDER BY dayTimestamp ASC
    """)
    fun getExerciseProgress(exerciseId: Long): Flow<List<ExerciseProgress>>

    @Query("""
        SELECT DISTINCT e.id, e.name, e.category, COUNT(s.id) as totalSets
        FROM exercises e
        JOIN workout_exercises we ON we.exerciseId = e.id
        JOIN sets s ON s.workoutExerciseId = we.id
        GROUP BY e.id
        HAVING totalSets > 0
        ORDER BY totalSets DESC
    """)
    fun getExercisesWithProgress(): Flow<List<ExerciseWithProgress>>

    @Query("""
        SELECT we.id as workoutExerciseId, we.workoutId, we.exerciseId, we.orderInWorkout, we.note,
               e.id as exerciseId, e.name as exerciseName, e.muscleGroups, e.category, e.isBuiltIn, e.description,
               s.id as setId, s.weight, s.reps, s.distance, s.duration, s.intensity, s.orderInExercise
        FROM workout_exercises we
        JOIN exercises e ON e.id = we.exerciseId
        LEFT JOIN sets s ON s.workoutExerciseId = we.id
        WHERE we.workoutId = :workoutId
        ORDER BY we.orderInWorkout, s.orderInExercise
    """)
    fun getWorkoutWithDetails(workoutId: Long): Flow<List<WorkoutDetailRow>>
}