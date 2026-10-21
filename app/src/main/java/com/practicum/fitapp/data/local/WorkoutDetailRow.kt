package com.practicum.fitapp.data.local

/**
 * POJO для полной тренировки (для копирования).
 */
data class WorkoutDetailRow(
    val workoutExerciseId: Long,
    val workoutId: Long,
    val exerciseId: Long,
    val orderInWorkout: Int,
    val note: String,
    val exerciseName: String,
    val muscleGroups: String,
    val category: String,
    val isBuiltIn: Boolean,
    val description: String,
    val setId: Long?,
    val weight: Double?,
    val reps: Int?,
    val distance: Double?,
    val duration: Int?,
    val intensity: String?,
    val orderInExercise: Int?
)