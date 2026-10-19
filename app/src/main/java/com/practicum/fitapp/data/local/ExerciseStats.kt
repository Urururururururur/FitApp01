package com.practicum.fitapp.data.local

import androidx.room.Embedded

/**
 * Статистика по одному упражнению в тренировке.
 */
data class ExerciseStats(
    val workoutExerciseId: Long,
    val maxWeight: Double,
    val totalWeight: Double,
    val totalReps: Int
)