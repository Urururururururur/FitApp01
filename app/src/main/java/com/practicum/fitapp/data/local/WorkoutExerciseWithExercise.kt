package com.practicum.fitapp.data.local

import androidx.room.Embedded

/**
 * POJO для JOIN-запроса.
 * Содержит поля из WorkoutExerciseEntity и ExerciseEntity.
 */
data class WorkoutExerciseWithExercise(
    val workoutExerciseId: Long,
    @Embedded val exercise: ExerciseEntity
)