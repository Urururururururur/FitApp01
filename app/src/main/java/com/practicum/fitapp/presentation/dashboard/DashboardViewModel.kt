package com.practicum.fitapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.HealthConnectManager
import com.practicum.fitapp.data.local.Achievement
import com.practicum.fitapp.data.local.WorkoutEntity
import com.practicum.fitapp.data.repository.AchievementRepository
import com.practicum.fitapp.data.repository.NutritionRepository
import com.practicum.fitapp.data.repository.TrainingRepository
import com.practicum.fitapp.data.repository.UserRepository
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository,
    private val healthConnectManager: HealthConnectManager,
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    // ==================== 1. КАЛОРИИ ====================

    // Получаем норму калорий (упрощено через map)
    private val dailyGoal: StateFlow<Double> = userRepository.getProfile()
        .map { profile ->
            if (profile != null) NutritionCalculator.calculatePlan(profile).targetCalories else 2000.0
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000.0)

    // Получаем съеденные калории за сегодня (используем правильное имя метода)
    private val consumedCalories: StateFlow<Double> = nutritionRepository
        .getMealEntriesForMonth(
            startDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        .map { list ->
            list.sumOf { it.calories } // Используем поле calories
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Соединяем их в один стейт для UI
    val calorieProgress: StateFlow<Pair<Double, Double>> = combine(dailyGoal, consumedCalories) { goal, consumed ->
        Pair(consumed, goal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0.0, 2000.0))

    // ==================== 2. ВОДА (Пока локально) ====================
    private val _waterConsumed = MutableStateFlow(1200) // Пока 1200 мл
    val waterConsumed = _waterConsumed.asStateFlow()
    val waterGoal = 2000 // 2 литра

    fun addWater(ml: Int) {
        _waterConsumed.value += ml
    }
    val todayWorkout: StateFlow<WorkoutEntity?> = trainingRepository
        .getWorkoutsByDay(
            startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        .map { list -> list.firstOrNull() } // Берем первую тренировку за день
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ==================== 3. АКТИВНОСТЬ (Health Connect) ====================
    private val _healthMetrics = MutableStateFlow<HealthConnectManager.HealthMetrics?>(null)
    val healthMetrics = _healthMetrics.asStateFlow()

    fun loadHealthData() {
        viewModelScope.launch {
            if (healthConnectManager.hasAllPermissions()) {
                _healthMetrics.value = healthConnectManager.readHealthMetrics()
            }
        }
    }

    val unlockedAchievements: StateFlow<List<Achievement>> = achievementRepository.getUnlocked()
        .map { unlockedList ->
            unlockedList.take(4).mapNotNull { Achievement.fromId(it.achievementId) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}