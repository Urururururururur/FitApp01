package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.practicum.fitapp.data.local.CollectionEntity
import com.practicum.fitapp.data.local.CollectionRecipeCrossRef
import com.practicum.fitapp.data.local.RecipeEntity
import kotlinx.coroutines.flow.Flow

data class CollectionWithRecipes(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CollectionRecipeCrossRef::class,
            parentColumn = "collectionId",
            entityColumn = "recipeId"
        )
    )
    val recipes: List<RecipeEntity>
)

@Dao
interface CollectionDao {

    @Transaction
    @Query("SELECT * FROM collections")
    fun getCollectionsWithRecipes(): Flow<List<CollectionWithRecipes>>

    // ИСПРАВЛЕНО: Используем @Insert вместо @Query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Query("INSERT INTO collection_recipe_cross_ref (collectionId, recipeId) VALUES (:collectionId, :recipeId)")
    suspend fun insertCollectionRecipe(collectionId: Long, recipeId: Long)
}