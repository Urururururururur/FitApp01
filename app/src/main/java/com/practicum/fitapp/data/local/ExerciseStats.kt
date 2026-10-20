package com.practicum.fitapp.data.local

/**
 * Статистика по одному упражнению в тренировке.
 */
data class ExerciseStats(
    val workoutExerciseId: Long,
    val maxWeight: Double,
    val totalWeight: Double,
    val totalReps: Int,
    val totalDistance: Double,
    val totalDuration: Int
)