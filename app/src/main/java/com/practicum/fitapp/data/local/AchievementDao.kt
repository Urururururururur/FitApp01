package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
data class DailyCaloriesData(
    val dayId: Long,
    val totalCalories: Double
)
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

    // ==================== ДЛЯ НОВЫХ АЧИВОК ====================

    // 1. Для calorie_balance_5 (Баланс 5/7). Возвращает сумму калорий за каждый из последних 7 дней.
    // (Предполагается, что date в meal_entries - это timestamp начала дня в миллисекундах)
    @Query("""
        SELECT (date / 86400000) as dayId, COALESCE(SUM(productCalories * grams / 100.0), 0.0) as totalCalories
        FROM meal_entries
        GROUP BY (date / 86400000)
        ORDER BY dayId DESC
        LIMIT 7
    """)
    suspend fun getDailyCaloriesLast7Days(): List<DailyCaloriesData>

    // 2. Для return_after_break (Возвращение). Возвращает даты всех тренировок (по возрастанию).
    @Query("SELECT date FROM workouts ORDER BY date ASC")
    suspend fun getAllWorkoutDates(): List<Long>

    // 3. Для first_pr (Первый рекорд). Проверяем, есть ли упражнение, где вес на текущей тренировке больше, чем на предыдущей.
    // (Упрощенная версия: просто ищем, есть ли подходы с весом больше 0, и была ли прогрессия).
    @Query("""
        SELECT COUNT(*) FROM sets s1 
        INNER JOIN workout_exercises we1 ON s1.workoutExerciseId = we1.id
        WHERE s1.weight > 0 AND EXISTS (
            SELECT 1 FROM sets s2 
            INNER JOIN workout_exercises we2 ON s2.workoutExerciseId = we2.id
            WHERE we2.exerciseId = we1.exerciseId 
            AND s2.workoutExerciseId != s1.workoutExerciseId 
            AND s2.weight < s1.weight
        )
    """)
    suspend fun hasAnyPr(): Int


}
