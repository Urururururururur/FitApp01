package com.practicum.fitapp.data.local

import com.practicum.fitapp.data.repository.RecipeRepository
import kotlinx.coroutines.flow.first // <-- ИМПОРТ ТЕПЕРЬ НА СВОЕМ МЕСТЕ

class RecipeSeeder(private val repository: RecipeRepository) {

    suspend fun seedDatabaseIfEmpty() {
        // Если рецепты уже есть, ничего не делаем
        if (repository.getFilteredRecipes(null, 0, 9999, "").first().isNotEmpty()) return

        // 1. Создаем категории
        val catBreakfast = repository.insertCategory(CategoryEntity(name = "Завтрак", type = CategoryType.MEAL, icon = "🍳"))
        val catLunch = repository.insertCategory(CategoryEntity(name = "Обед", type = CategoryType.MEAL, icon = "🍲"))
        val catVegan = repository.insertCategory(CategoryEntity(name = "Веганский", type = CategoryType.DIET, icon = "🌱"))
        val catFast = repository.insertCategory(CategoryEntity(name = "Быстро", type = CategoryType.METHOD, icon = "⚡"))

        // 2. Создаем рецепты
        val recipe1 = RecipeEntity(
            title = "Овсянка с ягодами",
            description = "Полезный и быстрый завтрак для заряда энергии на весь день.",
            instructions = "1. Залить овсянку кипятком.\n2. Добавить ягоды и мед.",
            cookTimeMinutes = 5,
            servings = 1,
            calories = 350,
            protein = 10.0,
            fat = 5.0,
            carbs = 60.0
        )

        val recipe2 = RecipeEntity(
            title = "Тофу с овощами",
            description = "Легкий веганский обед с высоким содержанием белка.",
            instructions = "1. Обжарить тофу.\n2. Добавить овощи и соевый соус.",
            cookTimeMinutes = 15,
            servings = 2,
            calories = 280,
            protein = 20.0,
            fat = 12.0,
            carbs = 25.0
        )

        val id1 = repository.insertRecipe(recipe1)
        val id2 = repository.insertRecipe(recipe2)

        // 3. Связываем рецепты с категориями
        repository.insertRecipeCategory(id1, catBreakfast)
        repository.insertRecipeCategory(id1, catFast)

        repository.insertRecipeCategory(id2, catLunch)
        repository.insertRecipeCategory(id2, catVegan)

        // 4. Добавляем ингредиенты
        repository.insertIngredient(id1, "Овсяные хлопья", 50.0)
        repository.insertIngredient(id1, "Ягоды", 50.0)

        repository.insertIngredient(id2, "Тофу", 150.0)
        repository.insertIngredient(id2, "Брокколи", 100.0)

        // 5. Создаем подборку "Вдохновение"
        val collectionId = repository.insertCollection(
            CollectionEntity(
                title = "Вдохновение",
                subtitle = "Попробуй что-то новое",
                imageUrl = null
            )
        )

        // Добавляем рецепты в подборку
        repository.insertCollectionRecipe(collectionId, id1)
        repository.insertCollectionRecipe(collectionId, id2)
    }
}