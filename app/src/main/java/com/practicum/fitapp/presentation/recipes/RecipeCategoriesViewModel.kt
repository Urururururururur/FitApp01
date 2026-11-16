package com.practicum.fitapp.presentation.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.CategoryEntity
import com.practicum.fitapp.data.local.CategoryType
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RecipeCategoriesViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Получаем тип (MEAL, DIET, METHOD) из навигации
    private val categoryTypeString: String = savedStateHandle["type"] ?: "MEAL"
    val categoryType: CategoryType = try {
        CategoryType.valueOf(categoryTypeString)
    } catch (e: Exception) {
        CategoryType.MEAL
    }

    // Получаем начальный выбранный ID из навигации
    private val initialCategoryId: Long = savedStateHandle["categoryId"] ?: savedStateHandle["initialId"] ?: 0L

    // Стейт для выбранной категории (чтобы клик по пилюле менял контент)
    private val _selectedCategoryId = MutableStateFlow(initialCategoryId)
    val selectedCategoryId: StateFlow<Long> = _selectedCategoryId.asStateFlow()

    // Загружаем все категории нужного типа (например, только MEAL)
    val categories: StateFlow<List<CategoryEntity>> = repository.getCategoriesByType(categoryType)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Загружаем рецепты для ВЫБРАННОЙ категории
    val recipes: StateFlow<List<RecipeEntity>> = _selectedCategoryId
        .flatMapLatest { catId ->
            if (catId == 0L) {
                // Если id = 0 (не передали), показываем все рецепты этого типа
                // Для этого можно объединить все категории в один запрос, но для простоты
                // сделаем так: если не выбран, покажем пустой список (или переделаем логику)
                flowOf(emptyList())
            } else {
                repository.getFilteredRecipes(
                    categoryIds = listOf(catId),
                    minCalories = 0,
                    maxCalories = 9999,
                    searchQuery = ""
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Метод для переключения выбранной пилюли
    fun selectCategory(id: Long) {
        _selectedCategoryId.value = id
    }
}