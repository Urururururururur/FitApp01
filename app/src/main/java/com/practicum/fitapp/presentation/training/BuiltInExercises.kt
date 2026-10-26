package com.practicum.fitapp.presentation.training

import com.practicum.fitapp.data.local.ExerciseCategory
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.MuscleGroup

val builtInExercises: List<ExerciseEntity> = listOf(
    // ==================== ПЛЕЧИ ====================
    ExerciseEntity(name = "Жим Арнольда", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим в блоке на плечи стоя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим в тренажере на плечи", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим гантелей на плечи сидя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим гантелей на плечи стоя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги на плечи сидя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги на плечи сидя в машине Смита", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги на плечи стоя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги на плечи стоя в машине Смита", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги на плечи стоя в машине Смита за голову", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги на плечи стоя за голову", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Жим на плечи", isBuiltIn = true),

    ExerciseEntity(name = "Махи в стороны в кросовере", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Махи в стороны", isBuiltIn = true),
    ExerciseEntity(name = "Махи в стороны в тренажере сидя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Махи в стороны", isBuiltIn = true),
    ExerciseEntity(name = "Махи в стороны в тренажере стоя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Махи в стороны", isBuiltIn = true),
    ExerciseEntity(name = "Махи гантелей в стороны сидя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Махи в стороны", isBuiltIn = true),
    ExerciseEntity(name = "Махи гантелей в стороны стоя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Махи в стороны", isBuiltIn = true),
    ExerciseEntity(name = "Махи одной гантелей в сторону на наклонной скамье", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Махи в стороны", isBuiltIn = true),

    ExerciseEntity(name = "Махи гантелей назад в наклоне", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Отведения назад", isBuiltIn = true),
    ExerciseEntity(name = "Махи гантелей назад на наклонной скамье", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Отведения назад", isBuiltIn = true),
    ExerciseEntity(name = "Махи гантелей назад на скамье", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Отведения назад", isBuiltIn = true),
    ExerciseEntity(name = "Махи одной рукой назад в нижнем блоке", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Отведения назад", isBuiltIn = true),
    ExerciseEntity(name = "Отведения рук назад в кросовере", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Отведения назад", isBuiltIn = true),
    ExerciseEntity(name = "Отведения рук назад в тренажере", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Отведения назад", isBuiltIn = true),
    ExerciseEntity(name = "Тяга к лицу стоя в блоке", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Отведения назад", isBuiltIn = true),

    ExerciseEntity(name = "Подъем блина перед собой стоя", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Подъем перед собой", isBuiltIn = true),
    ExerciseEntity(name = "Подъем гантелей перед собой", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Подъем перед собой", isBuiltIn = true),
    ExerciseEntity(name = "Подъем рук перед собой в нижнем блоке", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Подъем перед собой", isBuiltIn = true),
    ExerciseEntity(name = "Подъем штанги перед собой", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Подъем перед собой", isBuiltIn = true),

    ExerciseEntity(name = "Тяга гантелей к подбородку", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Тяга к подбородку", isBuiltIn = true),
    ExerciseEntity(name = "Тяга нижнего блока к подбородку", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Тяга к подбородку", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги к подбородку", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Тяга к подбородку", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги к подбородку в машине Смита", muscleGroups = MuscleGroup.SHOULDERS.name, subcategory = "Тяга к подбородку", isBuiltIn = true),

    // ==================== ТРАПЕЦИЯ ====================
    ExerciseEntity(name = "Шраги в нижнем блоке", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги в нижнем блоке", isBuiltIn = true),
    ExerciseEntity(name = "Шраги в тренажере", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Шраги с гантелями", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Шраги с гантелями лежа на наклонной скамье", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Шраги с гантелями сидя на наклонной скамье", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Шраги в машине Смита", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Шраги в машине Смита за спиной", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Шраги со штангой", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Шраги со штангой за спиной", muscleGroups = MuscleGroup.TRAPS.name, subcategory = "Шраги со штангой", isBuiltIn = true),

    // ==================== СПИНА ====================
    ExerciseEntity(name = "Подтягивания в гравитроне параллельным хватом", muscleGroups = MuscleGroup.BACK.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подтягивания", isBuiltIn = true),
    ExerciseEntity(name = "Подтягивания в гравитроне узким хватом", muscleGroups = MuscleGroup.BACK.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подтягивания", isBuiltIn = true),
    ExerciseEntity(name = "Подтягивания в гравитроне широким хватом", muscleGroups = MuscleGroup.BACK.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подтягивания", isBuiltIn = true),
    ExerciseEntity(name = "Подтягивания обратным хватом", muscleGroups = MuscleGroup.BACK.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подтягивания", isBuiltIn = true),
    ExerciseEntity(name = "Подтягивания параллельным хватом", muscleGroups = MuscleGroup.BACK.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подтягивания", isBuiltIn = true),
    ExerciseEntity(name = "Подтягивания узким хватом", muscleGroups = MuscleGroup.BACK.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подтягивания", isBuiltIn = true),
    ExerciseEntity(name = "Подтягивания широким хватом", muscleGroups = MuscleGroup.BACK.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подтягивания", isBuiltIn = true),

    ExerciseEntity(name = "Пулловер в тренажере", muscleGroups = MuscleGroup.BACK.name, subcategory = "Пулловер", isBuiltIn = true),
    ExerciseEntity(name = "Пулловер на верхнем блоке", muscleGroups = MuscleGroup.BACK.name, subcategory = "Пулловер", isBuiltIn = true),
    ExerciseEntity(name = "Пулловер на нижнем блоке", muscleGroups = MuscleGroup.BACK.name, subcategory = "Пулловер", isBuiltIn = true),
    ExerciseEntity(name = "Пулловер с гантелей на ровной скамье", muscleGroups = MuscleGroup.BACK.name, subcategory = "Пулловер", isBuiltIn = true),

    ExerciseEntity(name = "Тяга в тренажере вертикально", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга в тренажере вертикально", isBuiltIn = true),
    ExerciseEntity(name = "Тяга в тренажере вертикально обратным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга в тренажере вертикально", isBuiltIn = true),
    ExerciseEntity(name = "Тяга в тренажере вертикально одной рукой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга в тренажере вертикально", isBuiltIn = true),

    ExerciseEntity(name = "Тяга в тренажере горизонтально", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга в тренажере горизонтально", isBuiltIn = true),
    ExerciseEntity(name = "Тяга в тренажере горизонтально одной рукой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга в тренажере горизонтально", isBuiltIn = true),
    ExerciseEntity(name = "Тяга в тренажере горизонтально параллельным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга в тренажере горизонтально", isBuiltIn = true),

    ExerciseEntity(name = "Тяга верхнего блока за голову", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга верхнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга верхнего блока обратным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга верхнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга верхнего блока одной рукой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга верхнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга верхнего блока с V-образной рукояткой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга верхнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга верхнего блока узким хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга верхнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга верхнего блока широким хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга верхнего блока", isBuiltIn = true),

    ExerciseEntity(name = "Тяга гантелей к поясу", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга гантели к поясу одной рукой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга к поясу в тренажере", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга к поясу в тренажере одной рукой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга нижнего блока к поясу", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга нижнего блока к поясу обратным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги к поясу", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги к поясу в машине Смита", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги к поясу в машине Смита обратным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги к поясу обратным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга к поясу", isBuiltIn = true),

    ExerciseEntity(name = "Тяга блока лежа на наклонной скамье", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга на скамье", isBuiltIn = true),
    ExerciseEntity(name = "Тяга гантелей лежа на наклонной скамье", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга на скамье", isBuiltIn = true),
    ExerciseEntity(name = "Тяга гантелей лежа на наклонной скамье одной рукой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга на скамье", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги лежа на наклонной скамье", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга на скамье", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги лежа на ровной скамье", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга на скамье", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги обратным хватом лежа на наклонной скамье", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга на скамье", isBuiltIn = true),
    ExerciseEntity(name = "Тяга штанги обратным хватом лежа на ровной скамье", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга на скамье", isBuiltIn = true),

    ExerciseEntity(name = "Тяга нижнего блока с V-образной рукояткой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга нижнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга нижнего блока обратным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга нижнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга нижнего блока одной рукой", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга нижнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга нижнего блока узким хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга нижнего блока", isBuiltIn = true),
    ExerciseEntity(name = "Тяга нижнего блока широким хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга нижнего блока", isBuiltIn = true),

    ExerciseEntity(name = "Тяга Т-грифа", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга Т-грифа", isBuiltIn = true),
    ExerciseEntity(name = "Тяга Т-грифа в тренажере", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга Т-грифа", isBuiltIn = true),
    ExerciseEntity(name = "Тяга Т-грифа в тренажере обратным хватом", muscleGroups = MuscleGroup.BACK.name, subcategory = "Тяга Т-грифа", isBuiltIn = true),

    // ==================== ПОЯСНИЦА ====================
    ExerciseEntity(name = "Гиперэкстензия", muscleGroups = MuscleGroup.LOWER_BACK.name, isBuiltIn = true),
    ExerciseEntity(name = "Разгибание спины в тренажере", muscleGroups = MuscleGroup.LOWER_BACK.name, isBuiltIn = true),
    ExerciseEntity(name = "Разгибание спины лежа", muscleGroups = MuscleGroup.LOWER_BACK.name, isBuiltIn = true),

    // ==================== ГРУДЬ ====================
    ExerciseEntity(name = "Жим в кроссовере горизонтально", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в кроссовере", isBuiltIn = true),
    ExerciseEntity(name = "Жим в кроссовере лежа", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в кроссовере", isBuiltIn = true),
    ExerciseEntity(name = "Жим в кроссовере лежа на наклонной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в кроссовере", isBuiltIn = true),
    ExerciseEntity(name = "Жим в кроссовере лежа на скамье с обратным наклоном", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в кроссовере", isBuiltIn = true),

    ExerciseEntity(name = "Жим в тренажере лежа", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Жим в тренажере сидя", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Жим в тренажере сидя под углом вверх", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Жим в тренажере сидя под углом вниз", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим в тренажере", isBuiltIn = true),

    ExerciseEntity(name = "Жим гантелей лежа на наклонной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим гантелей", isBuiltIn = true),
    ExerciseEntity(name = "Жим гантелей лежа на полу", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим гантелей", isBuiltIn = true),
    ExerciseEntity(name = "Жим гантелей лежа на ровной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим гантелей", isBuiltIn = true),
    ExerciseEntity(name = "Жим гантелей лежа на скамье с обратным наклоном", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим гантелей", isBuiltIn = true),

    ExerciseEntity(name = "Жим Свенда лежа", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим Свенда", isBuiltIn = true),
    ExerciseEntity(name = "Жим Свенда с Т-грифом", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим Свенда", isBuiltIn = true),
    ExerciseEntity(name = "Жим Свенда с гантелями лежа", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим Свенда", isBuiltIn = true),

    ExerciseEntity(name = "Жим лежа в машине Смита на наклонной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим штанги", isBuiltIn = true),
    ExerciseEntity(name = "Жим лежа в машине Смита на ровной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим штанги", isBuiltIn = true),
    ExerciseEntity(name = "Жим лежа в машине Смита на скамье с обратным наклоном", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим штанги", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги лежа на наклонной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим штанги", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги лежа на ровной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим штанги", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги лежа на скамье с обратным наклоном", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Жим штанги", isBuiltIn = true),

    ExerciseEntity(name = "Отжимания от пола", muscleGroups = MuscleGroup.CHEST.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания", isBuiltIn = true),
    ExerciseEntity(name = "Отжимания под углом вверх", muscleGroups = MuscleGroup.CHEST.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания", isBuiltIn = true),
    ExerciseEntity(name = "Отжимания под углом вниз", muscleGroups = MuscleGroup.CHEST.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания", isBuiltIn = true),

    ExerciseEntity(name = "Отжимания в брусьях на грудь", muscleGroups = MuscleGroup.CHEST.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания в брусьях", isBuiltIn = true),
    ExerciseEntity(name = "Отжимания в гравитроне на грудь", muscleGroups = MuscleGroup.CHEST.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания в брусьях", isBuiltIn = true),

    ExerciseEntity(name = "Разведение гантелей на наклонной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Разведение гантелей", isBuiltIn = true),
    ExerciseEntity(name = "Разведение гантелей на ровной скамье", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Разведение гантелей", isBuiltIn = true),
    ExerciseEntity(name = "Разведение гантелей на скамье с обратным наклоном", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Разведение гантелей", isBuiltIn = true),

    ExerciseEntity(name = "Сведения в кроссовере сверху", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Сведения в кроссовере", isBuiltIn = true),
    ExerciseEntity(name = "Сведения в кроссовере сидя", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Сведения в кроссовере", isBuiltIn = true),
    ExerciseEntity(name = "Сведения в кроссовере снизу", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Сведения в кроссовере", isBuiltIn = true),
    ExerciseEntity(name = "Сведения в кроссовере стоя", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Сведения в кроссовере", isBuiltIn = true),

    ExerciseEntity(name = "Сведения рук в тренажере", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Сведения в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Сведения рук в тренажере бабочка", muscleGroups = MuscleGroup.CHEST.name, subcategory = "Сведения в тренажере", isBuiltIn = true),

    // ==================== ПРЕСС ====================
    ExerciseEntity(name = "Ножницы", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Ножницы", isBuiltIn = true),

    ExerciseEntity(name = "Подъемы корпуса", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подъемы корпуса", isBuiltIn = true),
    ExerciseEntity(name = "Подъемы корпуса на наклонной скамье", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подъемы корпуса", isBuiltIn = true),

    ExerciseEntity(name = "Подъемы ног на брусьях", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подъемы ног", isBuiltIn = true),
    ExerciseEntity(name = "Подъемы ног на турнике", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подъемы ног", isBuiltIn = true),

    ExerciseEntity(name = "Диагональные скручивания", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Скручивания", isBuiltIn = true),
    ExerciseEntity(name = "Скручивания", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Скручивания", isBuiltIn = true),
    ExerciseEntity(name = "Скручивания в тренажере", muscleGroups = MuscleGroup.ABS.name, subcategory = "Скручивания", isBuiltIn = true),
    ExerciseEntity(name = "Скручивания на наклонной скамье", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Скручивания", isBuiltIn = true),
    ExerciseEntity(name = "Скручивания на скамье", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Скручивания", isBuiltIn = true),
    ExerciseEntity(name = "Скручивания с канатом", muscleGroups = MuscleGroup.ABS.name, subcategory = "Скручивания", isBuiltIn = true),
    ExerciseEntity(name = "Скручивания с канатом стоя", muscleGroups = MuscleGroup.ABS.name, subcategory = "Скручивания", isBuiltIn = true),

    ExerciseEntity(name = "Планка", muscleGroups = MuscleGroup.ABS.name, category = ExerciseCategory.HOLD.name, subcategory = "Планка", isBuiltIn = true),

    // ==================== БИЦЕПС ====================
    ExerciseEntity(name = "Сгибания рук в кроссовере сверху", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук стоя в нижнем блоке", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук стоя в нижнем блоке за спиной", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук стоя в нижнем блоке обратным хватом", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук в блоке", isBuiltIn = true),

    ExerciseEntity(name = "Сгибания рук в тренажере", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук в тренажере Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук в тренажере", isBuiltIn = true),

    ExerciseEntity(name = "Концентрированные сгибания рук с гантелями", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Молотки", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Поперечные молотки", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания Зотмана", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с гантелями", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с гантелями лежа на наклонной скамье", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с гантелями обратным хватом", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с гантелями попеременно", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с гантелями сидя на наклонной скамье", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями", isBuiltIn = true),

    ExerciseEntity(name = "Молотки на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания Зотмана на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания одной рукой на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук на скамье Скотта попеременно", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с гантелями на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с гантелями обратным хватом на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук с гантелями на скамье Скотта", isBuiltIn = true),

    ExerciseEntity(name = "Сгибания рук с EZ-штангой", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с EZ-штангой лежа на наклонной скамье", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с EZ-штангой обратным хватом", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук со штангой", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук со штангой лежа на наклонной скамье", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук со штангой обратным хватом", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой", isBuiltIn = true),

    ExerciseEntity(name = "Сгибания рук с EZ-штангой на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук с EZ-штангой обратным хватом на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук со штангой на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой на скамье Скотта", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания рук со штангой обратным хватом на скамье Скотта", muscleGroups = MuscleGroup.BICEPS.name, subcategory = "Сгибания рук со штангой на скамье Скотта", isBuiltIn = true),

    // ==================== ТРИЦЕПС ====================
    ExerciseEntity(name = "Жим штанги лежа обратным узким хватом", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Жим узким хватом", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги лежа обратным узким хватом в машине Смита", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Жим узким хватом", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги лежа узким хватом", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Жим узким хватом", isBuiltIn = true),
    ExerciseEntity(name = "Жим штанги лежа узким хватом в машине Смита", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Жим узким хватом", isBuiltIn = true),

    ExerciseEntity(name = "Отжимания узким хватом", muscleGroups = MuscleGroup.TRICEPS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания", isBuiltIn = true),
    ExerciseEntity(name = "Отжимания узким хватом под углом вверх", muscleGroups = MuscleGroup.TRICEPS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания", isBuiltIn = true),
    ExerciseEntity(name = "Отжимания узким хватом под углом вниз", muscleGroups = MuscleGroup.TRICEPS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания", isBuiltIn = true),

    ExerciseEntity(name = "Обратные отжимания на скамье", muscleGroups = MuscleGroup.TRICEPS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания в брусьях", isBuiltIn = true),
    ExerciseEntity(name = "Отжимания в брусьях на трицепс", muscleGroups = MuscleGroup.TRICEPS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания в брусьях", isBuiltIn = true),
    ExerciseEntity(name = "Отжимания в гравитроне на трицепс", muscleGroups = MuscleGroup.TRICEPS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Отжимания в брусьях", isBuiltIn = true),

    ExerciseEntity(name = "Разгибания одной рукой из-за головы на нижнем блоке", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук в наклоне на нижнем блоке", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук из-за головы на верхнем блоке", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук из-за головы на нижнем блоке", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук на верхнем блоке", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук на верхнем блоке обратным хватом", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в блоке", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук на верхнем блоке с канатом", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в блоке", isBuiltIn = true),

    ExerciseEntity(name = "Жим в тренажере брусья", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук в тренажере", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук в тренажере", isBuiltIn = true),

    ExerciseEntity(name = "Разгибания одной рукой из-за головы сидя", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук в наклоне с гантелью", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук из-за головы сидя", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук с гантелью из-за головы на наклонной скамье", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук с гантелью из-за головы сидя", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания рук с гантелью из-за головы стоя", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Разгибания рук с гантелями", isBuiltIn = true),

    ExerciseEntity(name = "Французский жим гантелей", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Французский жим", isBuiltIn = true),
    ExerciseEntity(name = "Французский жим гантелей на наклонной скамье", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Французский жим", isBuiltIn = true),
    ExerciseEntity(name = "Французский жим гантелей одной рукой", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Французский жим", isBuiltIn = true),
    ExerciseEntity(name = "Французский жим гантелей скамье с обратным наклоном", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Французский жим", isBuiltIn = true),
    ExerciseEntity(name = "Французский жим штанги", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Французский жим", isBuiltIn = true),
    ExerciseEntity(name = "Французский жим штанги на наклонной скамье", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Французский жим", isBuiltIn = true),
    ExerciseEntity(name = "Французский жим штанги скамье с обратным наклоном", muscleGroups = MuscleGroup.TRICEPS.name, subcategory = "Французский жим", isBuiltIn = true),

    // ==================== ПРЕДПЛЕЧЬЯ ====================
    ExerciseEntity(name = "Разгибания кистей в блоке сидя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "В блоке", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания кистей в блоке стоя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "В блоке", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей в блоке сидя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "В блоке", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей в блоке стоя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "В блоке", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей в блоке стоя за спиной", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "В блоке", isBuiltIn = true),

    ExerciseEntity(name = "Разгибания кистей с гантелями сидя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "С гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания кистей с гантелями стоя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "С гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей с гантелями сидя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "С гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей с гантелями стоя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "С гантелями", isBuiltIn = true),

    ExerciseEntity(name = "Разгибания кистей со штангой сидя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "Со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания кистей со штангой стоя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "Со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей со штангой сидя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "Со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей со штангой стоя", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "Со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания кистей со штангой стоя за спиной", muscleGroups = MuscleGroup.FOREARMS.name, subcategory = "Со штангой", isBuiltIn = true),

    // ==================== КВАДРИЦЕПС ====================
    ExerciseEntity(name = "Выпады", muscleGroups = MuscleGroup.QUADS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады назад", muscleGroups = MuscleGroup.QUADS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады при ходьбе", muscleGroups = MuscleGroup.QUADS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады с гантелями", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады с гантелями назад", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады с гантелями при ходьбе", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады со штангой", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады со штангой назад", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Выпады", isBuiltIn = true),
    ExerciseEntity(name = "Выпады со штангой при ходьбе", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Выпады", isBuiltIn = true),

    ExerciseEntity(name = "Вертикальный жим ногами в тренажере", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Жим ногами", isBuiltIn = true),
    ExerciseEntity(name = "Жим в тренажере одной ногой", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Жим ногами", isBuiltIn = true),
    ExerciseEntity(name = "Жим в тренажере одной ногой горизонтально", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Жим ногами", isBuiltIn = true),
    ExerciseEntity(name = "Жим ногами в тренажере", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Жим ногами", isBuiltIn = true),
    ExerciseEntity(name = "Жим ногами в тренажере горизонтально", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Жим ногами", isBuiltIn = true),

    ExerciseEntity(name = "Зашагивания на лавку с гантелями", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Зашагивания", isBuiltIn = true),
    ExerciseEntity(name = "Зашагивания на лавку со штангой", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Зашагивания", isBuiltIn = true),

    ExerciseEntity(name = "Болгарские приседания", muscleGroups = MuscleGroup.QUADS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Болгарские приседания в машине Смита", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Гакк приседания", muscleGroups = MuscleGroup.QUADS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Гакк приседания в тренажере", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Гоблет приседания с гантелью", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания Джеффесона со штангой", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания в машине Смита", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания в рычажном тренажере", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания в тренажёре с поясом", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания с гантелями", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания со штангой", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания сумо", muscleGroups = MuscleGroup.QUADS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Приседания сумо в машине Смита", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Сплит приседания с гантелями", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Фронтальные приседания в машине Смита", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Фронтальные приседания с гантелями", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),
    ExerciseEntity(name = "Фронтальные приседания со штангой", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Приседания", isBuiltIn = true),

    ExerciseEntity(name = "Разгибания ног в тренажере сидя", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Разгибания ног", isBuiltIn = true),
    ExerciseEntity(name = "Разгибания одной ногой в тренажере сидя", muscleGroups = MuscleGroup.QUADS.name, subcategory = "Разгибания ног", isBuiltIn = true),

    // ==================== ЯГОДИЦЫ ====================
    ExerciseEntity(name = "Гиперэкстензия на ягодицы", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Гиперэкстензия", isBuiltIn = true),
    ExerciseEntity(name = "Обратная гиперэкстензия", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Гиперэкстензия", isBuiltIn = true),

    ExerciseEntity(name = "Отведение ног в сторону на нижнем блоке", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Отведения ног", isBuiltIn = true),
    ExerciseEntity(name = "Отведение ног назад в тренажере", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Отведения ног", isBuiltIn = true),
    ExerciseEntity(name = "Отведение ног назад на нижнем блоке", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Отведения ног", isBuiltIn = true),

    ExerciseEntity(name = "Подъем ягодиц в машине Смита", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Подъем ягодиц", isBuiltIn = true),
    ExerciseEntity(name = "Подъем ягодиц в тренажере", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Подъем ягодиц", isBuiltIn = true),
    ExerciseEntity(name = "Подъем ягодиц со штангой", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Подъем ягодиц", isBuiltIn = true),
    ExerciseEntity(name = "Ягодичный мостик", muscleGroups = MuscleGroup.GLUTES.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Подъем ягодиц", isBuiltIn = true),

    ExerciseEntity(name = "Разведение ног в тренажере в присяде", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Разведения ног", isBuiltIn = true),
    ExerciseEntity(name = "Разведение ног в тренажере сидя", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Разведения ног", isBuiltIn = true),

    ExerciseEntity(name = "Румынская тяга в машине Смита", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Румынская тяга", isBuiltIn = true),
    ExerciseEntity(name = "Румынская тяга на нижнем блоке", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Румынская тяга", isBuiltIn = true),
    ExerciseEntity(name = "Румынская тяга с гантелями", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Румынская тяга", isBuiltIn = true),
    ExerciseEntity(name = "Румынская тяга с гирей", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Румынская тяга", isBuiltIn = true),
    ExerciseEntity(name = "Румынская тяга со штангой", muscleGroups = MuscleGroup.GLUTES.name, subcategory = "Румынская тяга", isBuiltIn = true),

    // ==================== БИЦЕПС БЕДРА ====================
    ExerciseEntity(name = "Мертвая тяга", muscleGroups = MuscleGroup.HAMSTRINGS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Мертвая тяга", isBuiltIn = true),
    ExerciseEntity(name = "Мертвая тяга в машине Смита", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Мертвая тяга", isBuiltIn = true),
    ExerciseEntity(name = "Мертвая тяга на нижнем блоке", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Мертвая тяга", isBuiltIn = true),
    ExerciseEntity(name = "Мертвая тяга с гантелями", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Мертвая тяга", isBuiltIn = true),

    ExerciseEntity(name = "Наклоны со штангой", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Наклоны", isBuiltIn = true),
    ExerciseEntity(name = "Наклоны со штангой в машине Смита", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Наклоны", isBuiltIn = true),

    ExerciseEntity(name = "Сгибания ног в тренажере лежа", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Сгибания ног", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания ног в тренажере сидя", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Сгибания ног", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания ног в тренажере стоя", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Сгибания ног", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания одной ногой в тренажере лежа", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Сгибания ног", isBuiltIn = true),
    ExerciseEntity(name = "Сгибания одной ногой в тренажере сидя", muscleGroups = MuscleGroup.HAMSTRINGS.name, subcategory = "Сгибания ног", isBuiltIn = true),
    ExerciseEntity(name = "Скандинавские сгибания", muscleGroups = MuscleGroup.HAMSTRINGS.name, category = ExerciseCategory.BODYWEIGHT.name, subcategory = "Сгибания ног", isBuiltIn = true),

    // ==================== ПРИВОДЯЩИЕ БЕДРА ====================
    ExerciseEntity(name = "Подъемы ног лежа на боку", muscleGroups = MuscleGroup.ADDUCTORS.name, category = ExerciseCategory.BODYWEIGHT.name, isBuiltIn = true),
    ExerciseEntity(name = "Приведение ног в тренажере", muscleGroups = MuscleGroup.ADDUCTORS.name, isBuiltIn = true),
    ExerciseEntity(name = "Приведение ног на нижнем блоке", muscleGroups = MuscleGroup.ADDUCTORS.name, isBuiltIn = true),
    ExerciseEntity(name = "Сведения ног в тренажере сидя", muscleGroups = MuscleGroup.ADDUCTORS.name, isBuiltIn = true),

    // ==================== ИКРЫ ====================
    ExerciseEntity(name = "Жим носками в тренажере", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Жим носками", isBuiltIn = true),
    ExerciseEntity(name = "Жим носками в тренажере для жима ногами", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Жим носками", isBuiltIn = true),

    ExerciseEntity(name = "Подъем на носки в тренажере сидя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки в тренажере", isBuiltIn = true),
    ExerciseEntity(name = "Подъем на носки в тренажере стоя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки в тренажере", isBuiltIn = true),

    ExerciseEntity(name = "Подъем на носки с гантелями сидя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки с гантелями", isBuiltIn = true),
    ExerciseEntity(name = "Подъем на носки с гантелями стоя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки с гантелями", isBuiltIn = true),

    ExerciseEntity(name = "Подъем на носки в машине Смита сидя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Подъем на носки в машине Смита стоя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Подъем на носки со штангой сидя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки со штангой", isBuiltIn = true),
    ExerciseEntity(name = "Подъем на носки со штангой стоя", muscleGroups = MuscleGroup.CALVES.name, subcategory = "Подъем на носки со штангой", isBuiltIn = true),

    // ==================== КАРДИО ====================
    ExerciseEntity(name = "Бег", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true),
    ExerciseEntity(name = "Беговая дорожка", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true),
    ExerciseEntity(name = "Велотренажер", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true),
    ExerciseEntity(name = "Гребной тренажер", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true),
    ExerciseEntity(name = "Степпер", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true),
    ExerciseEntity(name = "Тренажер лестница", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true),
    ExerciseEntity(name = "Ходьба", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true),
    ExerciseEntity(name = "Эллиптический тренажер", muscleGroups = MuscleGroup.CARDIO.name, category = ExerciseCategory.CARDIO.name, isBuiltIn = true)
)