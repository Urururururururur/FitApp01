package com.practicum.fitapp.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.MuscleGroup
import com.practicum.fitapp.data.local.WorkoutEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import com.practicum.fitapp.presentation.common.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject
import com.practicum.fitapp.presentation.training.CatalogProgram


@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val workoutsForDay: StateFlow<List<WorkoutEntity>> = _selectedDate
        .flatMapLatest { date ->
            val start = DateUtils.localDateToStartOfDay(date)
            val end = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.getWorkoutsByDay(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val workoutTags: StateFlow<Map<Long, List<MuscleGroup>>> = _selectedDate
        .flatMapLatest { date ->
            val start = DateUtils.localDateToStartOfDay(date)
            val end = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.getMuscleGroupsForDay(start, end).map { rawList ->
                rawList.groupBy { it.workoutId }
                    .mapValues { (_, items) ->
                        items.flatMap { MuscleGroup.parseList(it.muscleGroups) }.distinct()
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val allExercises: StateFlow<List<ExerciseEntity>> = repository
        .getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val workoutDates: StateFlow<Set<LocalDate>> = repository
        .getAllWorkoutDates()
        .map { timestamps ->
            timestamps.map { DateUtils.timestampToLocalDate(it) }.toSet()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val allWorkouts: StateFlow<List<WorkoutEntity>> = repository
        .getAllWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            // Так как мы обновили базу, в ней теперь 0 встроенных упражнений.
            // Если их меньше 300 (значит база пустая или старая), вставляем новые.
            if (repository.countBuiltInExercises() < 300) {
                repository.insertExercises(builtInExercises)
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addNewWorkout() {
        viewModelScope.launch {
            val dateTimestamp = DateUtils.localDateToStartOfDay(_selectedDate.value) +
                    (System.currentTimeMillis() % 86_400_000L)
            repository.insertWorkout(
                WorkoutEntity(
                    date = dateTimestamp,
                    name = "Тренировка"
                )
            )
        }
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkout(id)
        }
    }

    fun copyWorkoutToDate(sourceWorkoutId: Long, targetDate: LocalDate) {
        viewModelScope.launch {
            val targetTimestamp = DateUtils.localDateToStartOfDay(targetDate) +
                    (System.currentTimeMillis() % 86_400_000L)
            repository.copyWorkout(sourceWorkoutId, targetTimestamp)
        }
    }

    fun addProgramDay(program: com.practicum.fitapp.presentation.training.CatalogProgram, day: com.practicum.fitapp.presentation.training.CatalogDay) {
        viewModelScope.launch {
            val timestamp = DateUtils.localDateToStartOfDay(_selectedDate.value) +
                    (System.currentTimeMillis() % 86_400_000L)
            repository.addProgramDayToWorkout(program, day, timestamp)
        }
    }
}