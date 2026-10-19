package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Тренировка — одна сессия.
 * Тренировка может быть "в ожидании" (isCompleted = false) или "выполнена".
 * Если endDate = null, тренировка ещё идёт.
 */
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Timestamp начала тренировки (миллисекунды)
    val date: Long,

    // Timestamp окончания. null — тренировка не завершена.
    val endDate: Long? = null,

    val name: String = "Тренировка",

    val note: String = "",

    val status: String = WorkoutStatus.WAITING.name,

    // Техническое: когда запись создана в БД
    val createdAt: Long = System.currentTimeMillis()
)