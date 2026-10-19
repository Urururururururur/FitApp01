package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Профиль пользователя.
 * Локальное приложение = один пользователь = одна запись.
 * ID всегда 1.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,   // всегда 1 — единственная запись

    val name: String = "",
    val gender: String = "MALE",   // "MALE" / "FEMALE"
    val age: Int = 25,
    val heightCm: Double = 175.0,
    val weightKg: Double = 70.0,
    val activityLevel: String = "MODERATE",   // SEDENTARY / LIGHT / MODERATE / ACTIVE / VERY_ACTIVE
    val goal: String = "MAINTAIN"   // LOSE / MAINTAIN / GAIN
)