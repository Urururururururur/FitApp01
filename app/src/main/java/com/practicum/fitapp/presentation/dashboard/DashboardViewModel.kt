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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class DailyMacros(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0
)

data class Goals(
    val calories: Double = 2000.0,
    val protein: Double = 100.0,
    val fat: Double = 60.0,
    val carbs: Double = 250.0
)

// ОБНОВЛЕНО: Добавлены showNutrition и showWorkoutPlan
data class DashboardSettings(
    val showNutrition: Boolean = true,
    val showWater: Boolean = true,
    val showActivity: Boolean = true,
    val showWorkoutPlan: Boolean = true,
    val quitAlcohol: Boolean = false,
    val alcoholQuitDate: Long? = null,
    val alcoholRelapses: Int = 0,
    val quitNicotine: Boolean = false,
    val nicotineQuitDate: Long? = null,
    val nicotineRelapses: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository,
    private val healthConnectManager: HealthConnectManager,
    private val trainingRepository: TrainingRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {
    val hcPermissions = healthConnectManager.permissions

    val userName: StateFlow<String> = userRepository.getProfile()
        .map { profile -> profile?.name ?: "Андрей" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Андрей")

    // СЛУШАЕМ НАСТРОЙКИ
    val settings: StateFlow<DashboardSettings> = userRepository.getProfile()
        .map { profile ->
            DashboardSettings(
                showNutrition = profile?.showNutrition ?: true,
                showWater = profile?.showWaterBalance ?: true,
                showActivity = profile?.showActivity ?: true,
                showWorkoutPlan = profile?.showWorkoutPlan ?: true,
                quitAlcohol = profile?.quitAlcohol ?: false,
                alcoholQuitDate = profile?.alcoholQuitDate,
                alcoholRelapses = profile?.alcoholRelapses ?: 0,
                quitNicotine = profile?.quitNicotine ?: false,
                nicotineQuitDate = profile?.nicotineQuitDate,
                nicotineRelapses = profile?.nicotineRelapses ?: 0
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSettings())

    val goals: StateFlow<Goals> = userRepository.getProfile()
        .map { profile ->
            if (profile != null) {
                val plan = NutritionCalculator.calculatePlan(profile)
                Goals(plan.targetCalories, plan.proteinGrams, plan.fatGrams, plan.carbsGrams)
            } else Goals()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Goals())

    private val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val consumed: StateFlow<DailyMacros> = nutritionRepository
        .getEntriesByDate(todayStart)
        .map { list ->
            var cal = 0.0
            var prot = 0.0
            var f = 0.0
            var carb = 0.0
            list.forEach { entry ->
                val factor = entry.grams / 100.0
                cal += entry.productCalories * factor
                prot += entry.productProtein * factor
                f += entry.productFat * factor
                carb += entry.productCarbs * factor
            }
            DailyMacros(cal, prot, f, carb)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyMacros())

    private val _waterConsumed = MutableStateFlow(1200)
    val waterConsumed = _waterConsumed.asStateFlow()
    val waterGoal = 2200

    fun addWater(ml: Int) {
        _waterConsumed.value += ml
    }

    private val _healthMetrics = MutableStateFlow<HealthConnectManager.HealthMetrics?>(null)
    val healthMetrics = _healthMetrics.asStateFlow()

    fun loadHealthData() {
        viewModelScope.launch {
            if (healthConnectManager.hasAllPermissions()) {
                _healthMetrics.value = healthConnectManager.readHealthMetrics()
            }
        }
    }

    val todayWorkout: StateFlow<WorkoutEntity?> = trainingRepository
        .getWorkoutsByDay(
            startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        .map { list -> list.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val unlockedAchievements: StateFlow<List<Achievement>> = achievementRepository.getUnlocked()
        .map { unlockedList ->
            unlockedList.take(3).mapNotNull { Achievement.fromId(it.achievementId) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}