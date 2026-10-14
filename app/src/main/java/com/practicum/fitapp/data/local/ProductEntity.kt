package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity — таблица продуктов (справочник).
 * Каждое поле класса = столбец в таблице SQLite.
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val caloriesPer100g: Int,

    val proteinPer100g: Double,

    val fatPer100g: Double,

    val carbsPer100g: Double
)
/**
 * Объявил таблицу products. Room по этой аннотации сам сгенерирует SQL:
 * CREATE TABLE products (
 *     id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
 *     name TEXT NOT NULL,
 *     caloriesPer100g INTEGER NOT NULL,
 *     proteinPer100g REAL NOT NULL,
 *     fatPer100g REAL NOT NULL,
 *     carbsPer100g REAL NOT NULL
 * )
 */