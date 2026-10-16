package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Справочник упражнений.
 * Хранит и встроенные (isBuiltIn = true), и пользовательские (isBuiltIn = false).
 *
 * muscleGroups — строка через запятую с именами enum'а, например "LEGS,BACK".
 * category — строка имени ExerciseCategory, например "STRENGTH".
 */
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    // "LEGS,BACK,CARDIO" — через запятую
    val muscleGroups: String = "",

    // "STRENGTH" / "BODYWEIGHT" / "CARDIO"
    val category: String = ExerciseCategory.STRENGTH.name,

    // true — встроенное, удалить нельзя. false — пользовательское.
    val isBuiltIn: Boolean = false,

    // Описание техники, пока пустое
    val description: String = ""
) {
    /** Удобный доступ к группам мышц как к списку enum. */
    val muscleGroupList: List<MuscleGroup>
        get() = MuscleGroup.parseList(muscleGroups)

    /** Удобный доступ к категории как к enum. */
    val categoryEnum: ExerciseCategory
        get() = ExerciseCategory.valueOf(category)
}