package com.practicum.fitapp.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ExerciseProgress
import com.practicum.fitapp.data.local.ExerciseWithProgress
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExerciseStatsViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {

    /** Список всех упражнений, по которым есть подходы. */
    val exercisesWithProgress: StateFlow<List<ExerciseWithProgress>> = repository
        .getExercisesWithProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Выбранное для просмотра упражнение (null = никакое не выбрано). */
    private val _selectedExerciseId = MutableStateFlow<Long?>(null)
    val selectedExerciseId: StateFlow<Long?> = _selectedExerciseId.asStateFlow()

    /** Данные для графика выбранного упражнения. */
    val progressData: StateFlow<List<ExerciseProgress>> = _selectedExerciseId
        .flatMapLatest { id ->
            if (id != null) repository.getExerciseProgress(id) else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectExercise(id: Long?) {
        _selectedExerciseId.value = id
    }
}