package com.practicum.fitapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// --- КАТЕГОРИИ ---
@Entity(tableName = "recipe_categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,             // Например, "Завтрак" или "Веганский"
    val type: CategoryType,       // Enum, Room сам сохранит его как строку
    val icon: String              // Эмодзи, например "🍳"
)

// --- РЕЦЕПТЫ ---
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val instructions: String,     // Храним инструкцию как одну строку с переносами \n
    val cookTimeMinutes: Int,
    val servings: Int,
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val imageUrl: String? = null, // Ссылка на картинку
    val isFavorite: Boolean = false // Встроенный флаг избранного
)

// --- ИНГРЕДИЕНТЫ РЕЦЕПТА ---
@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE // Если удаляем рецепт, удаляются и его ингредиенты
        )
    ],
    indices = [Index("recipeId")]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long,
    val productName: String,      // Название (например, "Куриная грудка")
    val grams: Double             // Вес (например, 150.0)
)

// --- ПОДБОРКИ (Коллекции) ---
@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,            // "Вдохновение"
    val subtitle: String,         // "10 лучших рецептов"
    val imageUrl: String? = null  // Картинка обложки
)

// ==================== СВЯЗУЮЩИЕ ТАБЛИЦЫ (MANY-TO-MANY) ====================

// Связь Рецепт <-> Категория
@Entity(
    tableName = "recipe_category_cross_ref",
    primaryKeys = ["recipeId", "categoryId"],
    foreignKeys = [
        ForeignKey(entity = RecipeEntity::class, parentColumns = ["id"], childColumns = ["recipeId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("recipeId"), Index("categoryId")]
)
data class RecipeCategoryCrossRef(
    val recipeId: Long,
    val categoryId: Long
)

// Связь Подборка <-> Рецепт
@Entity(
    tableName = "collection_recipe_cross_ref",
    primaryKeys = ["collectionId", "recipeId"],
    foreignKeys = [
        ForeignKey(entity = CollectionEntity::class, parentColumns = ["id"], childColumns = ["collectionId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = RecipeEntity::class, parentColumns = ["id"], childColumns = ["recipeId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("collectionId"), Index("recipeId")]
)
data class CollectionRecipeCrossRef(
    val collectionId: Long,
    val recipeId: Long
)