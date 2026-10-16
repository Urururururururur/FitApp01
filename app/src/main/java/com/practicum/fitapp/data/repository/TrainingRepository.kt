package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.ExerciseDao
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.SetDao
import com.practicum.fitapp.data.local.SetEntity
import com.practicum.fitapp.data.local.WorkoutDao
import com.practicum.fitapp.data.local.WorkoutEntity
import com.practicum.fitapp.data.local.WorkoutExerciseDao
import com.practicum.fitapp.data.local.WorkoutExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository для раздела «Тренировки».
 * Объединяет работу с тренировками, упражнениями в тренировке и подходами.
 */
class TrainingRepository(
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val setDao: SetDao,
    private val exerciseDao: ExerciseDao
) {

    // ==================== Тренировки ====================

    fun getWorkoutsByDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutEntity>> =
        workoutDao.getWorkoutsByDay(startOfDay, endOfDay)

    fun getWorkoutById(id: Long): Flow<WorkoutEntity?> = workoutDao.getById(id)

    fun getAllWorkoutDates(): Flow<List<Long>> = workoutDao.getAllWorkoutDates()

    suspend fun insertWorkout(workout: WorkoutEntity): Long = workoutDao.insert(workout)

    suspend fun updateWorkout(
        id: Long,
        name: String,
        note: String,
        endDate: Long?,
        isCompleted: Boolean
    ) = workoutDao.update(id, name, note, endDate, isCompleted)

    suspend fun deleteWorkout(id: Long) = workoutDao.deleteById(id)

    /** Список "сырых" строк групп мышц — для дальнейшего парсинга в Kotlin. */
    fun getMuscleGroupsRaw(workoutId: Long): Flow<List<String>> =
        workoutDao.getMuscleGroupsRaw(workoutId)

    // ==================== Упражнения в тренировке ====================

    fun getWorkoutExercises(workoutId: Long): Flow<List<WorkoutExerciseEntity>> =
        workoutExerciseDao.getByWorkout(workoutId)

    fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseEntity>> =
        workoutExerciseDao.getExercisesForWorkout(workoutId)

    suspend fun insertWorkoutExercise(entity: WorkoutExerciseEntity): Long =
        workoutExerciseDao.insert(entity)

    suspend fun nextWorkoutExerciseOrder(workoutId: Long): Int =
        workoutExerciseDao.nextOrder(workoutId)

    suspend fun updateWorkoutExercise(id: Long, note: String, order: Int) =
        workoutExerciseDao.update(id, note, order)

    suspend fun deleteWorkoutExercise(id: Long) = workoutExerciseDao.deleteById(id)

    // ==================== Подходы ====================

    fun getSets(workoutExerciseId: Long): Flow<List<SetEntity>> =
        setDao.getByWorkoutExercise(workoutExerciseId)

    suspend fun insertSet(set: SetEntity): Long = setDao.insert(set)

    suspend fun updateSet(
        id: Long,
        weight: Double?,
        reps: Int?,
        distance: Double?,
        duration: Int?,
        intensity: String
    ) = setDao.update(id, weight, reps, distance, duration, intensity)

    suspend fun deleteSet(id: Long) = setDao.deleteById(id)

    suspend fun nextSetOrder(workoutExerciseId: Long): Int =
        setDao.nextOrder(workoutExerciseId)

    // ==================== Справочник упражнений ====================

    fun getAllExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getAll()

    fun getBuiltInExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getBuiltIn()

    fun getUserExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getUserCreated()

    suspend fun countBuiltInExercises(): Int = exerciseDao.countBuiltIn()

    suspend fun insertExercises(exercises: List<ExerciseEntity>) = exerciseDao.insertAll(exercises)

    suspend fun insertExercise(exercise: ExerciseEntity): Long = exerciseDao.insert(exercise)

    suspend fun deleteUserExercise(id: Long) = exerciseDao.deleteUserCreated(id)

}
