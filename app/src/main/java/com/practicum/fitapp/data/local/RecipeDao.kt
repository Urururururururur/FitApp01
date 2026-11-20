package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<RecipeIngredientEntity>
)

@Dao
interface RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Long): Flow<RecipeWithIngredients?>

    @Query("""
        SELECT r.* FROM recipes r
        INNER JOIN recipe_category_cross_ref rcc ON r.id = rcc.recipeId
        WHERE (:categoryIdsSize = 0 OR rcc.categoryId IN (:categoryIds))
        AND r.calories BETWEEN :minCalories AND :maxCalories
        AND r.title LIKE '%' || :searchQuery || '%'
        GROUP BY r.id
        HAVING COUNT(DISTINCT rcc.categoryId) = :categoryIdsSize
    """)
    fun getFilteredRecipes(
        categoryIds: List<Long>,
        categoryIdsSize: Int,
        minCalories: Int,
        maxCalories: Int,
        searchQuery: String
    ): Flow<List<RecipeEntity>>

    @Query("""
        SELECT r.* FROM recipes r
        INNER JOIN recipe_category_cross_ref rcc ON r.id = rcc.recipeId
        INNER JOIN collection_recipe_cross_ref crc ON r.id = crc.recipeId
        WHERE rcc.categoryId = :categoryId AND crc.collectionId = :collectionId
    """)
    fun getRecipesByCategoryAndCollection(categoryId: Long, collectionId: Long): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun toggleFavorite(recipeId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Query("INSERT INTO recipe_ingredients (recipeId, productName, grams) VALUES (:recipeId, :productName, :grams)")
    suspend fun insertIngredient(recipeId: Long, productName: String, grams: Double)

    @Query("INSERT INTO recipe_category_cross_ref (recipeId, categoryId) VALUES (:recipeId, :categoryId)")
    suspend fun insertRecipeCategory(recipeId: Long, categoryId: Long)

    // --- Новые методы для сидера ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIngredients(ingredients: List<RecipeIngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecipeCategories(refs: List<RecipeCategoryCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCollectionRecipes(refs: List<CollectionRecipeCrossRef>)
}