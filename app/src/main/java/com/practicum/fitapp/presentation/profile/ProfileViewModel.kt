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

    /** Профиль (null если пользователь ещё не заполнял). */
    val profile: StateFlow<UserProfileEntity?> = repository
        .getProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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

    /** Сохранить (или создать) профиль. */
    fun saveProfile(
        name: String,
        gender: String,
        age: Int,
        heightCm: Double,
        weightKg: Double,
        activityLevel: String,
        goal: String,
        quitAlcohol: Boolean,
        quitNicotine: Boolean
    ) {
        viewModelScope.launch {
            val current = profile.value
            val isFirstTime = current == null
            val weightChanged = current?.weightKg != weightKg

            // 1. Сохраняем профиль
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
                    nicotineRelapses = current?.nicotineRelapses ?: 0
                )
            )

            // 2. Если это первый запуск или вес изменился,
            // автоматически создаем запись о взвешивании, чтобы норма стала актуальной
            if (isFirstTime || weightChanged) {
                repository.addWeight(System.currentTimeMillis(), weightKg)
            }
        }
    }

    /** Зафиксировать новое взвешивание на сегодня и обновить вес в профиле. */
    fun addWeight(weightKg: Double) {
        viewModelScope.launch {
            val current = profile.value

            // 1. Обновляем вес в самом профиле, чтобы форма и норма пересчитались
            if (current != null) {
                repository.saveProfile(current.copy(weightKg = weightKg))
            }

            // 2. Добавляем запись в историю взвешиваний
            repository.addWeight(System.currentTimeMillis(), weightKg)
        }
    }

    /** Обновить дату отказа от алкоголя (сбрасывает срывы). */
    fun updateAlcoholDate(date: Long) {
        val current = profile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(
                current.copy(
                    alcoholQuitDate = date,
                    alcoholRelapses = 0
                )
            )
        }
    }

    /** Обновить дату отказа от никотина (сбрасывает срывы). */
    fun updateNicotineDate(date: Long) {
        val current = profile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(
                current.copy(
                    nicotineQuitDate = date,
                    nicotineRelapses = 0
                )
            )
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

    /** Очистка всех данных приложения. */
    fun clearAllData(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Очищаем все таблицы в фоновом потоке
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }
            // Вызываем колбэк, чтобы перезапустить Activity (сбросит стейты)
            onComplete()
        }
    }
}