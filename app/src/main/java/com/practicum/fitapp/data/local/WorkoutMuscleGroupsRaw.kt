package com.practicum.fitapp.data.local

/**
 * POJO для запроса групп мышц по тренировкам.
 * workoutId — к какой тренировке относится строка.
 * muscleGroups — строка вида "LEGS,BACK".
 */
data class WorkoutMuscleGroupsRaw(
    val workoutId: Long,
    val muscleGroups: String
)