package com.practicum.fitapp.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class RecipeSeeder(
    private val context: Context,
    private val database: FitAppDatabase
) {
    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val recipeDao = database.recipeDao()
            val categoryDao = database.categoryDao()

            // ИСПРАВЛЕНИЕ: Проверяем наличие категорий, а не рецептов!
            // Если категории уже есть, значит база уже заполнялась.
            val existingCategories = categoryDao.getAllCategories().first()
            if (existingCategories.isNotEmpty()) {
                Log.d("RecipeSeeder", "База уже заполнена (категорий: ${existingCategories.size}). Пропускаем.")
                return@withContext
            }

            Log.d("RecipeSeeder", "Начинаем загрузку рецептов из JSON...")

            val jsonString = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
            val payload = Gson().fromJson(jsonString, RecipesPayload::class.java)

            if (payload == null || payload.recipes.isEmpty()) {
                Log.e("RecipeSeeder", "JSON пустой или не распарсился")
                return@withContext
            }

            // 1. Вставляем категории
            val categoryIdMap = mutableMapOf<String, Long>()
            payload.categories.forEach { jsonCat ->
                val type = try { CategoryType.valueOf(jsonCat.type) } catch (e: Exception) { CategoryType.OTHER }
                val id = categoryDao.insertCategory(CategoryEntity(name = jsonCat.name, type = type, icon = jsonCat.icon))
                categoryIdMap[jsonCat.name] = id
            }

            // 2. Вставляем подборки
            val collectionIdMap = mutableMapOf<String, Long>()
            payload.collections.forEach { jsonCol ->
                val id = database.collectionDao().insertCollection(CollectionEntity(title = jsonCol.title, subtitle = jsonCol.subtitle, imageUrl = jsonCol.imageUrl))
                collectionIdMap[jsonCol.title] = id
            }

            val ingredientsToInsert = mutableListOf<RecipeIngredientEntity>()
            val recipeCategoriesToInsert = mutableListOf<RecipeCategoryCrossRef>()
            val collectionRecipesToInsert = mutableListOf<CollectionRecipeCrossRef>()

            // 3. Вставляем рецепты
            payload.recipes.forEach { jsonRecipe ->
                val recipeEntity = RecipeEntity(
                    title = jsonRecipe.title,
                    description = jsonRecipe.description,
                    instructions = jsonRecipe.instructions.joinToString("\n"),
                    cookTimeMinutes = jsonRecipe.cookTimeMinutes,
                    servings = jsonRecipe.servings,
                    totalWeightGrams = jsonRecipe.totalWeightGrams,
                    calories = jsonRecipe.calories,
                    protein = jsonRecipe.protein,
                    fat = jsonRecipe.fat,
                    carbs = jsonRecipe.carbs,
                    imageUrl = jsonRecipe.imageUrl,
                    isFavorite = jsonRecipe.isFavorite
                )

                val newRecipeId = recipeDao.insertRecipe(recipeEntity)

                // Ингредиенты
                jsonRecipe.ingredients.forEach { ing ->
                    ingredientsToInsert.add(RecipeIngredientEntity(recipeId = newRecipeId, productName = ing.productName, grams = ing.grams))
                }

                // Категории
                jsonRecipe.categoryNames.forEach { catName ->
                    categoryIdMap[catName]?.let { catId ->
                        recipeCategoriesToInsert.add(RecipeCategoryCrossRef(recipeId = newRecipeId, categoryId = catId))
                    }
                }

                // Подборки
                jsonRecipe.collectionTitles.forEach { colTitle ->
                    collectionIdMap[colTitle]?.let { colId ->
                        collectionRecipesToInsert.add(CollectionRecipeCrossRef(collectionId = colId, recipeId = newRecipeId))
                    }
                }
            }

            // 4. Массовая вставка связей
            recipeDao.insertAllIngredients(ingredientsToInsert)
            recipeDao.insertAllRecipeCategories(recipeCategoriesToInsert)
            recipeDao.insertAllCollectionRecipes(collectionRecipesToInsert)

            Log.d("RecipeSeeder", "Успешно загружено ${payload.recipes.size} рецептов!")

        } catch (e: Exception) {
            Log.e("RecipeSeeder", "Ошибка при загрузке рецептов", e)
        }
    }
}