package com.practicum.fitapp.presentation.nutrition

/**
 * Сводка нутриентов за день.
 * Готовый результат для отображения в UI.
 */
data class DailySummary(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
    val entriesCount: Int = 0
)