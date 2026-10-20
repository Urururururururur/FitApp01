package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.UserProfileDao
import com.practicum.fitapp.data.local.UserProfileEntity
import com.practicum.fitapp.data.local.WeightEntryDao
import com.practicum.fitapp.data.local.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository для профиля пользователя и истории взвешиваний.
 */
class UserRepository(
    private val dao: UserProfileDao,
    private val weightEntryDao: WeightEntryDao
) {

    // ==================== Профиль ====================

    /** Профиль. Возвращает null, если его ещё нет. */
    fun getProfile(): Flow<UserProfileEntity?> = dao.getProfile()

    /** Сохранить профиль (создаёт или обновляет). */
    suspend fun saveProfile(profile: UserProfileEntity) {
        dao.saveProfile(profile)
    }

    // ==================== Взвешивания ====================

    /** Вся история взвешиваний. */
    fun getWeightHistory(): Flow<List<WeightEntryEntity>> = weightEntryDao.getAll()

    /** Последнее взвешивание. */
    fun getLastWeight(): Flow<WeightEntryEntity?> = weightEntryDao.getLast()

    /** Взвешивание, актуальное на конкретный момент. */
    suspend fun getWeightAt(timestamp: Long): WeightEntryEntity? =
        weightEntryDao.getLatestBefore(timestamp)

    /** Добавить новое взвешивание. */
    suspend fun addWeight(date: Long, weightKg: Double) {
        weightEntryDao.insert(WeightEntryEntity(date = date, weightKg = weightKg))
    }
}