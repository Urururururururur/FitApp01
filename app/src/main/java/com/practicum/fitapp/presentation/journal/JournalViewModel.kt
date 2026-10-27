package com.practicum.fitapp.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.UserProfileEntity
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

/**
 * Состояние дня для тепловой карты питания.
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
     */
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

                // Группируем калории по LocalDate (исправление часовых поясов)
                val caloriesByDate = entriesList.groupBy {
                    Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                }.mapValues { (_, entries) ->
                    entries.sumOf { it.calories }
                }

                val days = mutableListOf<NutritionDay>()
                val daysInMonth = month.lengthOfMonth()

                for (day in 1..daysInMonth) {
                    val date = month.atDay(day)
                    val calories = caloriesByDate[date] ?: 0.0
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
            val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            trainingRepository.getWorkoutTimestampsForMonth(start, end).map { timestamps ->
                // Превращаем timestamp'ы в LocalDate для точного сравнения (исправление часовых поясов)
                val workoutDates = timestamps.map {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }.toSet()

                val days = mutableListOf<WorkoutDay>()
                val daysInMonth = month.lengthOfMonth()

                for (day in 1..daysInMonth) {
                    val date = month.atDay(day)
                    val hasWorkout = date in workoutDates
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