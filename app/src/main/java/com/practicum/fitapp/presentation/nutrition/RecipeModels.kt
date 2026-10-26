package com.practicum.fitapp.presentation.nutrition

/** Ингредиент рецепта. */
data class RecipeIngredient(
    val productName: String,
    val grams: Double,
    val kcal: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

/** Модель рецепта. */
data class Recipe(
    val id: String,
    val title: String,
    val mealType: String, // breakfast, lunch, dinner, snack
    val description: String,
    val cookingTimeMinutes: Int,
    val ingredients: List<RecipeIngredient>,
    val totalWeightGrams: Double,
    val totalsKcal: Double,
    val totalsProtein: Double,
    val totalsFat: Double,
    val totalsCarbs: Double,
    val steps: List<String>
)