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

    var newName by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var newProtein by remember { mutableStateOf("") }
    var newFat by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }

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
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newCalories,
                onValueChange = { newCalories = it },
                label = { Text("Ккал/100г") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = newProtein,
                onValueChange = { newProtein = it },
                label = { Text("Белки") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newFat,
                onValueChange = { newFat = it },
                label = { Text("Жиры") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = newCarbs,
                onValueChange = { newCarbs = it },
                label = { Text("Углеводы") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newCategory,
            onValueChange = { newCategory = it },
            label = { Text("Категория (например: мясо, овощи)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newName.isNotBlank()) {
                    viewModel.addProduct(
                        name = newName,
                        category = newCategory.ifBlank { "Прочее" },
                        calories = newCalories.toIntOrNull() ?: 0,
                        protein = newProtein.toDoubleOrNull() ?: 0.0,
                        fat = newFat.toDoubleOrNull() ?: 0.0,
                        carbs = newCarbs.toDoubleOrNull() ?: 0.0
                    )
                    newName = ""
                    newCategory = ""
                    newCalories = ""
                    newProtein = ""
                    newFat = ""
                    newCarbs = ""
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
                    text = "${product.name} (${product.category}) — ${product.caloriesPer100g} ккал\n" +
                            "Б: ${product.proteinPer100g}  Ж: ${product.fatPer100g}  У: ${product.carbsPer100g}",
                    fontSize = 16.sp
                )
                TextButton(onClick = { viewModel.deleteProduct(product) }) {
                    Text("✕")
                }
            }
        }
    }
}