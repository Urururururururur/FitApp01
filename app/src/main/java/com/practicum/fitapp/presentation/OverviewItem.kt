package com.practicum.fitapp.presentation.recipes

import com.practicum.fitapp.data.local.CategoryEntity
import com.practicum.fitapp.data.local.CollectionWithRecipes

sealed interface OverviewItem {
    // Блок с горизонтальным списком категорий (например, "Диеты")
    data class CategoryRow(
        val title: String,
        val categories: List<CategoryEntity>
    ) : OverviewItem

    // Блок с большой карточкой подборки (например, "Вдохновение")
    data class CollectionBlock(
        val collection: CollectionWithRecipes
    ) : OverviewItem

    // Блок с быстрым фильтром по калориям
    data object CalorieFilterBlock : OverviewItem

    // Сетка популярных рецептов
    data class PopularRecipesBlock(
        val recipes: List<com.practicum.fitapp.data.local.RecipeEntity>
    ) : OverviewItem
}