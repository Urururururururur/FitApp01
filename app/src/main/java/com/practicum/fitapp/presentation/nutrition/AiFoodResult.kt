package com.practicum.fitapp.presentation.nutrition

import com.google.gson.annotations.SerializedName

data class AiFoodResult(
    @SerializedName("name") val name: String? = null,
    @SerializedName("caloriesPer100g") val caloriesPer100g: Int? = null,
    @SerializedName("proteinPer100g") val proteinPer100g: Double? = null,
    @SerializedName("fatPer100g") val fatPer100g: Double? = null,
    @SerializedName("carbsPer100g") val carbsPer100g: Double? = null,
    @SerializedName("error") val error: String? = null
)