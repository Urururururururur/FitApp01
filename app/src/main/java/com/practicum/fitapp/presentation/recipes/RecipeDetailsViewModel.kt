package com.practicum.fitapp.presentation.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.MealEntryEntity
import com.practicum.fitapp.data.local.RecipeWithIngredients
import com.practicum.fitapp.data.repository.NutritionRepository
import com.practicum.fitapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val nutritionRepository: NutritionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: Long = savedStateHandle["recipeId"] ?: 0L

    val recipeState: StateFlow<RecipeWithIngredients?> = recipeRepository.getRecipeDetails(recipeId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Теперь принимаем количество порций и коллбэк
    fun addRecipeToDiary(mealType: String, servingsCount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val recipeWithIngredients = recipeState.value ?: return@launch
            val recipe = recipeWithIngredients.recipe

            // Считаем граммовку: (общий вес / кол-во порций в рецепте) * сколько порций выбрал юзер
            val singleServingWeight = recipe.totalWeightGrams.toDouble() / recipe.servings
            val finalWeight = singleServingWeight * servingsCount

            val factor = finalWeight / 100.0

            val totalCalories = (recipe.calories * factor).toInt()
            val totalProtein = recipe.protein * factor
            val totalFat = recipe.fat * factor
            val totalCarbs = recipe.carbs * factor

            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val entry = MealEntryEntity(
                date = todayStart,
                productId = recipe.id,
                productName = recipe.title,
                productCalories = totalCalories,
                productProtein = totalProtein,
                productFat = totalFat,
                productCarbs = totalCarbs,
                grams = finalWeight,
                mealType = mealType
            )

            nutritionRepository.addMealEntry(entry)
            onSuccess() // Сообщаем экрану, что всё ок
        }
    }
}