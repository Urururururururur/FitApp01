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
import com.practicum.fitapp.presentation.nutrition.builtInProducts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    init {
        viewModelScope.launch {
            if (repository.countBuiltInProducts() == 0) {
                // Указываем полный путь, чтобы Kotlin взял список из BuiltInProducts.kt,
                // а не StateFlow из этого же класса
                repository.insertProducts(com.practicum.fitapp.presentation.nutrition.builtInProducts)
            }
        }
    }
    // Поиск
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun addRecipeToDiary(recipe: Recipe, dateTimestamp: Long, grams: Double) {
        viewModelScope.launch {
            // Вычисляем КБЖУ на 100 г на основе общих данных рецепта
            val kcalPer100 = if (recipe.totalWeightGrams > 0)
                (recipe.totalsKcal / recipe.totalWeightGrams * 100).toInt() else 0
            val proteinPer100 = if (recipe.totalWeightGrams > 0)
                recipe.totalsProtein / recipe.totalWeightGrams * 100 else 0.0
            val fatPer100 = if (recipe.totalWeightGrams > 0)
                recipe.totalsFat / recipe.totalWeightGrams * 100 else 0.0
            val carbsPer100 = if (recipe.totalWeightGrams > 0)
                recipe.totalsCarbs / recipe.totalWeightGrams * 100 else 0.0

            repository.addMealEntry(
                com.practicum.fitapp.data.local.MealEntryEntity(
                    date = dateTimestamp,
                    productId = 0L, // ID 0 означает, что это рецепт
                    productName = recipe.title,
                    productCalories = kcalPer100,
                    productProtein = proteinPer100,
                    productFat = fatPer100,
                    productCarbs = carbsPer100,
                    grams = grams
                )
            )
        }
    }
}