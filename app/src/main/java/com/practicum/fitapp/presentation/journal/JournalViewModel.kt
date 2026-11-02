package com.practicum.fitapp.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.WeightEntryEntity
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import com.practicum.fitapp.data.HealthConnectManager
import kotlinx.coroutines.launch

data class NutritionDay(val date: LocalDate, val calories: Double, val goal: Double) {
    val ratio: Double get() = if (goal > 0) calories / goal else 0.0
}

data class WorkoutDay(val date: LocalDate, val hasWorkout: Boolean)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val trainingRepository: TrainingRepository,
    private val userRepository: UserRepository,
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    // Список разрешений для UI (чтобы запросить их)
    val hcPermissions = healthConnectManager.permissions

    private val _stepsState = MutableStateFlow<Long?>(null)
    val stepsState = _stepsState.asStateFlow()

    fun loadSteps() {
        viewModelScope.launch {
            try {
                if (healthConnectManager.hasAllPermissions()) {
                    val steps = healthConnectManager.readStepsToday()
                    _stepsState.value = steps
                } else {
                    _stepsState.value = null
                }
            } catch (e: Exception) {
                _stepsState.value = null
            }
        }
    }

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    val weightHistory: StateFlow<List<WeightEntryEntity>> = userRepository.getWeightHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nutritionDays: StateFlow<List<NutritionDay>> = _selectedMonth
        .flatMapLatest { month ->
            val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            combine(
                nutritionRepository.getMealEntriesForMonth(start, end),
                userRepository.getProfile()
            ) { entriesList, profile ->
                if (profile == null) return@combine emptyList()
                val goal = NutritionCalculator.calculatePlan(profile).targetCalories
                val caloriesByDate = entriesList.groupBy { Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }
                    .mapValues { (_, entries) -> entries.sumOf { it.calories } }

                val days = mutableListOf<NutritionDay>()
                for (day in 1..month.lengthOfMonth()) {
                    val date = month.atDay(day)
                    val calories = caloriesByDate[date] ?: 0.0
                    days.add(NutritionDay(date, calories, goal))
                }
                days
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutDays: StateFlow<List<WorkoutDay>> = _selectedMonth
        .flatMapLatest { month ->
            val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            trainingRepository.getWorkoutTimestampsForMonth(start, end).map { timestamps ->
                val workoutDates = timestamps.map { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }.toSet()
                val days = mutableListOf<WorkoutDay>()
                for (day in 1..month.lengthOfMonth()) {
                    val date = month.atDay(day)
                    days.add(WorkoutDay(date, date in workoutDates))
                }
                days
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun previousMonth() { _selectedMonth.value = _selectedMonth.value.minusMonths(1) }
    fun nextMonth() { _selectedMonth.value = _selectedMonth.value.plusMonths(1) }
}