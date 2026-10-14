package com.practicum.fitapp.presentation.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    val products: StateFlow<List<ProductEntity>> = repository
        .getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(
        name: String,
        calories: Int,
        protein: Double,
        fat: Double,
        carbs: Double
    ) {
        if (name.isBlank()) return

        viewModelScope.launch {
            repository.addProduct(
                ProductEntity(
                    name = name,
                    caloriesPer100g = calories,
                    proteinPer100g = protein,
                    fatPer100g = fat,
                    carbsPer100g = carbs
                )
            )
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}