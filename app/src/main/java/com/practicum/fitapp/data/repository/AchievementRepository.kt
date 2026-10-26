package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.AchievementDao
import com.practicum.fitapp.data.local.UnlockedAchievementEntity
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val dao: AchievementDao) {

    /** Все разблокированные ачивки. */
    fun getUnlocked(): Flow<List<UnlockedAchievementEntity>> = dao.getUnlocked()

    /** Проверить, разблокирована ли ачивка. */
    suspend fun isUnlocked(id: String): Boolean = dao.isUnlocked(id)


    /** Разблокировать ачивку. */
    suspend fun unlock(id: String) {
        dao.unlock(UnlockedAchievementEntity(achievementId = id, unlockedAt = System.currentTimeMillis()))
    }
    suspend fun getTotalWorkouts(): Int = dao.getTotalWorkouts()
    suspend fun getTotalWeightLifted(): Double = dao.getTotalWeightLifted()
    suspend fun getTotalDistance(): Double = dao.getTotalDistance()
    suspend fun getTotalDiaryDays(): Int = dao.getTotalDiaryDays()
    suspend fun getTotalUniqueExercises(): Int = dao.getTotalUniqueExercises()
}