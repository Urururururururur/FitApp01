package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_entries")
data class MealEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val productId: Long, // 0 для рецептов
    val productName: String,
    val productCalories: Int,
    val productProtein: Double = 0.0,
    val productFat: Double = 0.0,
    val productCarbs: Double = 0.0,
    val grams: Double
)