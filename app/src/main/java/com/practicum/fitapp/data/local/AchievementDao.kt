package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    /** Все разблокированные ачивки. */
    @Query("SELECT * FROM unlocked_achievements ORDER BY unlockedAt DESC")
    fun getUnlocked(): Flow<List<UnlockedAchievementEntity>>

    /** Проверить, разблокирована ли конкретная ачивка. */
    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_achievements WHERE achievementId = :id)")
    suspend fun isUnlocked(id: String): Boolean

    /** Разблокировать ачивку (если уже есть — игнорировать). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(achievement: UnlockedAchievementEntity)
    // ==================== ТРЕНИРОВКИ ====================

    /** Общее количество тренировок. */
    @Query("SELECT COUNT(*) FROM workouts")
    suspend fun getTotalWorkouts(): Int

    /** Суммарный поднятый вес (тонны считаются как 1000 кг). */
    @Query("SELECT COALESCE(SUM(weight * reps), 0.0) FROM sets WHERE weight IS NOT NULL AND reps IS NOT NULL")
    suspend fun getTotalWeightLifted(): Double

    /** Суммарная дистанция (метры). */
    @Query("SELECT COALESCE(SUM(distance), 0.0) FROM sets WHERE distance IS NOT NULL")
    suspend fun getTotalDistance(): Double

    /** Количество разных упражнений, которые выполнял пользователь. */
    @Query("SELECT COUNT(DISTINCT exerciseId) FROM workout_exercises")
    suspend fun getTotalUniqueExercises(): Int

// ==================== ПИТАНИЕ (ДНЕВНИК) ====================

    /** Количество дней, в которые были записи о еде. */
    @Query("SELECT COUNT(DISTINCT (date / 86400000)) FROM meal_entries")
    suspend fun getTotalDiaryDays(): Int
}