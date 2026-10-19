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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    /** Профиль (null если пользователь ещё не заполнял). */
    val profile: StateFlow<UserProfileEntity?> = repository
        .getProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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