package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.UserProfileDao
import com.practicum.fitapp.data.local.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository для профиля пользователя.
 */
class UserRepository(private val dao: UserProfileDao) {

    /** Профиль. Возвращает null, если его ещё нет. */
    fun getProfile(): Flow<UserProfileEntity?> = dao.getProfile()

    /** Сохранить профиль (создаёт или обновляет). */
    suspend fun saveProfile(profile: UserProfileEntity) {
        dao.saveProfile(profile)
    }
}