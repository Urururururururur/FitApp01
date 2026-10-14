package com.practicum.fitapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.nutrition.NutritionViewModel

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel()
) {
    // Подписываемся на StateFlow из ViewModel.
    // collectAsStateWithLifecycle превращает Flow в Compose-state.
    val products by viewModel.products.collectAsStateWithLifecycle()

    var newMeal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Питание",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Сегодня съедено: ${products.size} блюд",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newMeal,
            onValueChange = { newMeal = it },
            label = { Text("Название продукта") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newMeal.isNotBlank()) {
                    viewModel.addProduct(name = newMeal, calories = 100)
                    newMeal = ""
                }
            }
        ) {
            Text("Добавить")
        }

        Spacer(modifier = Modifier.height(16.dp))

        products.forEach { product ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.name} — ${product.caloriesPer100g} ккал",
                    fontSize = 18.sp
                )
                TextButton(onClick = { viewModel.deleteProduct(product) }) {
                    Text("✕")
                }
            }
        }
    }
}