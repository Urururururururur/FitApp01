package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Один подход в упражнении.
 * Поля nullable, потому что разные типы подходов заполняют разные поля:
 *  - Силовые: weight + reps
 *  - С собственным весом: reps (или duration для планки)
 *  - Кардио: distance + duration
 * Остальные поля остаются null.
 */
@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutExerciseId")]
)
data class SetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val workoutExerciseId: Long,

    // Основные поля
    val weight: Double? = null,     // кг
    val reps: Int? = null,          // количество

    // Опциональные поля (для кардио и подобных)
    val distance: Double? = null,   // метры
    val duration: Int? = null,      // секунды

    // "WARMUP" / "EASY" / "NORMAL" / "HARD"
    val intensity: String = Intensity.NORMAL.name,

    // Порядок подхода в упражнении: 0, 1, 2, ...
    val orderInExercise: Int
)