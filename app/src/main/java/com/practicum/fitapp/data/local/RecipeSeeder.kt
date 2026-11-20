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
            val collectionDao = database.collectionDao()

            // Проверяем, есть ли уже рецепты (если база заполнена, ничего не делаем)
            val existingCategories = categoryDao.getAllCategories().first()
            if (existingCategories.isNotEmpty()) {
                Log.d("RecipeSeeder", "База уже заполнена. Пропускаем.")
                return@withContext
            }

            // Сюда мы будем сохранять ID существующих и только что созданных категорий/подборок
            val categoryIdMap = mutableMapOf<String, Long>()
            val collectionIdMap = mutableMapOf<String, Long>()

            // 1. Сначала загружаем то, что УЖЕ есть в базе (на случай, если сидер запускался частично)
            existingCategories.forEach { categoryIdMap[it.name] = it.id }
            // Если у тебя есть метод получения всех подборок, можешь добавить и его сюда

            // Список твоих JSON файлов. Просто добавляй новые сюда в будущем!
            val jsonFiles = listOf("recipes.json", "recipes_part2.json")

            for (fileName in jsonFiles) {
                Log.d("RecipeSeeder", "Начинаем чтение файла: $fileName")
                try {
                    val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
                    val payload = Gson().fromJson(jsonString, RecipesPayload::class.java)

                    if (payload == null || payload.recipes.isEmpty()) {
                        Log.w("RecipeSeeder", "Файл $fileName пустой или не распарсился")
                        continue
                    }

                    // 2. Обрабатываем категории из файла
                    payload.categories?.forEach { jsonCat ->
                        if (!categoryIdMap.containsKey(jsonCat.name)) {
                            val type = try { CategoryType.valueOf(jsonCat.type) } catch (e: Exception) { CategoryType.OTHER }
                            // Создаем категорию только если ее еще нет в мапе
                            val newId = categoryDao.insertCategory(CategoryEntity(name = jsonCat.name, type = type, icon = jsonCat.icon))
                            categoryIdMap[jsonCat.name] = newId
                        }
                    }

                    // 3. Обрабатываем подборки из файла
                    payload.collections?.forEach { jsonCol ->
                        if (!collectionIdMap.containsKey(jsonCol.title)) {
                            val newId = collectionDao.insertCollection(CollectionEntity(title = jsonCol.title, subtitle = jsonCol.subtitle, imageUrl = jsonCol.imageUrl))
                            collectionIdMap[jsonCol.title] = newId
                        }
                    }

                    // Списки для пакетной вставки
                    val ingredientsToInsert = mutableListOf<RecipeIngredientEntity>()
                    val recipeCategoriesToInsert = mutableListOf<RecipeCategoryCrossRef>()
                    val collectionRecipesToInsert = mutableListOf<CollectionRecipeCrossRef>()

                    // 4. Обрабатываем рецепты
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
                            // Конвертируем локальное имя картинки в системный URI для Android
                            imageUrl = jsonRecipe.imageUrl?.let {
                                "android.resource://${context.packageName}/drawable/${it.substringBefore(".")}"
                            },
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
                        // Подборки (новые)
                        jsonRecipe.collectionTitles.forEach { colTitle ->
                            // ПРОВЕРКА: если подборки нет в мапе, создаем её прямо сейчас
                            if (!collectionIdMap.containsKey(colTitle)) {
                                val newColId = collectionDao.insertCollection(
                                    CollectionEntity(title = colTitle, subtitle = "", imageUrl = null)
                                )
                                collectionIdMap[colTitle] = newColId
                                Log.d("RecipeSeeder", "Автоматически создана подборка: $colTitle")
                            }

                            // Привязываем рецепт к подборке
                            collectionIdMap[colTitle]?.let { colId ->
                                collectionRecipesToInsert.add(CollectionRecipeCrossRef(collectionId = colId, recipeId = newRecipeId))
                            }
                        }
                    }

                    // Массовая вставка связей для текущего файла
                    if (ingredientsToInsert.isNotEmpty()) recipeDao.insertAllIngredients(ingredientsToInsert)
                    if (recipeCategoriesToInsert.isNotEmpty()) recipeDao.insertAllRecipeCategories(recipeCategoriesToInsert)
                    if (collectionRecipesToInsert.isNotEmpty()) recipeDao.insertAllCollectionRecipes(collectionRecipesToInsert)

                    Log.d("RecipeSeeder", "Успешно загружено ${payload.recipes.size} рецептов из $fileName")

                } catch (e: Exception) {
                    Log.e("RecipeSeeder", "Ошибка при чтении $fileName", e)
                }
            }

            Log.d("RecipeSeeder", "Общая загрузка завершена!")

        } catch (e: Exception) {
            Log.e("RecipeSeeder", "Критическая ошибка при загрузке рецептов", e)
        }
    }
}