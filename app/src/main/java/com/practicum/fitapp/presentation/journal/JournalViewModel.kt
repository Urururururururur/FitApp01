package com.practicum.fitapp.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.DailyCalories
import com.practicum.fitapp.data.local.DailyWorkoutCount
import com.practicum.fitapp.data.local.UserProfileEntity
import com.practicum.fitapp.data.repository.NutritionRepository
import com.practicum.fitapp.data.repository.TrainingRepository
import com.practicum.fitapp.data.repository.UserRepository
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import com.practicum.fitapp.presentation.common.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import com.practicum.fitapp.data.local.WeightEntryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map


/**
 * Состояние дня для тепловой карты питания.
 *  - день
 *  - съедено ккал
 *  - норма ккал
 *  - процент от нормы
 */
data class NutritionDay(
    val date: LocalDate,
    val calories: Double,
    val goal: Double
) {
    val ratio: Double
        get() = if (goal > 0) calories / goal else 0.0
}

/**
 * Состояние дня для тепловой карты тренировок.
 */
data class WorkoutDay(
    val date: LocalDate,
    val hasWorkout: Boolean
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val trainingRepository: TrainingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Выбранный месяц (по умолчанию — текущий)
    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    // История взвешиваний
    val weightHistory: StateFlow<List<WeightEntryEntity>> = userRepository
        .getWeightHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Данные для тепловой карты питания за выбранный месяц.
     * flatMapLatest автоматически переключает поток, когда меняется месяц.
     */
    val nutritionDays: StateFlow<List<NutritionDay>> = _selectedMonth
        .flatMapLatest { month ->
            val start = month.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = month.plusMonths(1).atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            combine(
                nutritionRepository.getDailyCalories(start, end),
                userRepository.getProfile()
            ) { caloriesList, profile ->
                if (profile == null) return@combine emptyList()

                val goal = NutritionCalculator.calculatePlan(profile).targetCalories
                val caloriesByDay = caloriesList.associateBy { it.dayTimestamp }
                val days = mutableListOf<NutritionDay>()
                val daysInMonth = month.lengthOfMonth()
                val zone = java.time.ZoneId.systemDefault()

                for (day in 1..daysInMonth) {
                    val date = month.atDay(day)
                    val dayStartTs = date.atStartOfDay(zone).toInstant().toEpochMilli()
                    val calories = caloriesByDay[dayStartTs]?.totalCalories ?: 0.0
                    days.add(NutritionDay(date, calories, goal))
                }
                days
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Данные для тепловой карты тренировок за выбранный месяц.
     */
    val workoutDays: StateFlow<List<WorkoutDay>> = _selectedMonth
        .flatMapLatest { month ->
            val start = month.atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = month.plusMonths(1).atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            trainingRepository.getDailyWorkoutCounts(start, end).map { workoutsList ->
                val workoutByDay = workoutsList.associateBy { it.dayTimestamp }
                val days = mutableListOf<WorkoutDay>()
                val daysInMonth = month.lengthOfMonth()
                val zone = java.time.ZoneId.systemDefault()

                for (day in 1..daysInMonth) {
                    val date = month.atDay(day)
                    val dayStartTs = date.atStartOfDay(zone).toInstant().toEpochMilli()
                    val hasWorkout = (workoutByDay[dayStartTs]?.count ?: 0) > 0
                    days.add(WorkoutDay(date, hasWorkout))
                }
                days
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Методы переключения месяцев
    fun previousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }
}