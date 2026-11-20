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
import com.practicum.fitapp.data.local.RecipeIngredientEntity
import com.practicum.fitapp.data.local.RecipeCategoryCrossRef
import com.practicum.fitapp.data.local.CollectionRecipeCrossRef
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
            categoryIdsSize = categoryIds.size,
            minCalories = minCalories,
            maxCalories = maxCalories,
            searchQuery = searchQuery
        )

    fun getRecipeDetails(recipeId: Long): Flow<RecipeWithIngredients?> =
        recipeDao.getRecipeById(recipeId)

    fun getFavoriteRecipes(): Flow<List<RecipeEntity>> =
        recipeDao.getFavoriteRecipes()

    fun getRecipesByCategoryAndCollection(categoryId: Long, collectionId: Long): Flow<List<RecipeEntity>> =
        recipeDao.getRecipesByCategoryAndCollection(categoryId, collectionId)

    // --- Чтение (Подборки) ---
    fun getCollections(): Flow<List<CollectionWithRecipes>> =
        collectionDao.getCollectionsWithRecipes()

    // --- Действия ---
    suspend fun toggleFavorite(recipeId: Long, isFavorite: Boolean) =
        recipeDao.toggleFavorite(recipeId, isFavorite)

    // ==================== МЕТОДЫ ДЛЯ СИДЕРА ====================

    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)

    suspend fun insertRecipe(recipe: RecipeEntity): Long = recipeDao.insertRecipe(recipe)

    suspend fun insertIngredient(recipeId: Long, name: String, grams: Double) =
        recipeDao.insertIngredient(recipeId, name, grams)

    suspend fun insertRecipeCategory(recipeId: Long, categoryId: Long) =
        recipeDao.insertRecipeCategory(recipeId, categoryId)

    suspend fun insertCollection(collection: CollectionEntity): Long =
        collectionDao.insertCollection(collection)

    suspend fun insertCollectionRecipe(collectionId: Long, recipeId: Long) =
        collectionDao.insertCollectionRecipe(collectionId, recipeId)

    // --- Новые пакетные методы ---
    suspend fun insertAllIngredients(ingredients: List<RecipeIngredientEntity>) =
        recipeDao.insertAllIngredients(ingredients)

    suspend fun insertAllRecipeCategories(refs: List<RecipeCategoryCrossRef>) =
        recipeDao.insertAllRecipeCategories(refs)

    suspend fun insertAllCollectionRecipes(refs: List<CollectionRecipeCrossRef>) =
        recipeDao.insertAllCollectionRecipes(refs)
}