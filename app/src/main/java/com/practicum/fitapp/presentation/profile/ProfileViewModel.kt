package com.practicum.fitapp.presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.FitAppDatabase
import com.practicum.fitapp.data.local.UserProfileEntity
import com.practicum.fitapp.data.local.WeightEntryEntity
import com.practicum.fitapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val db: FitAppDatabase
) : ViewModel() {

    val profile: StateFlow<UserProfileEntity?> = repository
        .getProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val weightHistory: StateFlow<List<WeightEntryEntity>> = repository
        .getWeightHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val lastWeight: StateFlow<WeightEntryEntity?> = repository
        .getLastWeight()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // ОБНОВЛЕНО: Добавлены showNutrition и showWorkoutPlan
    fun updateDashboardSettings(
        showWater: Boolean,
        showActivity: Boolean,
        quitAlcohol: Boolean,
        quitNicotine: Boolean,
        showNutrition: Boolean,
        showWorkoutPlan: Boolean
    ) {
        val current = profile.value ?: return
        viewModelScope.launch {
            val newAlcoholDate = when {
                quitAlcohol && !current.quitAlcohol -> System.currentTimeMillis()
                !quitAlcohol -> null
                else -> current.alcoholQuitDate
            }

            val newNicotineDate = when {
                quitNicotine && !current.quitNicotine -> System.currentTimeMillis()
                !quitNicotine -> null
                else -> current.nicotineQuitDate
            }

            repository.saveProfile(
                current.copy(
                    showWaterBalance = showWater,
                    showActivity = showActivity,
                    quitAlcohol = quitAlcohol,
                    alcoholQuitDate = newAlcoholDate,
                    quitNicotine = quitNicotine,
                    nicotineQuitDate = newNicotineDate,
                    showNutrition = showNutrition,
                    showWorkoutPlan = showWorkoutPlan
                )
            )
        }
    }

    // ОБНОВЛЕНО: Добавлены showNutrition и showWorkoutPlan
    fun saveProfile(
        name: String,
        gender: String,
        age: Int,
        heightCm: Double,
        weightKg: Double,
        activityLevel: String,
        goal: String,
        quitAlcohol: Boolean,
        quitNicotine: Boolean,
        showWaterBalance: Boolean,
        showActivity: Boolean,
        showNutrition: Boolean,
        showWorkoutPlan: Boolean
    ) {
        viewModelScope.launch {
            val current = profile.value
            val isFirstTime = current == null
            val weightChanged = current?.weightKg != weightKg

            repository.saveProfile(
                UserProfileEntity(
                    id = 1,
                    name = name,
                    gender = gender,
                    age = age,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    activityLevel = activityLevel,
                    goal = goal,
                    quitAlcohol = quitAlcohol,
                    alcoholQuitDate = if (quitAlcohol && current?.alcoholQuitDate == null) System.currentTimeMillis() else current?.alcoholQuitDate,
                    alcoholRelapses = current?.alcoholRelapses ?: 0,
                    quitNicotine = quitNicotine,
                    nicotineQuitDate = if (quitNicotine && current?.nicotineQuitDate == null) System.currentTimeMillis() else current?.nicotineQuitDate,
                    nicotineRelapses = current?.nicotineRelapses ?: 0,
                    showWaterBalance = showWaterBalance,
                    showActivity = showActivity,
                    showNutrition = showNutrition,
                    showWorkoutPlan = showWorkoutPlan
                )
            )

            if (isFirstTime || weightChanged) {
                repository.addWeight(System.currentTimeMillis(), weightKg)
            }
        }
    }

    fun addWeight(weightKg: Double) {
        viewModelScope.launch {
            val current = profile.value
            if (current != null) {
                repository.saveProfile(current.copy(weightKg = weightKg))
            }
            repository.addWeight(System.currentTimeMillis(), weightKg)
        }
    }

    fun updateAlcoholDate(date: Long) {
        val current = profile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(current.copy(alcoholQuitDate = date, alcoholRelapses = 0))
        }
    }

    fun updateNicotineDate(date: Long) {
        val current = profile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(current.copy(nicotineQuitDate = date, nicotineRelapses = 0))
        }
    }

    fun addAlcoholRelapse() {
        val current = profile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(current.copy(alcoholRelapses = current.alcoholRelapses + 1))
        }
    }

    fun addNicotineRelapse() {
        val current = profile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(current.copy(nicotineRelapses = current.nicotineRelapses + 1))
        }
    }

    fun clearAllData(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }
            onComplete()
        }
    }
}