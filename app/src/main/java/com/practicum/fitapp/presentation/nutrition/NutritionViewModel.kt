package com.practicum.fitapp.presentation.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.MealEntryEntity
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.data.repository.AiCalorieRepository
import com.practicum.fitapp.data.repository.NutritionRepository
import com.practicum.fitapp.data.repository.UserRepository
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: NutritionRepository,
    private val userRepository: UserRepository,
    private val aiCalorieRepository: AiCalorieRepository
) : ViewModel() {

    // ==================== Выбранная дата ====================
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // ==================== Продукты ====================
    val products: StateFlow<List<ProductEntity>> = repository
        .getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ==================== Норма калорий ====================
    val dailyGoal: StateFlow<Double?> = userRepository
        .getProfile()
        .map { profile ->
            if (profile != null) NutritionCalculator.calculatePlan(profile).targetCalories else null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // ==================== Записи за день ====================
    val entriesForDay: StateFlow<List<MealEntryEntity>> = _selectedDate
        .flatMapLatest { date ->
            val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.getEntriesByDate(timestamp)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ==================== Сводка ====================
    val dailySummary: StateFlow<DailySummary> = entriesForDay
        .map { entries ->
            var calories = 0.0
            var protein = 0.0
            var fat = 0.0
            var carbs = 0.0
            entries.forEach { entry ->
                val factor = entry.grams / 100.0
                calories += entry.productCalories * factor
                protein += entry.productProtein * factor
                fat += entry.productFat * factor
                carbs += entry.productCarbs * factor
            }
            DailySummary(calories, protein, fat, carbs, entries.size)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailySummary()
        )

    // ==================== ИИ Состояния ====================
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    private val _aiResult = MutableStateFlow<AiFoodResult?>(null)
    val aiResult = _aiResult.asStateFlow()

    fun analyzeFoodImage(uri: android.net.Uri) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiResult.value = null
            _aiResult.value = aiCalorieRepository.analyzeFoodImage(uri)
            _isAiLoading.value = false
        }
    }

    fun analyzeFoodText(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiResult.value = null
            _aiResult.value = aiCalorieRepository.analyzeFoodText(query)
            _isAiLoading.value = false
        }
    }

    fun clearAiResult() {
        _aiResult.value = null
    }

    fun saveAiResultToDiary(result: AiFoodResult, grams: Double) {
        if (result.name == null || result.caloriesPer100g == null) return
        viewModelScope.launch {
            val timestamp = _selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.addMealEntry(
                MealEntryEntity(
                    date = timestamp,
                    productId = 0L,
                    productName = result.name,
                    productCalories = result.caloriesPer100g,
                    productProtein = result.proteinPer100g ?: 0.0,
                    productFat = result.fatPer100g ?: 0.0,
                    productCarbs = result.carbsPer100g ?: 0.0,
                    grams = grams
                )
            )
            _aiResult.value = null
        }
    }

    // ==================== Действия ====================
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addEntry(product: ProductEntity, grams: Double) {
        viewModelScope.launch {
            val timestamp = _selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.addMealEntry(
                MealEntryEntity(
                    date = timestamp,
                    productId = product.id,
                    productName = product.name,
                    productCalories = product.caloriesPer100g,
                    productProtein = product.proteinPer100g,
                    productFat = product.fatPer100g,
                    productCarbs = product.carbsPer100g,
                    grams = grams
                )
            )
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteMealEntryById(id)
        }
    }
}