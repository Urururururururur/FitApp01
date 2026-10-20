package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Запись о взвешивании.
 * Одна запись = один замер веса в конкретный момент.
 * Дата хранится как timestamp (миллисекунды).
 */
@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,     // timestamp взвешивания
    val weightKg: Double
)