package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TrainingRepository(
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val setDao: SetDao,
    private val exerciseDao: ExerciseDao
) {

    fun getWorkoutsByDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutEntity>> =
        workoutDao.getWorkoutsByDay(startOfDay, endOfDay)

    fun getWorkoutById(id: Long): Flow<WorkoutEntity?> = workoutDao.getById(id)

    fun getAllWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()

    fun getAllWorkoutDates(): Flow<List<Long>> = workoutDao.getAllWorkoutDates()

    suspend fun insertWorkout(workout: WorkoutEntity): Long = workoutDao.insert(workout)

    suspend fun updateWorkout(
        id: Long,
        name: String,
        note: String,
        endDate: Long?,
        status: String
    ) = workoutDao.update(id, name, note, endDate, status)

    suspend fun deleteWorkout(id: Long) = workoutDao.deleteById(id)

    fun getMuscleGroupsRaw(workoutId: Long): Flow<List<String>> =
        workoutDao.getMuscleGroupsRaw(workoutId)

    fun getMuscleGroupsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkoutMuscleGroupsRaw>> =
        workoutDao.getMuscleGroupsForDay(startOfDay, endOfDay)

    fun getExerciseStats(workoutId: Long): Flow<List<ExerciseStats>> =
        workoutDao.getExerciseStats(workoutId)

    fun getDailyWorkoutCounts(startDate: Long, endDate: Long): Flow<List<DailyWorkoutCount>> =
        workoutDao.getDailyWorkoutCounts(startDate, endDate)

    fun getExercisesWithProgress(): Flow<List<ExerciseWithProgress>> =
        workoutDao.getExercisesWithProgress()

    fun getExerciseProgress(exerciseId: Long): Flow<List<ExerciseProgress>> =
        workoutDao.getExerciseProgress(exerciseId)

    fun getWorkoutExercises(workoutId: Long): Flow<List<WorkoutExerciseEntity>> =
        workoutExerciseDao.getByWorkout(workoutId)

    fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseEntity>> =
        workoutExerciseDao.getExercisesForWorkout(workoutId)

    fun getExercisesWithId(workoutId: Long): Flow<List<WorkoutExerciseWithExercise>> =
        workoutExerciseDao.getExercisesWithId(workoutId)

    fun getWorkoutExerciseById(id: Long): Flow<WorkoutExerciseEntity?> =
        workoutExerciseDao.getById(id)

    suspend fun insertWorkoutExercise(entity: WorkoutExerciseEntity): Long =
        workoutExerciseDao.insert(entity)

    suspend fun nextWorkoutExerciseOrder(workoutId: Long): Int =
        workoutExerciseDao.nextOrder(workoutId)

    suspend fun updateWorkoutExercise(id: Long, note: String, order: Int) =
        workoutExerciseDao.update(id, note, order)

    suspend fun deleteWorkoutExercise(id: Long) = workoutExerciseDao.deleteById(id)

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

    suspend fun nextSetOrder(workoutExerciseId: Long): Int = setDao.nextOrder(workoutExerciseId)

    suspend fun getLastSet(workoutExerciseId: Long): SetEntity? = setDao.getLastSet(workoutExerciseId)

    fun getAllExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getAll()

    fun getBuiltInExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getBuiltIn()

    fun getUserExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getUserCreated()

    suspend fun countBuiltInExercises(): Int = exerciseDao.countBuiltIn()

    suspend fun insertExercises(exercises: List<ExerciseEntity>) = exerciseDao.insertAll(exercises)

    suspend fun insertExercise(exercise: ExerciseEntity): Long = exerciseDao.insert(exercise)

    suspend fun deleteUserExercise(id: Long) = exerciseDao.deleteUserCreated(id)

    fun getExerciseById(id: Long): Flow<ExerciseEntity?> = exerciseDao.getById(id)

    suspend fun copyWorkout(sourceWorkoutId: Long, targetDate: Long) {
        val sourceWorkout = workoutDao.getById(sourceWorkoutId).first() ?: return

        val newWorkoutId = workoutDao.insert(
            WorkoutEntity(
                date = targetDate,
                name = sourceWorkout.name,
                note = sourceWorkout.note,
                status = WorkoutStatus.WAITING.name,
                endDate = null
            )
        )

        val details = workoutDao.getWorkoutWithDetails(sourceWorkoutId).first()
        val grouped = details.groupBy { it.workoutExerciseId }

        grouped.forEach { (_, rows) ->
            val first = rows.first()
            val newWorkoutExerciseId = workoutExerciseDao.insert(
                WorkoutExerciseEntity(
                    workoutId = newWorkoutId,
                    exerciseId = first.exerciseId,
                    orderInWorkout = first.orderInWorkout,
                    note = first.note
                )
            )
            rows.forEach { row ->
                if (row.setId != null) {
                    setDao.insert(
                        SetEntity(
                            workoutExerciseId = newWorkoutExerciseId,
                            weight = row.weight,
                            reps = row.reps,
                            distance = row.distance,
                            duration = row.duration,
                            intensity = row.intensity ?: Intensity.NORMAL.name,
                            orderInExercise = row.orderInExercise ?: 0
                        )
                    )
                }
            }
        }
    }
}