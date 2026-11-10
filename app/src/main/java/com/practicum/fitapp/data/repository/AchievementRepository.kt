package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.AchievementDao
import com.practicum.fitapp.data.local.UnlockedAchievementEntity
import kotlinx.coroutines.flow.Flow
import com.practicum.fitapp.data.local.DailyCaloriesData
class AchievementRepository(
    private val achievementDao: AchievementDao
) {
    fun getUnlocked(): Flow<List<UnlockedAchievementEntity>> = achievementDao.getUnlocked()

    suspend fun unlock(id: String) {
        achievementDao.unlock(UnlockedAchievementEntity(achievementId = id, unlockedAt = System.currentTimeMillis()))
    }

    // Старые методы
    suspend fun getTotalWorkouts(): Int = achievementDao.getTotalWorkouts()
    suspend fun getTotalWeightLifted(): Double = achievementDao.getTotalWeightLifted()
    suspend fun getTotalDistance(): Double = achievementDao.getTotalDistance()
    suspend fun getTotalUniqueExercises(): Int = achievementDao.getTotalUniqueExercises()
    suspend fun getTotalDiaryDays(): Int = achievementDao.getTotalDiaryDays()

    // НОВЫЕ МЕТОДЫ (добавь их в класс!)
    suspend fun getDailyCaloriesLast7Days(): List<DailyCaloriesData> {
        return achievementDao.getDailyCaloriesLast7Days()
    }

    suspend fun getAllWorkoutDates(): List<Long> {
        return achievementDao.getAllWorkoutDates()
    }

    suspend fun hasAnyPr(): Boolean {
        return achievementDao.hasAnyPr() > 0
    }
}