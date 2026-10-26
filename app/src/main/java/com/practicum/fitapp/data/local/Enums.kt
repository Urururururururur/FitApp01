package com.practicum.fitapp.data.local

/**
 * Группы мышц. Фиксированный список — не таблица в БД.
 * В Entity хранится как строка имени (например, "LEGS").
 */
enum class MuscleGroup(val displayName: String) {
    SHOULDERS("Плечи"),
    TRAPS("Трапеция"),
    BACK("Спина"),
    LOWER_BACK("Поясница"),
    CHEST("Грудь"),
    ABS("Пресс"),
    BICEPS("Бицепс"),
    TRICEPS("Трицепс"),
    FOREARMS("Предплечья"),
    QUADS("Квадрицепс"),
    GLUTES("Ягодицы"),
    HAMSTRINGS("Бицепс бедра"),
    ADDUCTORS("Приводящие бедра"),
    CALVES("Икры"),
    CARDIO("Кардио");

    companion object {
        fun fromString(value: String): MuscleGroup? =
            entries.find { it.name == value }

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
    CARDIO("Кардио"),
    HOLD("На время")
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
/**
 * Категории продуктов для справочника.
 */
enum class ProductCategory(val id: String, val displayName: String) {
    MEAT("meat", "Мясо"),
    FISH("fish", "Рыба"),
    DAIRY_EGGS("dairy_eggs", "Молочное/Яйца"),
    GRAINS_BREAD("grains_bread", "Крупы/Хлеб"),
    VEGETABLES_MUSHROOMS("vegetables_mushrooms", "Овощи/Грибы"),
    FRUITS_BERRIES("fruits_berries", "Фрукты/Ягоды"),
    FATS_NUTS("fats_nuts", "Жиры/Орехи"),
    SWEETS("sweets", "Сладкое"),
    OTHER("other", "Прочее");

    companion object {
        fun fromId(id: String): ProductCategory = entries.find { it.id == id } ?: OTHER
    }
}