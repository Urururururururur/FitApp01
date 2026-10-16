package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Упражнение внутри конкретной тренировки.
 * Ссылается на тренировку и на справочник упражнений.
 * orderInWorkout — порядок, в котором пользователь добавил упражнение.
 */
@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId"), Index("exerciseId")]
)
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val workoutId: Long,
    val exerciseId: Long,

    // Порядок добавления: 0, 1, 2, ...
    val orderInWorkout: Int,

    val note: String = ""
)