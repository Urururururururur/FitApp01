package com.practicum.fitapp.presentation.recipes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.CategoryEntity
import com.practicum.fitapp.data.local.CategoryType
import com.practicum.fitapp.data.local.CollectionEntity
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CrossCategory(
    val category: CategoryEntity,
    val recipes: List<RecipeEntity>
)

data class CrossCollection(
    val collection: CollectionEntity,
    val recipes: List<RecipeEntity>
)

@HiltViewModel
class RecipeCategoriesViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryTypeString: String = savedStateHandle["type"] ?: "MEAL"
    val categoryType: CategoryType = try { CategoryType.valueOf(categoryTypeString) } catch (e: Exception) { CategoryType.MEAL }
    private val initialCategoryId: Long = savedStateHandle["categoryId"] ?: savedStateHandle["initialId"] ?: 0L

    private val _selectedCategoryId = MutableStateFlow(initialCategoryId)
    val selectedCategoryId: StateFlow<Long> = _selectedCategoryId.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = repository.getCategoriesByType(categoryType)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recipes: StateFlow<List<RecipeEntity>> = _selectedCategoryId
        .flatMapLatest { catId ->
            if (catId == 0L) flowOf(emptyList())
            else repository.getFilteredRecipes(listOf(catId), 0, 9999, "")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ПЕРЕСЕЧЕНИЯ С КАТЕГОРИЯМИ
    val crossCategories: StateFlow<List<CrossCategory>> = _selectedCategoryId
        .flatMapLatest { currentCatId ->
            if (currentCatId == 0L) {
                Log.d("CrossDebug", "Current ID is 0, returning empty")
                return@flatMapLatest flowOf(emptyList())
            }

            repository.getAllCategories().flatMapLatest { allCats ->
                Log.d("CrossDebug", "All categories count: ${allCats.size}")
                val otherCats = allCats.filter { it.id != currentCatId && it.type != categoryType }
                Log.d("CrossDebug", "Other categories to check: ${otherCats.map { it.name }}")

                if (otherCats.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows = otherCats.map { cat ->
                    repository.getFilteredRecipes(listOf(currentCatId, cat.id), 0, 9999, "")
                        .map { list ->
                            Log.d("CrossDebug", "Category ${cat.name}: found ${list.size} recipes")
                            CrossCategory(cat, list)
                        }
                }
                combine(flows) { list ->
                    val filtered = list.filter { it.recipes.isNotEmpty() }
                    Log.d("CrossDebug", "Final crossCategories count: ${filtered.size}")
                    filtered
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ПЕРЕСЕЧЕНИЯ С ПОДБОРКАМИ
    val crossCollections: StateFlow<List<CrossCollection>> = _selectedCategoryId
        .flatMapLatest { currentCatId ->
            if (currentCatId == 0L) return@flatMapLatest flowOf(emptyList())

            repository.getCollections().flatMapLatest { collectionsList ->
                Log.d("CrossDebug", "All collections count: ${collectionsList.size}")
                if (collectionsList.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows = collectionsList.map { colWithRecipes ->
                    repository.getRecipesByCategoryAndCollection(currentCatId, colWithRecipes.collection.id)
                        .map { recipes ->
                            Log.d("CrossDebug", "Collection ${colWithRecipes.collection.title}: found ${recipes.size} recipes")
                            CrossCollection(colWithRecipes.collection, recipes)
                        }
                }
                combine(flows) { list ->
                    val filtered = list.filter { it.recipes.isNotEmpty() }
                    Log.d("CrossDebug", "Final crossCollections count: ${filtered.size}")
                    filtered
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(id: Long) {
        _selectedCategoryId.value = id
    }
}