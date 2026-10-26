package com.practicum.fitapp.presentation.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.MuscleGroup
import com.practicum.fitapp.data.local.WorkoutExerciseEntity
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Группа упражнений по подкатегории. */
data class ExerciseGroup(
    val subcategory: String,
    val exercises: List<ExerciseEntity>
)

@HiltViewModel
class ExerciseCatalogViewModel @Inject constructor(
    private val repository: TrainingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Long = checkNotNull(savedStateHandle["workoutId"])

    // Стейт строки поиска
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Стейт выбранной группы мышц (null = все)
    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)
    val selectedMuscleGroup: StateFlow<MuscleGroup?> = _selectedMuscleGroup.asStateFlow()

    // Отфильтрованный список упражнений
    // Отфильтрованный и сгруппированный список упражнений
    val groupedExercises: StateFlow<List<ExerciseGroup>> = combine(
        repository.getAllExercises(),
        _searchQuery,
        _selectedMuscleGroup
    ) { exercises, query, muscleGroup ->
        exercises.filter { ex ->
            val matchesQuery = query.isBlank() || ex.name.contains(query, ignoreCase = true)
            val matchesMuscle = muscleGroup == null || ex.muscleGroupList.contains(muscleGroup)
            matchesQuery && matchesMuscle
        }
    }.map { filteredList ->
        // Группируем по субкатегории. Если пусто — пишем "Разное".
        filteredList.groupBy { it.subcategory.ifBlank { "Разное" } }
            .map { (subcategory, exercisesList) ->
                ExerciseGroup(subcategory, exercisesList)
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMuscleGroup(group: MuscleGroup?) {
        _selectedMuscleGroup.value = group
    }

    /** Добавить упражнение в текущую тренировку. */
    fun addExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            val nextOrder = repository.nextWorkoutExerciseOrder(workoutId)
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