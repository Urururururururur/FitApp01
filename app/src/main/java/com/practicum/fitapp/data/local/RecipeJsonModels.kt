package com.practicum.fitapp.data.local

import com.google.gson.annotations.SerializedName

data class RecipesPayload(
    @SerializedName("categories") val categories: List<JsonCategory>,
    @SerializedName("collections") val collections: List<JsonCollection>,
    @SerializedName("recipes") val recipes: List<JsonRecipe>
)

data class JsonCategory(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("icon") val icon: String
)

data class JsonCollection(
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("imageUrl") val imageUrl: String?
)

data class JsonRecipe(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("instructions") val instructions: List<String>,
    @SerializedName("cookTimeMinutes") val cookTimeMinutes: Int,
    @SerializedName("servings") val servings: Int,
    @SerializedName("totalWeightGrams") val totalWeightGrams: Int,
    @SerializedName("calories") val calories: Int,
    @SerializedName("protein") val protein: Double,
    @SerializedName("fat") val fat: Double,
    @SerializedName("carbs") val carbs: Double,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isFavorite") val isFavorite: Boolean,
    @SerializedName("categoryNames") val categoryNames: List<String>,
    @SerializedName("collectionTitles") val collectionTitles: List<String>,
    @SerializedName("ingredients") val ingredients: List<JsonIngredient>
)

data class JsonIngredient(
    @SerializedName("productName") val productName: String,
    @SerializedName("grams") val grams: Double
)