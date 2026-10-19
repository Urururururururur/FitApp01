package com.practicum.fitapp.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import com.practicum.fitapp.data.local.WorkoutEntity
import com.practicum.fitapp.presentation.common.DateUtils
import kotlinx.coroutines.flow.flatMapLatest
import java.util.Calendar

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {


    // Стейт выбранной даты (по умолчанию - сегодня)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Список упражнений для отображения
    val allExercises: StateFlow<List<ExerciseEntity>> = repository
        .getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Наполнение БД при старте
    init {
        viewModelScope.launch {
            if (repository.countBuiltInExercises() == 0) {
                repository.insertExercises(builtInExercises)
            }
        }
    }
    // Записи тренировок за выбранный день.
// flatMapLatest переключает поток, когда меняется _selectedDate.
    val workoutsForDay: StateFlow<List<WorkoutEntity>> = _selectedDate
        .flatMapLatest { date ->
            val start = DateUtils.localDateToStartOfDay(date)
            // Конец дня - это начало следующего дня
            val end = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.getWorkoutsByDay(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    // Метод изменения выбранной даты
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }
    fun addNewWorkout() {
        viewModelScope.launch {
            // Создаём тренировку на выбранную дату, время начала - текущий момент
            val dateTimestamp = DateUtils.localDateToStartOfDay(_selectedDate.value) +
                    (System.currentTimeMillis() % 86_400_000L) // добавляем текущее время суток

            repository.insertWorkout(
                WorkoutEntity(
                    date = dateTimestamp,
                    name = "Тренировка"
                )
            )
        }
    }
}