package com.practicum.fitapp.presentation.training

import com.practicum.fitapp.data.local.ExerciseCategory
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.MuscleGroup

/**
 * Стартовый набор упражнений, который вставляется в БД при первом запуске.
 * Все упражнения — встроенные (isBuiltIn = true), их нельзя удалить.
 */
val builtInExercises: List<ExerciseEntity> = listOf(

    // ==== Ноги ====
    ExerciseEntity(
        name = "Приседания со штангой",
        muscleGroups = "${MuscleGroup.LEGS.name},${MuscleGroup.ABS.name}",
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Классическое базовое упражнение для ног и ягодиц."
    ),
    ExerciseEntity(
        name = "Жим ногами в тренажёре",
        muscleGroups = MuscleGroup.LEGS.name,
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Тренажёр для проработки квадрицепсов и ягодиц."
    ),
    ExerciseEntity(
        name = "Выпады с гантелями",
        muscleGroups = "${MuscleGroup.LEGS.name},${MuscleGroup.ABS.name}",
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Упражнение на ноги и баланс, с гантелями в руках."
    ),

    // ==== Спина ====
    ExerciseEntity(
        name = "Становая тяга",
        muscleGroups = "${MuscleGroup.BACK.name},${MuscleGroup.LEGS.name}",
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Базовое упражнение для спины, ягодиц и ног."
    ),
    ExerciseEntity(
        name = "Подтягивания",
        muscleGroups = "${MuscleGroup.BACK.name},${MuscleGroup.ARMS.name}",
        category = ExerciseCategory.BODYWEIGHT.name,
        isBuiltIn = true,
        description = "Подтягивания на перекладине собственным весом."
    ),
    ExerciseEntity(
        name = "Тяга штанги в наклоне",
        muscleGroups = MuscleGroup.BACK.name,
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Тяга штанги к поясу в наклоне корпуса."
    ),

    // ==== Грудь ====
    ExerciseEntity(
        name = "Жим штанги лёжа",
        muscleGroups = "${MuscleGroup.CHEST.name},${MuscleGroup.ARMS.name}",
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Классический жим лёжа на горизонтальной скамье."
    ),
    ExerciseEntity(
        name = "Отжимания от пола",
        muscleGroups = "${MuscleGroup.CHEST.name},${MuscleGroup.ARMS.name}",
        category = ExerciseCategory.BODYWEIGHT.name,
        isBuiltIn = true,
        description = "Отжимания собственным весом."
    ),
    ExerciseEntity(
        name = "Разводка гантелей лёжа",
        muscleGroups = MuscleGroup.CHEST.name,
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Изолирующее упражнение для грудных мышц."
    ),

    // ==== Плечи ====
    ExerciseEntity(
        name = "Жим гантелей сидя",
        muscleGroups = MuscleGroup.SHOULDERS.name,
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Жим гантелей вверх, сидя на скамье."
    ),
    ExerciseEntity(
        name = "Махи гантелями в стороны",
        muscleGroups = MuscleGroup.SHOULDERS.name,
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Изолирующее упражнение на средние дельты."
    ),

    // ==== Руки ====
    ExerciseEntity(
        name = "Сгибание рук со штангой",
        muscleGroups = MuscleGroup.ARMS.name,
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Бицепс, штанга или EZ-гриф."
    ),
    ExerciseEntity(
        name = "Французский жим лёжа",
        muscleGroups = MuscleGroup.ARMS.name,
        category = ExerciseCategory.STRENGTH.name,
        isBuiltIn = true,
        description = "Упражнение на трицепс с гантелью или штангой."
    ),

    // ==== Пресс ====
    ExerciseEntity(
        name = "Планка",
        muscleGroups = MuscleGroup.ABS.name,
        category = ExerciseCategory.HOLD.name,
        isBuiltIn = true,
        description = "Статическое упражнение на пресс. Засекай время."
    ),
    ExerciseEntity(
        name = "Скручивания",
        muscleGroups = MuscleGroup.ABS.name,
        category = ExerciseCategory.BODYWEIGHT.name,
        isBuiltIn = true,
        description = "Классические скручивания на пресс."
    ),

    // ==== Кардио ====
    ExerciseEntity(
        name = "Беговая дорожка",
        muscleGroups = MuscleGroup.CARDIO.name,
        category = ExerciseCategory.CARDIO.name,
        isBuiltIn = true,
        description = "Бег или ходьба. Указывай дистанцию и время."
    ),
    ExerciseEntity(
        name = "Велотренажёр",
        muscleGroups = MuscleGroup.CARDIO.name,
        category = ExerciseCategory.CARDIO.name,
        isBuiltIn = true,
        description = "Кардио на велотренажёре. Дистанция и время."
    ),
    ExerciseEntity(
        name = "Скакалка",
        muscleGroups = MuscleGroup.CARDIO.name,
        category = ExerciseCategory.CARDIO.name,
        isBuiltIn = true,
        description = "Прыжки на скакалке. Указывай время."
    )
)