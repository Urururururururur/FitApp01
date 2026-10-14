package com.practicum.fitapp.presentation.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.MealEntryEntity
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: NutritionRepository
) : ViewModel() {

    // ==================== Выбранная дата ====================

    /** Возвращает timestamp начала сегодняшнего дня (00:00:00). */
    private fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // Внутренний state текущего выбранного дня. По умолчанию — сегодня.
    private val _selectedDate = MutableStateFlow(startOfToday())

    /** Публичное read-only представление для UI. */
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    // ==================== Продукты (справочник) ====================

    val products: StateFlow<List<ProductEntity>> = repository
        .getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ==================== Записи за выбранный день ====================

    /**
     * Записи за день. flatMapLatest переключает поток на новый день,
     * когда меняется _selectedDate.
     */
    val entriesForDay: StateFlow<List<MealEntryEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getEntriesByDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Сумма калорий за выбранный день. */
    val totalCalories: StateFlow<Double> = _selectedDate
        .flatMapLatest { date -> repository.getTotalCaloriesByDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    /** Список всех дат, где есть записи. */
    val availableDates: StateFlow<List<Long>> = repository
        .getAllDates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ==================== Действия ====================

    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }

    fun addProduct(name: String, category: String, calories: Int,
                   protein: Double, fat: Double, carbs: Double) {
        if (name.isBlank()) return

        viewModelScope.launch {
            repository.addProduct(
                ProductEntity(
                    name = name,
                    category = category,
                    caloriesPer100g = calories,
                    proteinPer100g = protein,
                    fatPer100g = fat,
                    carbsPer100g = carbs
                )
            )
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    /** Добавить запись о еде — продукт съеден в выбранный день. */
    fun addEntry(product: ProductEntity, grams: Double) {
        viewModelScope.launch {
            repository.addMealEntry(
                MealEntryEntity(
                    date = _selectedDate.value,
                    productId = product.id,
                    productName = product.name,
                    productCalories = product.caloriesPer100g,
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