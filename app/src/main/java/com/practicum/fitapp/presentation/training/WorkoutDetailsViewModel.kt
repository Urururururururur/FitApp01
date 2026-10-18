package com.practicum.fitapp.presentation.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.WorkoutEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val repository: TrainingRepository,
    savedStateHandle: SavedStateHandle  // ← Сюда NavController передаёт аргументы из URL
) : ViewModel() {

    // Достаём workoutId из URL
    private val workoutId: Long = checkNotNull(savedStateHandle["workoutId"])

    // Загружаем тренировку по ID
    val workout: StateFlow<WorkoutEntity?> = repository.getWorkoutById(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}