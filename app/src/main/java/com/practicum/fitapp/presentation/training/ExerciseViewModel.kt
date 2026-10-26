package com.practicum.fitapp.presentation.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.Intensity
import com.practicum.fitapp.data.local.SetEntity
import com.practicum.fitapp.data.local.WorkoutExerciseEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.ExerciseCategory
import com.practicum.fitapp.data.local.WorkoutEntity

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
    val workout: StateFlow<WorkoutEntity?> = workoutExercise
        .flatMapLatest { we ->
            if (we != null) repository.getWorkoutById(we.workoutId) else kotlinx.coroutines.flow.flowOf(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    /** Само упражнение (справочник). Нужно для определения категории. */
    val exercise: StateFlow<ExerciseEntity?> = workoutExercise
        .flatMapLatest { we ->
            if (we != null) repository.getExerciseById(we.exerciseId) else kotlinx.coroutines.flow.flowOf(null)
        }
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
            // Берём категорию из текущего упражнения. Если не загрузилось — пробуем ещё раз.
            val category = exercise.value?.categoryEnum ?: ExerciseCategory.STRENGTH

            val lastSet = repository.getLastSet(workoutExerciseId)
            val nextOrder = repository.nextSetOrder(workoutExerciseId)

            val newSet = when (category) {
                ExerciseCategory.STRENGTH -> SetEntity(
                    workoutExerciseId = workoutExerciseId,
                    weight = lastSet?.weight,
                    reps = lastSet?.reps,
                    intensity = lastSet?.intensity ?: Intensity.NORMAL.name,
                    orderInExercise = nextOrder
                )
                ExerciseCategory.BODYWEIGHT -> SetEntity(
                    workoutExerciseId = workoutExerciseId,
                    reps = lastSet?.reps,
                    intensity = lastSet?.intensity ?: Intensity.NORMAL.name,
                    orderInExercise = nextOrder
                )
                ExerciseCategory.CARDIO -> SetEntity(
                    workoutExerciseId = workoutExerciseId,
                    distance = lastSet?.distance,
                    duration = lastSet?.duration,
                    intensity = lastSet?.intensity ?: Intensity.NORMAL.name,
                    orderInExercise = nextOrder
                )
                ExerciseCategory.HOLD -> SetEntity(
                    workoutExerciseId = workoutExerciseId,
                    duration = lastSet?.duration,
                    intensity = lastSet?.intensity ?: Intensity.NORMAL.name,
                    orderInExercise = nextOrder
                )
            }

            repository.insertSet(newSet)
        }
    }
    /** Обновить подход. */
    fun updateSet(set: SetEntity) {
        viewModelScope.launch {
            repository.updateSet(
                id = set.id,
                weight = set.weight,
                reps = set.reps,
                distance = set.distance,
                duration = set.duration,
                intensity = set.intensity
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