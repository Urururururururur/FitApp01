package com.practicum.fitapp.presentation.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RecipeCrossViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val catId1: Long = savedStateHandle["catId1"] ?: 0L
    private val catId2: Long = savedStateHandle["catId2"] ?: 0L

    // Загружаем рецепты, которые есть И в первой, И во второй категории
    val recipes: StateFlow<List<RecipeEntity>> = repository.getFilteredRecipes(
        categoryIds = listOf(catId1, catId2),
        minCalories = 0,
        maxCalories = 9999,
        searchQuery = ""
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Получаем названия категорий для заголовка
    val title: StateFlow<String> = repository.getAllCategories()
        .map { allCats ->
            val name1 = allCats.find { it.id == catId1 }?.name ?: ""
            val name2 = allCats.find { it.id == catId2 }?.name ?: ""
            if (name1.isNotEmpty() && name2.isNotEmpty()) "$name1: $name2" else "Рецепты"
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Загрузка...")
}