package com.practicum.fitapp.presentation.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.WorkoutEntity
import com.practicum.fitapp.data.local.WorkoutExerciseEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.practicum.fitapp.data.local.WorkoutExerciseWithExercise
import com.practicum.fitapp.data.local.WorkoutStatus
import com.practicum.fitapp.data.local.ExerciseStats


@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val repository: TrainingRepository,
    savedStateHandle: SavedStateHandle  // ← Сюда NavController передаёт аргументы из URL
) : ViewModel() {

    /** Начать тренировку: статус IN_PROGRESS, endDate = null. */
    fun startWorkout() {
        val currentWorkout = workout.value ?: return
        viewModelScope.launch {
            repository.updateWorkout(
                id = currentWorkout.id,
                name = currentWorkout.name,
                note = currentWorkout.note,
                endDate = null,   // Тренировка идёт
                status = WorkoutStatus.IN_PROGRESS.name
            )
        }
    }

    /** Обновить название тренировки. */
    fun updateName(newName: String) {
        val currentWorkout = workout.value ?: return
        if (newName.isBlank()) return  // не даём сохранить пустое имя
        viewModelScope.launch {
            repository.updateWorkout(
                id = currentWorkout.id,
                name = newName,
                note = currentWorkout.note,
                endDate = currentWorkout.endDate,
                status = currentWorkout.status
            )
        }
    }

    /** Обновить заметку тренировки. */
    fun updateNote(newNote: String) {
        val currentWorkout = workout.value ?: return
        viewModelScope.launch {
            repository.updateWorkout(
                id = currentWorkout.id,
                name = currentWorkout.name,
                note = newNote,
                endDate = currentWorkout.endDate,
                status = currentWorkout.status
            )
        }
    }
    /** Закончить тренировку: статус COMPLETED, endDate = сейчас. */
    fun finishWorkout() {
        val currentWorkout = workout.value ?: return
        viewModelScope.launch {
            repository.updateWorkout(
                id = currentWorkout.id,
                name = currentWorkout.name,
                note = currentWorkout.note,
                endDate = System.currentTimeMillis(),   // Время окончания
                status = WorkoutStatus.COMPLETED.name
            )
        }
    }
    // Достаём workoutId из URL
    private val workoutId: Long = checkNotNull(savedStateHandle["workoutId"])

    /** Статистика по каждому упражнению в тренировке. */
    val exerciseStats: StateFlow<List<ExerciseStats>> = repository
        .getExerciseStats(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )



    // Загружаем тренировку по ID
    val workout: StateFlow<WorkoutEntity?> = repository.getWorkoutById(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    /** Упражнения в тренировке (связи). */
    val workoutExercises: StateFlow<List<WorkoutExerciseEntity>> = repository
        .getWorkoutExercises(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Упражнения с раскрытием данных из справочника. */
    val exercisesWithId: StateFlow<List<WorkoutExerciseWithExercise>> = repository
        .getExercisesWithId(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    /** Все упражнения из справочника — для диалога выбора. */
    val allExercises: StateFlow<List<ExerciseEntity>> = repository
        .getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    /** Добавить упражнение в текущую тренировку. */
    fun addExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            // Получаем следующий порядковый номер
            val nextOrder = repository.nextWorkoutExerciseOrder(workoutId)

            // Создаём связь
            repository.insertWorkoutExercise(
                WorkoutExerciseEntity(
                    workoutId = workoutId,
                    exerciseId = exercise.id,
                    orderInWorkout = nextOrder
                )
            )
        }
    }
}