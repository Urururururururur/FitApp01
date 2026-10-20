package com.practicum.fitapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.UserProfileEntity
import com.practicum.fitapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.practicum.fitapp.data.local.WeightEntryEntity

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    /** История взвешиваний. */
    val weightHistory: StateFlow<List<WeightEntryEntity>> = repository
        .getWeightHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Последнее взвешивание (для UI). */
    val lastWeight: StateFlow<WeightEntryEntity?> = repository
        .getLastWeight()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /** Профиль (null если пользователь ещё не заполнял). */
    val profile: StateFlow<UserProfileEntity?> = repository
        .getProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    /** Зафиксировать новое взвешивание на сегодня. */
    fun addWeight(weightKg: Double) {
        viewModelScope.launch {
            repository.addWeight(System.currentTimeMillis(), weightKg)
        }
    }
    /** Сохранить (или создать) профиль. */
    fun saveProfile(
        name: String,
        gender: String,
        age: Int,
        heightCm: Double,
        weightKg: Double,
        activityLevel: String,
        goal: String
    ) {
        viewModelScope.launch {
            repository.saveProfile(
                UserProfileEntity(
                    id = 1,
                    name = name,
                    gender = gender,
                    age = age,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    activityLevel = activityLevel,
                    goal = goal
                )
            )
        }
    }
}