package com.practicum.fitapp.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.CategoryType
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    // === ФИЛЬТРЫ ===
    private val _selectedCategoryIds = MutableStateFlow<List<Long>?>(null) // null = все категории
    val selectedCategoryIds: StateFlow<List<Long>?> = _selectedCategoryIds

    private val _minCalories = MutableStateFlow(0)
    val minCalories: StateFlow<Int> = _minCalories

    private val _maxCalories = MutableStateFlow(1000)
    val maxCalories: StateFlow<Int> = _maxCalories

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // === СПИСОК РЕЦЕПТОВ С УЧЕТОМ ФИЛЬТРОВ ===
    val filteredRecipes: StateFlow<List<RecipeEntity>> = combine(
        _selectedCategoryIds,
        _minCalories,
        _maxCalories,
        _searchQuery
    ) { categoryIds, minCal, maxCal, query ->
        FilterParams(categoryIds, minCal, maxCal, query)
    }.flatMapLatest { params ->
        repository.getFilteredRecipes(
            categoryIds = params.categoryIds,
            minCalories = params.minCalories,
            maxCalories = params.maxCalories,
            searchQuery = params.searchQuery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // === КАТЕГОРИИ ДЛЯ ЭКРАНА ФИЛЬТРОВ ===
    val dietCategories = repository.getCategoriesByType(CategoryType.DIET)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mealCategories = repository.getCategoriesByType(CategoryType.MEAL)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === ПОДБОРКИ ДЛЯ ГЛАВНОГО ЭКРАНА ===
    val collections = repository.getCollections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === ДЕЙСТВИЯ ===
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleCategory(categoryId: Long) {
        val current = _selectedCategoryIds.value
        if (current == null) {
            _selectedCategoryIds.value = listOf(categoryId)
        } else {
            if (current.contains(categoryId)) {
                val newList = current - categoryId
                _selectedCategoryIds.value = if (newList.isEmpty()) null else newList
            } else {
                _selectedCategoryIds.value = current + categoryId
            }
        }
    }

    fun setCalorieRange(min: Int, max: Int) {
        _minCalories.value = min
        _maxCalories.value = max
    }

    fun toggleFavorite(recipeId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, !currentStatus)
        }
    }

    // Вспомогательный класс для combine
    private data class FilterParams(
        val categoryIds: List<Long>?,
        val minCalories: Int,
        val maxCalories: Int,
        val searchQuery: String
    )
}