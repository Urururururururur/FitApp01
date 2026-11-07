package com.practicum.fitapp.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.UserProfileEntity
import com.practicum.fitapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Текущий шаг (1 - Имя, 2 - Возраст, 3 - Рост/Вес, 4 - Активность, 5 - Цель, 6 - Итог)
    private val _currentStep = MutableStateFlow(1)
    val currentStep = _currentStep.asStateFlow()

    // Данные
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _gender = MutableStateFlow("MALE")
    val gender = _gender.asStateFlow()

    private val _age = MutableStateFlow("25")
    val age = _age.asStateFlow()

    private val _height = MutableStateFlow("175")
    val height = _height.asStateFlow()

    private val _weight = MutableStateFlow("70")
    val weight = _weight.asStateFlow()

    private val _activityLevel = MutableStateFlow("MODERATE") // SEDENTARY, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE
    val activityLevel = _activityLevel.asStateFlow()

    private val _goal = MutableStateFlow("MAINTAIN") // LOSE, MAINTAIN, GAIN
    val goal = _goal.asStateFlow()

    // Обновление данных
    fun updateName(newName: String) { _name.value = newName }
    fun updateGender(newGender: String) { _gender.value = newGender }
    fun updateAge(newAge: String) { _age.value = newAge }
    fun updateHeight(newHeight: String) { _height.value = newHeight }
    fun updateWeight(newWeight: String) { _weight.value = newWeight }
    fun updateActivityLevel(newLevel: String) { _activityLevel.value = newLevel }
    fun updateGoal(newGoal: String) { _goal.value = newGoal }

    // Навигация
    fun nextStep() {
        if (_currentStep.value < 6) _currentStep.value += 1
    }

    fun previousStep() {
        if (_currentStep.value > 1) _currentStep.value -= 1
    }

    // Сохранение профиля в БД
    fun saveProfile(onComplete: () -> Unit) {
        viewModelScope.launch {
            val weightDouble = _weight.value.toDoubleOrNull() ?: 70.0
            val profile = UserProfileEntity(
                name = _name.value.ifBlank { "Пользователь" },
                gender = _gender.value,
                age = _age.value.toIntOrNull() ?: 25,
                heightCm = _height.value.toDoubleOrNull() ?: 175.0,
                weightKg = weightDouble,
                activityLevel = _activityLevel.value,
                goal = _goal.value
            )
            userRepository.saveProfile(profile)

            // Обязательно создаем взвешивание, чтобы норма была актуальной
            userRepository.addWeight(System.currentTimeMillis(), weightDouble)

            onComplete()
        }
    }
}