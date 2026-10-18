package com.practicum.fitapp.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {

    // 1. Сначала объявляем Flow с упражнениями
    val allExercises: StateFlow<List<ExerciseEntity>> = repository
        .getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Потом init для наполнения БД
    init {
        viewModelScope.launch {
            if (repository.countBuiltInExercises() == 0) {
                repository.insertExercises(builtInExercises)
            }
        }
    }
}