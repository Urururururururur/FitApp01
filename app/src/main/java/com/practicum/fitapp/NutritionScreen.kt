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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel()
) {
    // Читаем данные из ViewModel
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val entries by viewModel.entriesForDay.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val dailyGoal by viewModel.dailyGoal.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val availableDates by viewModel.availableDates.collectAsStateWithLifecycle()

    // Локальные стейты для полей ввода
    var newName by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var newProtein by remember { mutableStateOf("") }
    var newFat by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }

    // Форматирование даты для отображения
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val dateText = dateFormat.format(Date(selectedDate))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ==================== Заголовок ====================
        Text(text = "Питание", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // ==================== Селектор даты ====================
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Дата: $dateText", fontSize = 18.sp)
            TextButton(onClick = { viewModel.setSelectedDate(startOfToday()) }) {
                Text("Сегодня")
            }
        }

        // Список доступных дат (для отладки, потом сделаем красивый пикер)
        if (availableDates.isNotEmpty()) {
            Text(text = "Даты с записями:", fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                availableDates.take(5).forEach { date ->
                    TextButton(onClick = { viewModel.setSelectedDate(date) }) {
                        Text(dateFormat.format(Date(date)))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==================== Сводка за день ====================
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "За день", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                // Съедено
                Text(
                    text = "Съедено: ${"%.0f".format(summary.calories)} ккал",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                // Норма и остаток (если профиль заполнен)
                if (dailyGoal != null) {
                    Text(
                        text = "Норма: ${"%.0f".format(dailyGoal!!)} ккал",
                        fontSize = 14.sp
                    )

                    val remaining = dailyGoal!! - summary.calories
                    Text(
                        text = if (remaining >= 0) {
                            "Осталось: ${"%.0f".format(remaining)} ккал"
                        } else {
                            "Перебор: ${"%.0f".format(-remaining)} ккал"
                        },
                        fontSize = 14.sp,
                        color = if (remaining >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Заполните профиль, чтобы видеть норму",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // БЖУ
                Text(
                    text = "Б: ${"%.1f".format(summary.protein)} г · " +
                            "Ж: ${"%.1f".format(summary.fat)} г · " +
                            "У: ${"%.1f".format(summary.carbs)} г",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Text(text = "Записей: ${summary.entriesCount}", fontSize = 12.sp, color = Color.Gray)
            }
        }

        // ==================== Список записей за день ====================
        Text(
            text = "Съедено за день",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (entries.isEmpty()) {
            Text(text = "Пока ничего не записано", fontSize = 14.sp)
        } else {
            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = entry.productName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${entry.grams} г — ${"%.0f".format(entry.grams * entry.productCalories / 100)} ккал",
                            fontSize = 13.sp
                        )
                    }
                    TextButton(onClick = { viewModel.deleteEntry(entry.id) }) {
                        Text("✕")
                    }
                }
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==================== Справочник: добавить продукт ====================
        Text(
            text = "Справочник продуктов",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newCategory,
            onValueChange = { newCategory = it },
            label = { Text("Категория") },
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
            Text("Добавить продукт")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==================== Список продуктов с кнопкой "Съесть" ====================
        Text(text = "Продукты:", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        products.forEach { product ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${product.name} (${product.category})", fontSize = 15.sp)
                    Text(text = "${product.caloriesPer100g} ккал/100г", fontSize = 12.sp)
                }
                TextButton(onClick = { viewModel.addEntry(product, grams = 100.0) }) {
                    Text("+100г")
                }
                TextButton(onClick = { viewModel.deleteProduct(product) }) {
                    Text("✕")
                }
            }
            HorizontalDivider()
        }
    }
}

/** Возвращает timestamp начала сегодняшнего дня. */
private fun startOfToday(): Long {
    val cal = java.util.Calendar.getInstance()
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}