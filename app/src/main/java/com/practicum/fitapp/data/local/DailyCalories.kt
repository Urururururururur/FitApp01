package com.practicum.fitapp.data.local

/**
 * POJO для агрегации калорий по дню.
 * dayTimestamp — timestamp начала дня (00:00:00).
 * totalCalories — сумма калорий за этот день.
 */
data class DailyCalories(
    val dayTimestamp: Long,
    val totalCalories: Double
)