package com.practicum.fitapp.presentation.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.CollectionWithRecipes
import com.practicum.fitapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val collectionId: Long = savedStateHandle["collectionId"] ?: 0L

    // Находим нужную подборку в общем потоке всех подборок
    val collectionState: StateFlow<CollectionWithRecipes?> = repository.getCollections()
        .map { collectionsList ->
            collectionsList.find { it.collection.id == collectionId }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}