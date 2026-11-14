package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.CategoryDao
import com.practicum.fitapp.data.local.CategoryEntity
import com.practicum.fitapp.data.local.CategoryType
import com.practicum.fitapp.data.local.CollectionDao
import com.practicum.fitapp.data.local.CollectionEntity
import com.practicum.fitapp.data.local.CollectionWithRecipes
import com.practicum.fitapp.data.local.RecipeDao
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.data.local.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val categoryDao: CategoryDao,
    private val recipeDao: RecipeDao,
    private val collectionDao: CollectionDao
) {
    // --- Чтение (Категории) ---
    fun getCategoriesByType(type: CategoryType): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    // --- Чтение (Рецепты) ---
    fun getFilteredRecipes(
        categoryIds: List<Long>,
        minCalories: Int,
        maxCalories: Int,
        searchQuery: String
    ): Flow<List<RecipeEntity>> =
        recipeDao.getFilteredRecipes(
            categoryIds = categoryIds,
            categoryIdsSize = categoryIds.size, // <-- Передаем размер
            minCalories = minCalories,
            maxCalories = maxCalories,
            searchQuery = searchQuery
        )

    fun getRecipeDetails(recipeId: Long): Flow<RecipeWithIngredients?> =
        recipeDao.getRecipeById(recipeId)

    fun getFavoriteRecipes(): Flow<List<RecipeEntity>> =
        recipeDao.getFavoriteRecipes()

    // --- Чтение (Подборки) ---
    fun getCollections(): Flow<List<CollectionWithRecipes>> =
        collectionDao.getCollectionsWithRecipes()

    // --- Действия ---
    suspend fun toggleFavorite(recipeId: Long, isFavorite: Boolean) =
        recipeDao.toggleFavorite(recipeId, isFavorite)

    // ==================== МЕТОДЫ ДЛЯ СИДЕРА (ЗАПОЛНЕНИЯ БАЗЫ) ====================

    suspend fun insertCategory(category: CategoryEntity): Long {
        // Если в CategoryDao нет метода insertCategory, возвращающего Long,
        // мы просто используем список и возвращаем 0 (или тебе нужно будет добавить этот метод в DAO).
        // Для простоты сидера, если у тебя в DAO метод insertCategories(listOf(category)),
        // то ID мы не получим. Но в прошлом шаге я просил добавить insertCategory(category): Long.
        return categoryDao.insertCategory(category)
    }

    suspend fun insertRecipe(recipe: RecipeEntity): Long =
        recipeDao.insertRecipe(recipe)

    suspend fun insertIngredient(recipeId: Long, name: String, grams: Double) =
        recipeDao.insertIngredient(recipeId, name, grams)

    suspend fun insertRecipeCategory(recipeId: Long, categoryId: Long) =
        recipeDao.insertRecipeCategory(recipeId, categoryId)

    suspend fun insertCollection(collection: CollectionEntity): Long =
        collectionDao.insertCollection(collection)

    suspend fun insertCollectionRecipe(collectionId: Long, recipeId: Long) =
        collectionDao.insertCollectionRecipe(collectionId, recipeId)
}