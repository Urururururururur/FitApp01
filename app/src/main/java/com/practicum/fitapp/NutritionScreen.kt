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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.practicum.fitapp.data.local.MealEntryEntity
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.nutrition.NutritionViewModel
import com.practicum.fitapp.ui.theme.*
import java.time.LocalDate
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun NutritionScreen(
    onAddFood: (String) -> Unit,
    onNavigateToRecipes: () -> Unit,
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

    // Проверяем, выбран ли сегодняшний день
    val isToday = selectedDate == LocalDate.now()

    // Стейты для диалогов
    var showGramsDialog by remember { mutableStateOf<ProductEntity?>(null) }
    var gramsInput by remember { mutableStateOf("100") }
    var showTextQueryDialog by remember { mutableStateOf(false) }
    var textQueryInput by remember { mutableStateOf("") }
    var editedGrams by remember { mutableStateOf("100") }

    var currentMealType by remember { mutableStateOf("OTHER") }
    val hazeState = remember { HazeState() }

    // Обработка возврата из справочника продуктов
    val selectedProductId = savedStateHandle.getStateFlow<Long?>("selectedProductId", null).collectAsStateWithLifecycle()
    val selectedMealType = savedStateHandle.getStateFlow<String?>("selectedMealType", null).collectAsStateWithLifecycle()

    LaunchedEffect(selectedProductId.value, selectedMealType.value) {
        if (selectedProductId.value != null && selectedMealType.value != null) {
            val product = products.find { it.id == selectedProductId.value }
            if (product != null) {
                showGramsDialog = product
                currentMealType = selectedMealType.value!!
                gramsInput = "100"
                savedStateHandle["selectedProductId"] = null
                savedStateHandle["selectedMealType"] = null
            }
        }
    }

    LaunchedEffect(aiResult) {
        if (aiResult != null) editedGrams = "100"
    }

    // ГРУППИРУЕМ ПРОДУКТЫ ПО ПРИЕМАМ ПИЩИ
    val breakfast = entries.filter { it.mealType == "BREAKFAST" }
    val lunch = entries.filter { it.mealType == "LUNCH" }
    val snack = entries.filter { it.mealType == "SNACK" }
    val dinner = entries.filter { it.mealType == "DINNER" }
    val other = entries.filter { it.mealType == "OTHER" || it.mealType.isBlank() }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Image(
            painter = painterResource(id = com.practicum.fitapp.R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().hazeSource(hazeState),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)
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

            // СКРЫВАЕМ ДАТУ, ЕСЛИ СЕГОДНЯ
            if (!isToday) {
                item {
                    Text(
                        text = DateUtils.formatShort(selectedDate),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // СВОДКА ЗА ДЕНЬ
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), hazeState = hazeState) {
                    Box(modifier = Modifier.fillMaxWidth()) {
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

                        Column(modifier = Modifier.padding(20.dp)) {
                            // МЕНЯЕМ ЗАГОЛОВОК В ЗАВИСИМОСТИ ОТ ДНЯ
                            Text(
                                text = if (isToday) "На сегодня" else "За день",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary,
                                fontFamily = Manrope
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (dailyGoal != null) {
                                val remaining = dailyGoal!! - summary.calories
                                val isOver = remaining < 0
                                val absRemaining = kotlin.math.abs(remaining)

                                Text(
                                    text = "%,.0f".format(absRemaining).replace(",", " "),
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isOver) Color(0xFFEF4444) else PrimaryGreen,
                                    fontFamily = Manrope,
                                    lineHeight = 48.sp
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isOver) Color(0xFFEF4444).copy(alpha = 0.1f) else Color.Transparent)
                                        .padding(horizontal = if (isOver) 8.dp else 0.dp, vertical = if (isOver) 4.dp else 0.dp)
                                ) {
                                    if (isOver) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
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

                                val calProgress = (summary.calories / dailyGoal!!).toFloat().coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.1f))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(calProgress).fillMaxHeight().background(if (isOver) Color(0xFFEF4444) else PrimaryGreen)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))

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

            // КНОПКА РЕЦЕПТОВ
            item {
                OutlinedButton(
                    onClick = onNavigateToRecipes,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen)
                ) {
                    Text("Каталог рецептов", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // КАРТОЧКИ ПРИЕМОВ ПИЩИ
            item {
                MealCard("Завтрак", "BREAKFAST", breakfast, isToday, onAddFood, { viewModel.deleteEntry(it) }, hazeState)
                Spacer(modifier = Modifier.height(12.dp))
                MealCard("Обед", "LUNCH", lunch, isToday, onAddFood, { viewModel.deleteEntry(it) }, hazeState)
                Spacer(modifier = Modifier.height(12.dp))
                MealCard("Перекус", "SNACK", snack, isToday, onAddFood, { viewModel.deleteEntry(it) }, hazeState)
                Spacer(modifier = Modifier.height(12.dp))
                MealCard("Ужин", "DINNER", dinner, isToday, onAddFood, { viewModel.deleteEntry(it) }, hazeState)
                if (other.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    MealCard("Другое", "OTHER", other, isToday, onAddFood, { viewModel.deleteEntry(it) }, hazeState)
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
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
                            viewModel.addEntry(showGramsDialog!!, grams, currentMealType)
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

    // Диалог текстового запроса к ИИ
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
                                onClick = { viewModel.saveAiResultToDiary(result, grams, "OTHER") }, // TODO: Будем передавать currentMealType, когда доработаем UI ИИ
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

// ============ КОМПОНЕНТ КАРТОЧКИ ПРИЕМА ПИЩИ ============

@Composable
fun MealCard(
    title: String,
    mealType: String,
    entries: List<MealEntryEntity>,
    isToday: Boolean,
    onAddClick: (String) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    hazeState: HazeState
) {
    val totalCalories = entries.sumOf { (it.grams * it.productCalories / 100).toInt() }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isToday) Modifier.clickable { onAddClick(mealType) } else Modifier),
        hazeState = hazeState
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                if (totalCalories > 0) {
                    Text(text = "$totalCalories ккал", fontSize = 14.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium, fontFamily = Manrope)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (entries.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Пока ничего не записано", fontSize = 14.sp, color = TextSecondary, fontFamily = Manrope)

                    if (isToday) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить",
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                entries.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.productName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, fontFamily = Manrope)
                            Text("${entry.grams} г — ${(entry.grams * entry.productCalories / 100).toInt()} ккал", fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
                        }

                        if (isToday) {
                            IconButton(onClick = { onDeleteEntry(entry.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Удалить", tint = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                if (isToday) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============ ОСТАЛЬНЫЕ КОМПОНЕНТЫ ============

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
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(1f).fillMaxHeight().background(color)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("${current.toInt()} г", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
    }
}