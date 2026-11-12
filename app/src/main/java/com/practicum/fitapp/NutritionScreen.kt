package com.practicum.fitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.nutrition.NutritionViewModel
import com.practicum.fitapp.ui.theme.*
import java.time.LocalDate

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

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
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiResult by viewModel.aiResult.collectAsStateWithLifecycle()

    // Стейты для диалогов
    var showGramsDialog by remember { mutableStateOf<ProductEntity?>(null) }
    var gramsInput by remember { mutableStateOf("100") }
    var showTextQueryDialog by remember { mutableStateOf(false) }
    var textQueryInput by remember { mutableStateOf("") }
    var editedGrams by remember { mutableStateOf("100") }

    // Стейт для Haze
    val hazeState = remember { HazeState() }

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

    // ОСНОВНОЙ КОНТЕЙНЕР
    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        // Фон
        Image(
            painter = painterResource(id = com.practicum.fitapp.R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = DateUtils.formatMonthYear(selectedDate),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                NutritionWeekStrip(
                    selectedDate = selectedDate,
                    onDayClick = { viewModel.setSelectedDate(it) },
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(24.dp))
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
                        color = TextPrimary,
                        fontFamily = Manrope
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // СВОДКА ЗА ДЕНЬ (С КАРТИНКОЙ)
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), hazeState = hazeState) {
                    // ОБОРАЧИВАЕМ В BOX, ЧТОБЫ НАЛОЖИТЬ КАРТИНКУ
                    Box(modifier = Modifier.fillMaxWidth()) {

                        // 1. КАРТИНКА НА ЗАДНЕМ ПЛАНЕ (В правом верхнем углу)
                        Image(
                            painter = painterResource(id = com.practicum.fitapp.R.drawable.img_fruits_bowl),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(180.dp)
                                .padding(top = 12.dp, end = 12.dp)
                                .clip(RoundedCornerShape(topEnd = 20.dp, bottomStart = 16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // 2. ТЕКСТ ПОВЕРХ КАРТИНКИ
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(text = "За день", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(12.dp))

                            if (dailyGoal != null) {
                                val remaining = dailyGoal!! - summary.calories
                                val isOver = remaining < 0
                                val absRemaining = kotlin.math.abs(remaining)

                                // Главный акцент
                                Text(
                                    text = "%,.0f".format(absRemaining).replace(",", " "),
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isOver) Color(0xFFEF4444) else PrimaryGreen,
                                    fontFamily = Manrope,
                                    lineHeight = 48.sp
                                )

                                // Плашка с восклицательным знаком (если перебор)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isOver) Color(0xFFEF4444).copy(alpha = 0.1f) else Color.Transparent)
                                        .padding(horizontal = if (isOver) 8.dp else 0.dp, vertical = if (isOver) 4.dp else 0.dp)
                                ) {
                                    if (isOver) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = if (isOver) "ккал перебор" else "ккал осталось",
                                        fontSize = 14.sp,
                                        color = if (isOver) Color(0xFFEF4444) else PrimaryGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Manrope
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Съедено: ${"%.0f".format(summary.calories)} из ${"%.0f".format(dailyGoal!!)}",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = Manrope
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Прогресс-бар
                                val calProgress = (summary.calories / dailyGoal!!).toFloat().coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.1f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(calProgress)
                                            .fillMaxHeight()
                                            .background(if (isOver) Color(0xFFEF4444) else PrimaryGreen)
                                    )
                                }
                            } else {
                                Text(
                                    text = "Заполните профиль, чтобы видеть норму",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = Manrope
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Макросы (капсулы)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                MacroCapsule("Белки", summary.protein, AccentLightRed, Modifier.weight(1f))
                                MacroCapsule("Жиры", summary.fat, AccentOrange, Modifier.weight(1f))
                                MacroCapsule("Углеводы", summary.carbs, AccentBlue, Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // КНОПКА ДОБАВИТЬ
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGreen.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryGreen)
                        .clickable { onAddFood() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Добавить продукт",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = Manrope
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // СПИСОК ПРОДУКТОВ
            item {
                Text(
                    text = "Съедено за день",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (entries.isEmpty()) {
                item {
                    Text(
                        "Пока ничего не записано",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = Manrope
                    )
                }
            } else {
                items(entries.size) { index ->
                    val entry = entries[index]
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        hazeState = hazeState
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.productName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = Manrope
                                )
                                Text(
                                    text = "${entry.grams} г — ${"%.0f".format(entry.grams * entry.productCalories / 100)} ккал",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = Manrope
                                )
                            }
                            IconButton(onClick = { viewModel.deleteEntry(entry.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Удалить",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // ОТСТУП В КОНЦЕ
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // ============ ДИАЛОГИ ============

    if (showGramsDialog != null) {
        AlertDialog(
            onDismissRequest = { showGramsDialog = null },
            title = { Text(showGramsDialog!!.name, fontFamily = Manrope) },
            text = {
                Column {
                    Text(
                        text = "${showGramsDialog!!.caloriesPer100g} ккал/100г",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = Manrope
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
                Button(
                    onClick = {
                        val grams = gramsInput.toDoubleOrNull()
                        if (grams != null && grams > 0) {
                            viewModel.addEntry(showGramsDialog!!, grams)
                            showGramsDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Добавить", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGramsDialog = null }) {
                    Text("Отмена", color = TextSecondary)
                }
            }
        )
    }

    if (showTextQueryDialog) {
        AlertDialog(
            onDismissRequest = { showTextQueryDialog = false },
            title = { Text("Спросить ИИ", fontFamily = Manrope) },
            text = {
                OutlinedTextField(
                    value = textQueryInput,
                    onValueChange = { textQueryInput = it },
                    label = { Text("Например: 2 яйца и тост") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.analyzeFoodText(textQueryInput)
                        textQueryInput = ""
                        showTextQueryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Спросить", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showTextQueryDialog = false }) { Text("Отмена", color = TextSecondary) }
            }
        )
    }

    if (isAiLoading) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { },
            title = { Text("Анализируем...", fontFamily = Manrope) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = PrimaryGreen)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Нейросеть обрабатывает запрос...", fontFamily = Manrope)
                }
            }
        )
    }

    // РЕЗУЛЬТАТ ИИ
    if (aiResult != null) {
        val result = aiResult!!

        if (result.error != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearAiResult() },
                title = { Text("Ошибка", fontFamily = Manrope) },
                text = { Text("Не удалось распознать: ${result.error}", fontFamily = Manrope) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearAiResult() }) { Text("Понятно") }
                }
            )
        } else {
            Dialog(onDismissRequest = { viewModel.clearAiResult() }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(24.dp),
                    hazeState = hazeState
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "✨ Результат от ИИ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = result.name ?: "Неизвестное блюдо",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            fontFamily = Manrope
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = editedGrams,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) editedGrams = v },
                            label = { Text("Порция, грамм") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val grams = editedGrams.toDoubleOrNull() ?: 100.0
                        val factor = grams / 100.0
                        val cal = (result.caloriesPer100g ?: 0) * factor
                        val prot = (result.proteinPer100g ?: 0.0) * factor
                        val fat = (result.fatPer100g ?: 0.0) * factor
                        val carb = (result.carbsPer100g ?: 0.0) * factor

                        Text("🔥 ${"%.0f".format(cal)} ккал", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary, fontFamily = Manrope)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Б: ${"%.1f".format(prot)} г", fontSize = 14.sp, color = TextSecondary, fontFamily = Manrope)
                            Text("Ж: ${"%.1f".format(fat)} г", fontSize = 14.sp, color = TextSecondary, fontFamily = Manrope)
                            Text("У: ${"%.1f".format(carb)} г", fontSize = 14.sp, color = TextSecondary, fontFamily = Manrope)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.clearAiResult() }) {
                                Text("Отмена", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.saveAiResultToDiary(result, grams) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
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

// ============ КОМПОНЕНТЫ ============

@Composable
private fun NutritionWeekStrip(
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    hazeState: HazeState
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
                hazeState = hazeState,
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
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    val isWeekend = DateUtils.isWeekend(date)

    val dayNameColor = if (isSelected) Color.White.copy(alpha = 0.8f) else if (isWeekend) Color(0xFFEF4444) else TextSecondary
    val dayNumberColor = if (isSelected) Color.White else if (isWeekend) Color(0xFFEF4444) else TextPrimary

    GlassCard(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        hazeState = hazeState,
        solidColor = if (isSelected) PrimaryGreen else null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = DateUtils.dayOfWeekShort(date),
                fontSize = 11.sp,
                color = dayNameColor,
                textAlign = TextAlign.Center,
                fontFamily = Manrope,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = dayNumberColor,
                textAlign = TextAlign.Center,
                fontFamily = Manrope,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MacroCapsule(name: String, current: Double, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("$name", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("${current.toInt()} г", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
    }
}