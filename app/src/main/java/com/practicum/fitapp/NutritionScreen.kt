package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
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

    // Теперь это просто LazyColumn, без обертки Box для кнопки
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // Отступ сверху под статус-бар, по бокам 16dp
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 48.dp)
    ) {
        // Заголовок
        item {
            Text(text = "Питание", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = DateUtils.formatMonthYear(selectedDate),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Недельная лента (в стиле тренировок)
        item {
            NutritionWeekStrip(
                selectedDate = selectedDate,
                onDayClick = { viewModel.setSelectedDate(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Дата + кнопка "Сегодня"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.formatShort(selectedDate),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { viewModel.setSelectedDate(LocalDate.now()) }) {
                    Text("Сегодня", color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Сводка за день (Белая жидкая карточка)
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                hasShadow = false,
                hasBorder = false
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "За день", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Съедено: ${"%.0f".format(summary.calories)} ккал",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (dailyGoal != null) {
                        Text(
                            text = "Норма: ${"%.0f".format(dailyGoal!!)} ккал",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Б: ${"%.1f".format(summary.protein)} г · " +
                                "Ж: ${"%.1f".format(summary.fat)} г · " +
                                "У: ${"%.1f".format(summary.carbs)} г",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Заголовок списка
        item {
            Text(
                text = "Съедено за день",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Список записей
        if (entries.isEmpty()) {
            item {
                Text(
                    "Пока ничего не записано",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(entries.size) { index ->
                val entry = entries[index]
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = entry.productName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "${entry.grams} г — ${"%.0f".format(entry.grams * entry.productCalories / 100)} ккал",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { viewModel.deleteEntry(entry.id) }) {
                            Text("✕", color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                }
            }
        }

        // Кнопка "+ Добавить продукт" (Внутри списка)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddFood() },
                shape = RoundedCornerShape(16.dp),
                solidColor = Color(0xFF81C784), // Плотный зеленый
                hasShadow = true,
                hasBorder = false
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Добавить продукт",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // ОТСТУП ПОСЛЕ КНОПКИ (чтобы можно было прокрутить выше нижнего меню)
        item {
            Spacer(modifier = Modifier.height(100.dp))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gramsInput,
                        onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) gramsInput = v },
                        label = { Text("Граммы") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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

// ============ КОМПОНЕНТЫ КАЛЕНДАРЯ (в стиле Тренировок) ============

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

    // Цвета из темы
    val dayNameColor = if (isWeekend) Color(0xFFE57373) else MaterialTheme.colorScheme.onSurfaceVariant
    val dayNumberColor = if (isWeekend) Color(0xFFE57373) else MaterialTheme.colorScheme.onSurface

    // Светло-зеленый фон
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    }

    GlassCard(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        glassColor = backgroundColor,
        hasShadow = false,
        hasBorder = false
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