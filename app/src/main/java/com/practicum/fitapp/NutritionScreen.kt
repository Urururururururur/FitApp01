package com.practicum.fitapp

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.nutrition.AiFoodResult
import com.practicum.fitapp.presentation.nutrition.NutritionViewModel
import java.time.LocalDate

@Composable
fun NutritionScreen(
    onAddFood: () -> Unit,
    savedStateHandle: SavedStateHandle,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val entries by viewModel.entriesForDay.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val dailyGoal by viewModel.dailyGoal.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiResult by viewModel.aiResult.collectAsStateWithLifecycle()

    // Стейты для диалогов
    var showGramsDialog by remember { mutableStateOf<ProductEntity?>(null) }
    var gramsInput by remember { mutableStateOf("100") }

    var showTextQueryDialog by remember { mutableStateOf(false) }
    var textQueryInput by remember { mutableStateOf("") }

    var editedGrams by remember { mutableStateOf("100") }

    // Лаунчер для галереи (ВОТ ОН, ИСПРАВЛЕНИЕ ОШИБКИ)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) viewModel.analyzeFoodImage(uri)
    }

    // Обработка возврата из справочника продуктов
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

    // Инициализация граммовки при получении результата от ИИ
    LaunchedEffect(aiResult) {
        if (aiResult != null) {
            editedGrams = "100"
        }
    }

    // Основной контент
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 48.dp)
    ) {
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

        item {
            NutritionWeekStrip(
                selectedDate = selectedDate,
                onDayClick = { viewModel.setSelectedDate(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

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

        // Сводка за день
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

        // Кнопка "+ Добавить продукт"
        item {
            Spacer(modifier = Modifier.height(24.dp))
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddFood() },
                shape = RoundedCornerShape(16.dp),
                solidColor = Color(0xFF81C784),
                hasShadow = true,
                hasBorder = false
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
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

        // Кнопка ИИ по фото
        item {
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { galleryLauncher.launch("image/*") }, // ВЫЗОВ ГАЛЕРЕИ ИЗ UI
                shape = RoundedCornerShape(16.dp),
                solidColor = Color(0xFF6A1B9A),
                hasShadow = true,
                hasBorder = false
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Посчитать ккал по фото", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Кнопка ИИ по тексту
        item {
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTextQueryDialog = true },
                shape = RoundedCornerShape(16.dp),
                solidColor = Color(0xFF6A1B9A),
                hasShadow = true,
                hasBorder = false
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💬", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Спросить ИИ о еде", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // ОТСТУП В КОНЦЕ
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // ============ ДИАЛОГИ ============

    // Диалог граммов для обычного продукта
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

    // Диалог текстового запроса к ИИ
    if (showTextQueryDialog) {
        AlertDialog(
            onDismissRequest = { showTextQueryDialog = false },
            title = { Text("Спросить ИИ") },
            text = {
                OutlinedTextField(
                    value = textQueryInput,
                    onValueChange = { textQueryInput = it },
                    label = { Text("Например: 2 яйца и тост") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.analyzeFoodText(textQueryInput)
                    textQueryInput = ""
                    showTextQueryDialog = false
                }) { Text("Спросить") }
            },
            dismissButton = {
                TextButton(onClick = { showTextQueryDialog = false }) { Text("Отмена") }
            }
        )
    }

    // Индикатор загрузки ИИ
    if (isAiLoading) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { },
            title = { Text("Анализируем...") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Нейросеть обрабатывает запрос...")
                }
            }
        )
    }

    // Результат ИИ
    if (aiResult != null) {
        val result = aiResult!!

        if (result.error != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearAiResult() },
                title = { Text("Ошибка") },
                text = { Text("Не удалось распознать: ${result.error}") },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearAiResult() }) { Text("Понятно") }
                }
            )
        } else {
            // Красивая карточка результата с возможностью правки граммов
            Dialog(onDismissRequest = { viewModel.clearAiResult() }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(24.dp),
                    solidColor = Color.White,
                    hasShadow = true
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "✨ Что на фото/в запросе?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = result.name ?: "Неизвестное блюдо",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Поле ввода граммов
                        OutlinedTextField(
                            value = editedGrams,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) editedGrams = v },
                            label = { Text("Порция, грамм") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Динамический расчет КБЖУ
                        val grams = editedGrams.toDoubleOrNull() ?: 100.0
                        val factor = grams / 100.0
                        val cal = (result.caloriesPer100g ?: 0) * factor
                        val prot = (result.proteinPer100g ?: 0.0) * factor
                        val fat = (result.fatPer100g ?: 0.0) * factor
                        val carb = (result.carbsPer100g ?: 0.0) * factor

                        Text("🔥 ${"%.0f".format(cal)} ккал", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Б: ${"%.1f".format(prot)} г", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Ж: ${"%.1f".format(fat)} г", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("У: ${"%.1f".format(carb)} г", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.clearAiResult() }) {
                                Text("Отмена", color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.saveAiResultToDiary(result, grams) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                            ) {
                                Text("Сохранить", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============ КОМПОНЕНТЫ КАЛЕНДАРЯ ============

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

    val dayNameColor = if (isWeekend) Color(0xFFE57373) else MaterialTheme.colorScheme.onSurfaceVariant
    val dayNumberColor = if (isWeekend) Color(0xFFE57373) else MaterialTheme.colorScheme.onSurface

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