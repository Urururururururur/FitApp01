package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ActivityLevel
import com.practicum.fitapp.data.local.FitnessGoal
import com.practicum.fitapp.data.local.Gender
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.profile.ProfileViewModel
import androidx.compose.material.icons.filled.Edit
import android.app.Activity
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val lastWeight by viewModel.lastWeight.collectAsStateWithLifecycle()
    var showAlcoholDateDialog by remember { mutableStateOf(false) }
    var showNicotineDateDialog by remember { mutableStateOf(false) }
    var dateInput by remember { mutableStateOf("") }

    // Стейты полей формы
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE.name) }
    var age by remember { mutableStateOf("25") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE.name) }
    var goal by remember { mutableStateOf(FitnessGoal.MAINTAIN.name) }
    var showSavedMessage by remember { mutableStateOf(false) }

    // Стейт спойлера
    var isDataExpanded by remember { mutableStateOf(false) }

    // Стейты диалога взвешивания
    var showWeighDialog by remember { mutableStateOf(false) }
    var newWeightInput by remember { mutableStateOf("") }

    // Стейты алкоголь и никотин
    var quitAlcohol by remember { mutableStateOf(false) }
    var quitNicotine by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Заполнение формы при загрузке профиля
    LaunchedEffect(profile) {
        profile?.let { p ->
            name = p.name
            gender = p.gender
            age = p.age.toString()
            height = p.heightCm.toString()
            weight = p.weightKg.toString()
            activityLevel = p.activityLevel
            goal = p.goal
            quitAlcohol = p.quitAlcohol
            quitNicotine = p.quitNicotine
        }
    }

    // Расчёт плана
    val plan = profile?.let { NutritionCalculator.calculatePlan(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(text = "Профиль", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // ==================== КАРТОЧКА НОРМЫ (СВЕРХУ) ====================
        if (plan != null) {
            val isOutdated = NutritionCalculator.isWeightOutdated(lastWeight?.date)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Твоя норма",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${"%.0f".format(plan.targetCalories)} ккал/день",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (isOutdated) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠ Норма неактуальна — пора взвеситься",
                            fontSize = 12.sp,
                            color = Color(0xFFC62828)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Б: ${"%.0f".format(plan.proteinGrams)} г · " +
                                "Ж: ${"%.0f".format(plan.fatGrams)} г · " +
                                "У: ${"%.0f".format(plan.carbsGrams)} г",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "BMR: ${"%.0f".format(plan.bmr)} ккал · TDEE: ${"%.0f".format(plan.tdee)} ккал",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showWeighDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Взвеситься сегодня")
                    }

                    if (lastWeight != null) {
                        Text(
                            text = "Последнее: ${DateUtils.formatShort(
                                DateUtils.timestampToLocalDate(lastWeight!!.date)
                            )} — ${"%.1f".format(lastWeight!!.weightKg)} кг",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            // Профиль не заполнен
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Заполните профиль",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Чтобы рассчитать твою норму калорий",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

// Карточка "Не пью"
        if (profile?.quitAlcohol == true) {
            val quitDate = profile?.alcoholQuitDate ?: System.currentTimeMillis()
            val days = (System.currentTimeMillis() - quitDate) / (1000 * 60 * 60 * 24)
            val dateText = DateUtils.formatShort(DateUtils.timestampToLocalDate(quitDate))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Не пью алкоголь", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$days дней трезвости",
                        fontSize = 16.sp,
                        color = Color(0xFF2E7D32)
                    )
                    if (profile!!.alcoholRelapses > 0) {
                        Text(
                            text = "Срывов: ${profile!!.alcoholRelapses}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Строка: дата + карандаш
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "С $dateText",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        IconButton(
                            onClick = {
                                dateInput = dateText
                                showAlcoholDateDialog = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Изменить дату",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Кнопка "Срыв"
                    OutlinedButton(
                        onClick = { viewModel.addAlcoholRelapse() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Зафиксировать срыв")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

// Карточка "Не курю"
        if (profile?.quitNicotine == true) {
            val quitDate = profile?.nicotineQuitDate ?: System.currentTimeMillis()
            val days = (System.currentTimeMillis() - quitDate) / (1000 * 60 * 60 * 24)
            val dateText = DateUtils.formatShort(DateUtils.timestampToLocalDate(quitDate))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Не курю", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$days дней без никотина",
                        fontSize = 16.sp,
                        color = Color(0xFF2E7D32)
                    )
                    if (profile!!.nicotineRelapses > 0) {
                        Text(
                            text = "Срывов: ${profile!!.nicotineRelapses}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Строка: дата + карандаш
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "С $dateText",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        IconButton(
                            onClick = {
                                dateInput = dateText
                                showNicotineDateDialog = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Изменить дату",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.addNicotineRelapse() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Зафиксировать срыв")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }




        // ==================== СПОЙЛЕР "МОИ ДАННЫЕ" ====================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column {
                // Заголовок спойлера
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDataExpanded = !isDataExpanded }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Мои данные",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (isDataExpanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isDataExpanded) "Свернуть" else "Развернуть"
                    )
                }

                // Разворачиваемое содержимое
                if (isDataExpanded) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Имя
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Имя") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Пол
                        Text(text = "Пол", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Gender.entries.forEach { g ->
                                FilterChip(
                                    selected = gender == g.name,
                                    onClick = { gender = g.name },
                                    label = { Text(g.displayName) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Возраст
                        OutlinedTextField(
                            value = age,
                            onValueChange = { v -> if (v.all { it.isDigit() }) age = v },
                            label = { Text("Возраст") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Рост
                        OutlinedTextField(
                            value = height,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) height = v },
                            label = { Text("Рост, см") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Вес
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) weight = v },
                            label = { Text("Вес, кг") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Уровень активности
                        Text(text = "Уровень активности", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        ActivityLevel.entries.forEach { level ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activityLevel == level.name,
                                    onClick = { activityLevel = level.name }
                                )
                                Text(text = level.displayName, fontSize = 13.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Цель
                        Text(text = "Цель", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FitnessGoal.entries.forEach { fg ->
                                FilterChip(
                                    selected = goal == fg.name,
                                    onClick = { goal = fg.name },
                                    label = { Text(fg.displayName) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Вредные привычки", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = quitAlcohol,
                                onClick = { quitAlcohol = !quitAlcohol },
                                label = { Text("Отказ от алкоголя") }
                            )
                            FilterChip(
                                selected = quitNicotine,
                                onClick = { quitNicotine = !quitNicotine },
                                label = { Text("Отказ от никотина") }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))


                        // Кнопка сохранить
                        Button(
                            onClick = {
                                viewModel.saveProfile(
                                    name = name,
                                    gender = gender,
                                    age = age.toIntOrNull() ?: 25,
                                    heightCm = height.toDoubleOrNull() ?: 175.0,
                                    weightKg = weight.toDoubleOrNull() ?: 70.0,
                                    activityLevel = activityLevel,
                                    goal = goal,
                                    quitAlcohol = quitAlcohol,
                                    quitNicotine = quitNicotine
                                )
                                showSavedMessage = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Сохранить")
                        }
                        Spacer(modifier = Modifier.height(32.dp))

                        TextButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Удалить все данные",
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                        }
                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text("Удалить все данные?") },
                                text = {
                                    Text(
                                        "Все ваши тренировки, записи о питании, вес и достижения будут удалены. " +
                                                "Это действие нельзя отменить."
                                    )
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDeleteDialog = false
                                            viewModel.clearAllData {
                                                // Перезапускаем Activity, чтобы все ViewModel и БД обновились
                                                (context as Activity).recreate()
                                            }
                                        }
                                    ) {
                                        Text("Удалить", color = Color.Red)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog = false }) {
                                        Text("Отмена")
                                    }
                                }
                            )
                        }
                     // ← Конец Column

                        if (showSavedMessage) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Профиль сохранён",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }

                    }
                }
            }
        }
    }
// Диалог изменения даты отказа от алкоголя
    if (showAlcoholDateDialog) {
        AlertDialog(
            onDismissRequest = { showAlcoholDateDialog = false },
            title = { Text("Дата отказа от алкоголя") },
            text = {
                Column {
                    Text("Введите дату в формате ДД.ММ.ГГГГ", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Дата") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        // Парсим дату "ДД.ММ.ГГГГ"
                        val parts = dateInput.split(".")
                        if (parts.size == 3) {
                            val day = parts[0].toInt()
                            val month = parts[1].toInt()
                            val year = parts[2].toInt()
                            val date = java.time.LocalDate.of(year, month, day)
                            val timestamp = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                            viewModel.updateAlcoholDate(timestamp)
                            showAlcoholDateDialog = false
                        }
                    } catch (e: Exception) {
                        // Игнорируем некорректный ввод
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlcoholDateDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

// Диалог изменения даты отказа от никотина
    if (showNicotineDateDialog) {
        AlertDialog(
            onDismissRequest = { showNicotineDateDialog = false },
            title = { Text("Дата отказа от никотина") },
            text = {
                Column {
                    Text("Введите дату в формате ДД.ММ.ГГГГ", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Дата") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        val parts = dateInput.split(".")
                        if (parts.size == 3) {
                            val day = parts[0].toInt()
                            val month = parts[1].toInt()
                            val year = parts[2].toInt()
                            val date = java.time.LocalDate.of(year, month, day)
                            val timestamp = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                            viewModel.updateNicotineDate(timestamp)
                            showNicotineDateDialog = false
                        }
                    } catch (e: Exception) {}
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNicotineDateDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
    // Диалог взвешивания
    if (showWeighDialog) {
        AlertDialog(
            onDismissRequest = { showWeighDialog = false },
            title = { Text("Взвешивание") },
            text = {
                OutlinedTextField(
                    value = newWeightInput,
                    onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newWeightInput = v },
                    label = { Text("Текущий вес, кг") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val w = newWeightInput.toDoubleOrNull()
                    if (w != null && w > 0) {
                        viewModel.addWeight(w)
                        newWeightInput = ""
                        showWeighDialog = false
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeighDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}