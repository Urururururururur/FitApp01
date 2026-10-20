package com.practicum.fitapp.presentation.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodCatalogViewModel @Inject constructor(
    private val repository: NutritionRepository
) : ViewModel() {

    /** Созданные пользователем продукты. */
    val userProducts: StateFlow<List<ProductEntity>> = repository
        .getUserCreatedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Использованные (были в записях о еде). */
    val usedProducts: StateFlow<List<ProductEntity>> = repository
        .getUsedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Встроенные (справочник). */
    val builtInProducts: StateFlow<List<ProductEntity>> = repository
        .getBuiltInProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Добавить новый продукт (пользовательский). */
    fun addProduct(
        name: String,
        category: String,
        calories: Int,
        protein: Double,
        fat: Double,
        carbs: Double
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addProduct(
                ProductEntity(
                    name = name,
                    category = category.ifBlank { "Прочее" },
                    caloriesPer100g = calories,
                    proteinPer100g = protein,
                    fatPer100g = fat,
                    carbsPer100g = carbs,
                    isUserCreated = true   // ← пользовательский
                )
            )
        }
    }

    /** Удалить пользовательский продукт. */
    fun deleteProduct(product: ProductEntity) {
        if (!product.isUserCreated) return   // защита от удаления встроенных
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}