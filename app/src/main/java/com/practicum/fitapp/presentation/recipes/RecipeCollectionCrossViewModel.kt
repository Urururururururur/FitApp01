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
class RecipeCollectionCrossViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long = savedStateHandle["categoryId"] ?: 0L
    private val collectionId: Long = savedStateHandle["collectionId"] ?: 0L

    // Загружаем рецепты, которые есть в этой категории И в этой подборке
    val recipes: StateFlow<List<RecipeEntity>> = repository.getRecipesByCategoryAndCollection(categoryId, collectionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Формируем заголовок (например, "Завтрак: Русская кухня")
    val title: StateFlow<String> = combine(
        repository.getAllCategories(),
        repository.getCollections()
    ) { categories, collections ->
        val catName = categories.find { it.id == categoryId }?.name ?: ""
        val colName = collections.find { it.collection.id == collectionId }?.collection?.title ?: ""

        if (catName.isNotEmpty() && colName.isNotEmpty()) "$catName: $colName" else "Рецепты"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Загрузка...")
}