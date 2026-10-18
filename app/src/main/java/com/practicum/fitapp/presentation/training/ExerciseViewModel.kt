package com.practicum.fitapp.presentation.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.SetEntity
import com.practicum.fitapp.data.local.WorkoutExerciseEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: TrainingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutExerciseId: Long = checkNotNull(savedStateHandle["workoutExerciseId"])

    /** Само упражнение (из связи). */
    val workoutExercise: StateFlow<WorkoutExerciseEntity?> = repository
        .getWorkoutExerciseById(workoutExerciseId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /** Список подходов. */
    val sets: StateFlow<List<SetEntity>> = repository
        .getSets(workoutExerciseId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Добавить пустой подход. */
    fun addSet() {
        viewModelScope.launch {
            val nextOrder = repository.nextSetOrder(workoutExerciseId)
            repository.insertSet(
                SetEntity(
                    workoutExerciseId = workoutExerciseId,
                    orderInExercise = nextOrder
                )
            )
        }
    }

    /** Удалить подход. */
    fun deleteSet(id: Long) {
        viewModelScope.launch {
            repository.deleteSet(id)
        }
    }
}