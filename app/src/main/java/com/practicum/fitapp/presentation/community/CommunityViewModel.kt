package com.practicum.fitapp.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.Achievement
import com.practicum.fitapp.data.repository.AchievementRepository
import com.practicum.fitapp.data.repository.UserRepository // <-- ДОБАВЛЕНО
import com.practicum.fitapp.presentation.calculator.NutritionCalculator // <-- ДОБАВЛЕНО
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first // <-- ДОБАВЛЕНО
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class AchievementUiState(
    val achievement: Achievement,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: AchievementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val achievements: StateFlow<List<AchievementUiState>> = repository.getUnlocked()
        .map { unlockedList ->
            val unlockedMap = unlockedList.associateBy { it.achievementId }
            Achievement.entries.map { achievement ->
                val unlocked = unlockedMap[achievement.id]
                AchievementUiState(
                    achievement = achievement,
                    isUnlocked = unlocked != null,
                    unlockedAt = unlocked?.unlockedAt
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            checkAchievements()
        }
    }

    private suspend fun checkAchievements() {
        // 1. Старые проверки
        val totalWorkouts = repository.getTotalWorkouts()
        if (totalWorkouts >= 1) repository.unlock("first_workout")
        if (totalWorkouts >= 10) repository.unlock("ten_workouts")
        if (totalWorkouts >= 50) repository.unlock("fifty_workouts")

        val totalWeight = repository.getTotalWeightLifted()
        if (totalWeight >= 1000.0) repository.unlock("first_ton")
        if (totalWeight >= 10000.0) repository.unlock("ten_tons")

        val totalDistance = repository.getTotalDistance()
        if (totalDistance >= 5000.0) repository.unlock("first_5km")
        if (totalDistance >= 10000.0) repository.unlock("first_10km")

        val diaryDays = repository.getTotalDiaryDays()
        if (diaryDays >= 7) repository.unlock("diary_7")
        if (diaryDays >= 30) repository.unlock("diary_30")

        val uniqueExercises = repository.getTotalUniqueExercises()
        if (uniqueExercises >= 10) repository.unlock("ten_exercises")

        // 2. Новые проверки (нужна цель по калориям)
        val profile = userRepository.getProfile().first()
        val targetCalories = profile?.let { NutritionCalculator.calculatePlan(it).targetCalories } ?: 2000.0

        // --- first_pr (Первый рекорд) ---
        if (repository.hasAnyPr()) {
            repository.unlock("first_pr")
        }

        // --- return_after_break (Возвращение) ---
        val workoutDates = repository.getAllWorkoutDates()
        if (workoutDates.size >= 2) {
            // Ищем разрыв в 14 дней между любыми двумя тренировками
            for (i in 1 until workoutDates.size) {
                val prevDate = workoutDates[i - 1]
                val currentDate = workoutDates[i]
                val daysBetween = (currentDate - prevDate) / (1000 * 60 * 60 * 24)
                if (daysBetween >= 14) {
                    repository.unlock("return_after_break")
                    break
                }
            }
        }

        // --- calorie_balance_5 и perfect_day ---
        val last7Days = repository.getDailyCaloriesLast7Days()
        var balancedDays = 0

        // Допустим, баланс — это попадание в +/- 10% от нормы
        val lowerBound = targetCalories * 0.9
        val upperBound = targetCalories * 1.1

        last7Days.forEach { day ->
            if (day.totalCalories in lowerBound..upperBound) {
                balancedDays++
            }

            // perfect_day: идеальный день, если попал в норму калорий и в этот же день была тренировка
            val dayStartMillis = day.dayId * 86400000
            val hasWorkoutThisDay = workoutDates.any { it in dayStartMillis..(dayStartMillis + 86400000) }

            if (day.totalCalories in lowerBound..upperBound && hasWorkoutThisDay) {
                repository.unlock("perfect_day")
            }
        }

        if (balancedDays >= 5) {
            repository.unlock("calorie_balance_5")
        }
    }
}