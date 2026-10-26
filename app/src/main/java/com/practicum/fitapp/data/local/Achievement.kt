package com.practicum.fitapp.data.local

/**
 * Список всех достижений FitApp.
 * id — уникальный строковый ключ (для БД).
 */
enum class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String
) {
    FIRST_WORKOUT("first_workout", "Первая тренировка", "Выполните первую тренировку", "🏋️"),
    TEN_WORKOUTS("ten_workouts", "10 тренировок", "Выполните 10 тренировок", "🔥"),
    FIFTY_WORKOUTS("fifty_workouts", "50 тренировок", "Выполните 50 тренировок", "💪"),

    FIRST_TON("first_ton", "Первая тонна", "Поднимите суммарно 1000 кг", "⚖️"),
    TEN_TONS("ten_tons", "10 тонн", "Поднимите суммарно 10 000 кг", "🏗️"),

    FIRST_PR("first_pr", "Первый рекорд", "Установите личный рекорд в упражнении", "🏆"),

    FIRST_5KM("first_5km", "Первые 5 км", "Пройдите или пробегите 5 км суммарно", "🏃"),
    FIRST_10KM("first_10km", "Первые 10 км", "Пройдите или пробегите 10 км суммарно", "🏅"),

    DIARY_7("diary_7", "7 дней дневника", "Заполняйте дневник питания 7 дней", "📓"),
    DIARY_30("diary_30", "30 дней дневника", "Заполняйте дневник питания 30 дней", "📚"),

    CALORIE_BALANCE_5("calorie_balance_5", "Баланс 5/7", "5 из 7 дней в коридоре калорий", "⚖️"),
    PERFECT_DAY("perfect_day", "Идеальный день", "Тренировка + дневник + калории в норме", "🌟"),

    RETURN_AFTER_BREAK("return_after_break", "Возвращение", "Вернитесь к тренировкам после перерыва", "🔄"),

    TEN_EXERCISES("ten_exercises", "10 разных упражнений", "Попробуйте 10 разных упражнений", "🎯");

    companion object {
        fun fromId(id: String): Achievement? = entries.find { it.id == id }
    }
}