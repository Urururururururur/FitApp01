package com.practicum.fitapp.presentation.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.RecipeWithIngredients
import com.practicum.fitapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle // Автоматически берет аргументы навигации
) : ViewModel() {

    private val recipeId: Long = savedStateHandle["recipeId"] ?: 0L

    // Загружаем данные и превращаем Flow в StateFlow для Compose
    val recipeState: StateFlow<RecipeWithIngredients?> = repository.getRecipeDetails(recipeId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}