package com.practicum.fitapp.data.local

/**
 * Прогресс упражнения за конкретный день.
 */
data class ExerciseProgress(
    val dayTimestamp: Long,
    val maxWeight: Double,
    val totalReps: Int,
    val totalDuration: Int,
    val totalDistance: Double
)