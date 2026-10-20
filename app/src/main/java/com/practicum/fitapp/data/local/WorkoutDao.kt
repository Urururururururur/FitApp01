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

    /**
     * Все группы мышц по всем тренировкам за интервал [startOfDay, endOfDay).
     * Возвращает пары: (workoutId, muscleGroups) — как строку вида "LEGS,BACK".
     */
    @Query("""
    SELECT we.workoutId AS workoutId, e.muscleGroups AS muscleGroups
    FROM workout_exercises we
    JOIN exercises e ON e.id = we.exerciseId
    JOIN workouts w ON w.id = we.workoutId
    WHERE w.date >= :startOfDay AND w.date < :endOfDay
""")
    fun getMuscleGroupsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutMuscleGroupsRaw>>

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

    /**
     * Тренировки по дням за период.
     * Возвращает список пар (день, количество тренировок).
     */
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




    /**
     * Статистика по каждому упражнению в тренировке.
     * Возвращает: id связи, максимальный вес, суммарный вес,
     * суммарные повторения, суммарную дистанцию, суммарное время.
     */
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

    /**
     * Список всех упражнений, по которым есть хотя бы один подход.
     * Считает количество выполненных подходов, чтобы не показывать пустые упражнения.
     */
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

    /**
     * Прогресс по упражнению: для каждого дня, когда оно выполнялось,
     * возвращает метрики (макс вес, сумма повторов, сумма времени).
     */
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
}