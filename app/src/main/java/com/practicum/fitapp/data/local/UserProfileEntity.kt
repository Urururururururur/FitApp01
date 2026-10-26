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
    val id: Int = 1,
    val name: String = "",
    val gender: String = "MALE",
    val age: Int = 25,
    val heightCm: Double = 175.0,
    val weightKg: Double = 70.0,
    val activityLevel: String = "MODERATE",
    val goal: String = "MAINTAIN",
    // --- Новые поля для трекеров ---
    val quitAlcohol: Boolean = false,
    val alcoholQuitDate: Long? = null,
    val alcoholRelapses: Int = 0,
    val quitNicotine: Boolean = false,
    val nicotineQuitDate: Long? = null,
    val nicotineRelapses: Int = 0
)