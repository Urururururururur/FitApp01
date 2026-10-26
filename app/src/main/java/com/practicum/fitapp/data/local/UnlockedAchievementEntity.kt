package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlocked_achievements")
data class UnlockedAchievementEntity(
    @PrimaryKey
    val achievementId: String,   // ссылается на Achievement.id
    val unlockedAt: Long         // timestamp получения
)