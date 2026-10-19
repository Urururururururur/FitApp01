package com.practicum.fitapp.data.local

enum class Gender(val displayName: String) {
    MALE("Мужской"),
    FEMALE("Женский")
}

enum class ActivityLevel(val displayName: String) {
    SEDENTARY("Сидячий (нет тренировок)"),
    LIGHT("Лёгкая активность (1-2 тренировки)"),
    MODERATE("Средняя (3-4 тренировки)"),
    ACTIVE("Высокая (5-6 тренировок)"),
    VERY_ACTIVE("Очень высокая (ежедневно)")
}

enum class FitnessGoal(val displayName: String) {
    LOSE("Похудение"),
    MAINTAIN("Поддержание"),
    GAIN("Набор массы")
}