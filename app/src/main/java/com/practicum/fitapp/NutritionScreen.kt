package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.nutrition.NutritionViewModel
import java.time.LocalDate

@Composable
fun NutritionScreen(
    onAddFood: () -> Unit,
    savedStateHandle: SavedStateHandle,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val entries by viewModel.entriesForDay.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val dailyGoal by viewModel.dailyGoal.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()

    var showGramsDialog by remember { mutableStateOf<ProductEntity?>(null) }
    var gramsInput by remember { mutableStateOf("100") }

    // Обработка возврата из справочника
    val selectedProductId = savedStateHandle.getStateFlow<Long?>("selectedProductId", null).collectAsStateWithLifecycle()
    LaunchedEffect(selectedProductId.value) {
        if (selectedProductId.value != null) {
            val product = products.find { it.id == selectedProductId.value }
            if (product != null) {
                showGramsDialog = product
                gramsInput = "100"
                savedStateHandle["selectedProductId"] = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Заголовок
        Text(text = "Питание", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Заголовок с месяцем и годом
        Text(
            text = DateUtils.formatMonthYear(selectedDate),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

// Недельная лента
        NutritionWeekStrip(
            selectedDate = selectedDate,
            onDayClick = { viewModel.setSelectedDate(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))

// Дата + кнопка "Сегодня"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = DateUtils.formatShort(selectedDate),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            TextButton(onClick = { viewModel.setSelectedDate(LocalDate.now()) }) {
                Text("Сегодня")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Сводка за день
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "За день", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Съедено: ${"%.0f".format(summary.calories)} ккал",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
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
                Text(
                    text = "Б: ${"%.1f".format(summary.protein)} г · " +
                            "Ж: ${"%.1f".format(summary.fat)} г · " +
                            "У: ${"%.1f".format(summary.carbs)} г",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Список записей
        Text(text = "Съедено за день", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (entries.isEmpty()) {
            Text("Пока ничего не записано", fontSize = 14.sp, color = Color.Gray)
        } else {
            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = entry.productName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${entry.grams} г — ${"%.0f".format(entry.grams * entry.productCalories / 100)} ккал",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    TextButton(onClick = { viewModel.deleteEntry(entry.id) }) {
                        Text("✕", color = Color.Red)
                    }
                }
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка добавления
        Button(
            onClick = onAddFood,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Добавить продукт")
        }
    }

    // Диалог ввода граммов
    if (showGramsDialog != null) {
        AlertDialog(
            onDismissRequest = { showGramsDialog = null },
            title = { Text(showGramsDialog!!.name) },
            text = {
                Column {
                    Text(
                        text = "${showGramsDialog!!.caloriesPer100g} ккал/100г",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gramsInput,
                        onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) gramsInput = v },
                        label = { Text("Граммы") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val grams = gramsInput.toDoubleOrNull()
                    if (grams != null && grams > 0) {
                        viewModel.addEntry(showGramsDialog!!, grams)
                        showGramsDialog = null
                    }
                }) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGramsDialog = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}
@Composable
private fun NutritionWeekStrip(
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val week = DateUtils.weekDays(selectedDate)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        week.forEach { date ->
            NutritionDayCell(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDayClick(date) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NutritionDayCell(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWeekend = DateUtils.isWeekend(date)
    val dayNameColor = if (isWeekend) Color(0xFFD32F2F) else Color.Gray
    val dayNumberColor = if (isWeekend) Color(0xFFD32F2F) else Color.Black

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF1976D2)) else null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = DateUtils.dayOfWeekShort(date),
                fontSize = 11.sp,
                color = dayNameColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = dayNumberColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}