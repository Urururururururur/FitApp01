package com.practicum.fitapp.data.local

/**
 * Группы мышц. Фиксированный список — не таблица в БД.
 * В Entity хранится как строка имени (например, "LEGS").
 */
enum class MuscleGroup(val displayName: String) {
    LEGS("Ноги"),
    BACK("Спина"),
    SHOULDERS("Плечи"),
    CARDIO("Кардио"),
    CHEST("Грудь"),
    ARMS("Руки"),
    ABS("Пресс");

    companion object {
        /** Найти по строковому имени. Возвращает null, если не найдено. */
        fun fromString(value: String): MuscleGroup? =
            entries.find { it.name == value }

        /** Разобрать строку вида "LEGS,BACK" в список групп. */
        fun parseList(value: String): List<MuscleGroup> =
            value.split(",")
                .mapNotNull { fromString(it.trim()) }
    }
}

/**
 * Категория упражнения.
 * STRENGTH — силовые (вес × повторения).
 * BODYWEIGHT — с собственным весом (повторения или время).
 * CARDIO — кардио (дистанция × время).
 */
enum class ExerciseCategory(val displayName: String) {
    STRENGTH("Силовое"),
    BODYWEIGHT("С собственным весом"),
    CARDIO("Кардио")
}

/**
 * Интенсивность подхода.
 */
enum class Intensity(val displayName: String) {
    WARMUP("Разминка"),
    EASY("Легко"),
    NORMAL("Норма"),
    HARD("Сложно")
}