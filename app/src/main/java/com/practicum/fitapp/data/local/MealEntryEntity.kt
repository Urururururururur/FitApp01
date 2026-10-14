package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Запись о приёме пищи. Ссылается на продукт из справочника.
 *
 * foreignKeys — описывает связь с products.
 * onDelete = CASCADE — если удалим продукт, все записи о нём тоже удалятся.
 * index — ускоряет запросы по productId (SQLite создаст B-tree индекс).
 */
@Entity(
    tableName = "meal_entries",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId"), Index("date")]
)
data class MealEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Дата в миллисекундах (Unix timestamp).
    val date: Long,

    // Внешний ключ на products.id.
    val productId: Long,

    // Денормализация: сохраняем название и калорийность на момент записи.
    val productName: String,
    val productCalories: Int,

    // Сколько грамм съедено.
    val grams: Double
)