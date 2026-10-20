package com.practicum.fitapp.data.local

/**
 * Упражнение + количество подходов (для списка статистики).
 */
data class ExerciseWithProgress(
    val id: Long,
    val name: String,
    val category: String,
    val totalSets: Int
)