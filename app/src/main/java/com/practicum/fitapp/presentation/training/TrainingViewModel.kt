package com.practicum.fitapp.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {

    init {
        // При создании ViewModel проверяем: есть ли встроенные упражнения.
        // Если нет — наполняем справочник.
        viewModelScope.launch {
            if (repository.countBuiltInExercises() == 0) {
                repository.insertExercises(builtInExercises)
            }
        }
    }
}