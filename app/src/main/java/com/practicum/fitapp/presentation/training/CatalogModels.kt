package com.practicum.fitapp.presentation.training

/** Модель упражнения в каталоге. */
data class CatalogExercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val rest: String,
    val note: String? = null
)

/** Модель дня в каталоге. */
data class CatalogDay(
    val day: String,        // "День 1"
    val title: String,      // "Грудь + трицепс"
    val exercises: List<CatalogExercise>
)

/** Модель программы тренировок. */
data class CatalogProgram(
    val id: String,
    val title: String,
    val subtitle: String,
    val level: String,      // "Новичок", "Средний", "Продвинутый"
    val gender: String,     // "Муж", "Жен", "Унисекс"
    val goal: String,       // "Масса", "Похудение", "Выносливость", "Форма"
    val tags: List<String>,
    val description: String,
    val days: List<CatalogDay>
)