package com.practicum.fitapp

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ActivityLevel
import com.practicum.fitapp.data.local.FitnessGoal
import com.practicum.fitapp.data.local.Gender
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.profile.ProfileViewModel
import com.practicum.fitapp.ui.theme.*

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val lastWeight by viewModel.lastWeight.collectAsStateWithLifecycle()
    var showAlcoholDateDialog by remember { mutableStateOf(false) }
    var showNicotineDateDialog by remember { mutableStateOf(false) }
    var dateInput by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE.name) }
    var age by remember { mutableStateOf("25") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE.name) }
    var goal by remember { mutableStateOf(FitnessGoal.MAINTAIN.name) }
    var showSavedMessage by remember { mutableStateOf(false) }

    // Стейты настроек главного экрана
    var showWaterBalance by remember { mutableStateOf(true) }
    var showActivity by remember { mutableStateOf(true) }
    var quitAlcohol by remember { mutableStateOf(false) }
    var quitNicotine by remember { mutableStateOf(false) }
    var showNutrition by remember { mutableStateOf(true) }
    var showWorkoutPlan by remember { mutableStateOf(true) }

    var isDataExpanded by remember { mutableStateOf(false) }
    var showWeighDialog by remember { mutableStateOf(false) }
    var newWeightInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Стейт для Haze
    val hazeState = remember { HazeState() }

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
            showWaterBalance = p.showWaterBalance
            showActivity = p.showActivity
            showNutrition = p.showNutrition
            showWorkoutPlan = p.showWorkoutPlan
        }
    }

    val plan = profile?.let { NutritionCalculator.calculatePlan(it) }

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

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Настройки",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp)
            ) {
                // ==================== КАРТОЧКА НОРМЫ ====================
                if (plan != null) {
                    val isOutdated = NutritionCalculator.isWeightOutdated(lastWeight?.date)

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        hazeState = hazeState,
                        hasBorder = true
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(text = "Твоя норма", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${"%.0f".format(plan.targetCalories)} ккал/день",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryGreen,
                                fontFamily = Manrope
                            )

                            if (isOutdated) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "⚠ Норма неактуальна — пора взвеситься", fontSize = 12.sp, color = Color(0xFFEF4444), fontFamily = Manrope)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Б: ${"%.0f".format(plan.proteinGrams)} г · Ж: ${"%.0f".format(plan.fatGrams)} г · У: ${"%.0f".format(plan.carbsGrams)} г",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                fontFamily = Manrope
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "BMR: ${"%.0f".format(plan.bmr)} ккал · TDEE: ${"%.0f".format(plan.tdee)} ккал",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontFamily = Manrope
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Кнопка взвеситься (Secondary glass pill)
                            OutlinedButton(
                                onClick = { showWeighDialog = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, PrimaryGreen),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen)
                            ) {
                                Icon(Icons.Outlined.MonitorWeight, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Взвеситься сегодня", fontWeight = FontWeight.Bold, fontFamily = Manrope)
                            }

                            if (lastWeight != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Последнее: ${DateUtils.formatShort(DateUtils.timestampToLocalDate(lastWeight!!.date))} — ${"%.1f".format(lastWeight!!.weightKg)} кг",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = Manrope,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                } else {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        hazeState = hazeState,
                        hasBorder = true
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(text = "Заполните профиль", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Text(text = "Чтобы рассчитать твою норму калорий", fontSize = 14.sp, color = TextSecondary, fontFamily = Manrope)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==================== КАРТОЧКА НАСТРОЕК ДАШБОРДА ====================
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    hazeState = hazeState,
                    hasBorder = true
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Настройки главного экрана",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        SettingsSwitchRow("Питание", showNutrition) { isChecked ->
                            showNutrition = isChecked
                            viewModel.updateDashboardSettings(
                                showWater = showWaterBalance, showActivity = showActivity,
                                quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                                showNutrition = isChecked, showWorkoutPlan = showWorkoutPlan
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsSwitchRow("Водный баланс", showWaterBalance) { isChecked ->
                            showWaterBalance = isChecked
                            viewModel.updateDashboardSettings(
                                showWater = isChecked, showActivity = showActivity,
                                quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                                showNutrition = showNutrition, showWorkoutPlan = showWorkoutPlan
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsSwitchRow("Активность (Health Connect)", showActivity) { isChecked ->
                            showActivity = isChecked
                            viewModel.updateDashboardSettings(
                                showWater = showWaterBalance, showActivity = isChecked,
                                quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                                showNutrition = showNutrition, showWorkoutPlan = showWorkoutPlan
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsSwitchRow("Мой план (Тренировки)", showWorkoutPlan) { isChecked ->
                            showWorkoutPlan = isChecked
                            viewModel.updateDashboardSettings(
                                showWater = showWaterBalance, showActivity = showActivity,
                                quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                                showNutrition = showNutrition, showWorkoutPlan = isChecked
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsSwitchRow("Отказ от алкоголя", quitAlcohol) { isChecked ->
                            quitAlcohol = isChecked
                            viewModel.updateDashboardSettings(
                                showWater = showWaterBalance, showActivity = showActivity,
                                quitAlcohol = isChecked, quitNicotine = quitNicotine,
                                showNutrition = showNutrition, showWorkoutPlan = showWorkoutPlan
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsSwitchRow("Отказ от никотина", quitNicotine) { isChecked ->
                            quitNicotine = isChecked
                            viewModel.updateDashboardSettings(
                                showWater = showWaterBalance, showActivity = showActivity,
                                quitAlcohol = quitAlcohol, quitNicotine = isChecked,
                                showNutrition = showNutrition, showWorkoutPlan = showWorkoutPlan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==================== СПОЙЛЕР "МОИ ДАННЫЕ" ====================
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    hazeState = hazeState,
                    hasBorder = true
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDataExpanded = !isDataExpanded }
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Мои данные", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Icon(
                                imageVector = if (isDataExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isDataExpanded) "Свернуть" else "Развернуть",
                                tint = TextSecondary
                            )
                        }

                        if (isDataExpanded) {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                                // Поля ввода (Прозрачные с зеленой рамкой при фокусе)
                                val textFieldColors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryGreen,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.White.copy(alpha = 0.5f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )

                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { newValue ->
                                        if (newValue.length <= 30 && newValue.matches(Regex("^[a-zA-Zа-яА-ЯёЁ\\s-]*$"))) {
                                            name = newValue
                                        }
                                    },
                                    label = { Text("Имя", color = TextSecondary, fontFamily = Manrope) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = name.isBlank(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(text = "Пол", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary, fontFamily = Manrope)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Gender.entries.forEach { g ->
                                        val isSelected = gender == g.name
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.3f))
                                                .border(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.5f), RoundedCornerShape(50))
                                                .clickable { gender = g.name },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = g.displayName,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else TextPrimary,
                                                fontFamily = Manrope
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                val ageInt = age.toIntOrNull() ?: 0
                                OutlinedTextField(
                                    value = age,
                                    onValueChange = { newValue -> if (newValue.length <= 3 && newValue.all { it.isDigit() }) age = newValue },
                                    label = { Text("Возраст", color = TextSecondary, fontFamily = Manrope) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = ageInt < 10 || ageInt > 100,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors
                                )
                                if (ageInt < 10 || ageInt > 100) {
                                    Text("Возраст должен быть от 10 до 100 лет", color = Color(0xFFEF4444), fontSize = 12.sp, fontFamily = Manrope, modifier = Modifier.padding(start = 8.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                val heightDouble = height.toDoubleOrNull() ?: 0.0
                                OutlinedTextField(
                                    value = height,
                                    onValueChange = { newValue -> if (newValue.length <= 5 && newValue.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) height = newValue },
                                    label = { Text("Рост, см", color = TextSecondary, fontFamily = Manrope) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = heightDouble < 50.0 || heightDouble > 250.0,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors
                                )
                                if (heightDouble < 50.0 || heightDouble > 250.0) {
                                    Text("Рост должен быть от 50 до 250 см", color = Color(0xFFEF4444), fontSize = 12.sp, fontFamily = Manrope, modifier = Modifier.padding(start = 8.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                val weightDouble = weight.toDoubleOrNull() ?: 0.0
                                OutlinedTextField(
                                    value = weight,
                                    onValueChange = { newValue -> if (newValue.length <= 5 && newValue.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) weight = newValue },
                                    label = { Text("Вес, кг", color = TextSecondary, fontFamily = Manrope) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = weightDouble < 20.0 || weightDouble > 300.0,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors
                                )
                                if (weightDouble < 20.0 || weightDouble > 300.0) {
                                    Text("Вес должен быть от 20 до 300 кг", color = Color(0xFFEF4444), fontSize = 12.sp, fontFamily = Manrope, modifier = Modifier.padding(start = 8.dp))
                                }
                                Spacer(modifier = Modifier.height(24.dp))

                                // Выбор активности
                                Text(text = "Уровень активности", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary, fontFamily = Manrope)
                                Spacer(modifier = Modifier.height(8.dp))
                                ActivityLevel.entries.forEach { level ->
                                    val isSelected = activityLevel == level.name
                                    GlassCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { activityLevel = level.name }
                                            .then(if (isSelected) Modifier.border(1.5.dp, PrimaryGreen, RoundedCornerShape(16.dp)) else Modifier),
                                        shape = RoundedCornerShape(16.dp),
                                        glassColor = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color.Transparent,
                                        hazeState = hazeState,
                                        hasBorder = !isSelected // Кастомная обводка для выбранного
                                    ) {
                                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                            if (isSelected) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = "Выбрано", tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                                            } else {
                                                Box(modifier = Modifier.size(24.dp).border(2.dp, TextSecondary.copy(alpha = 0.5f), CircleShape))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = level.displayName,
                                                fontSize = 16.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) PrimaryGreen else TextPrimary,
                                                fontFamily = Manrope
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))

                                // Выбор цели
                                Text(text = "Цель", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary, fontFamily = Manrope)
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    FitnessGoal.entries.forEach { fg ->
                                        val isSelected = goal == fg.name
                                        GlassCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { goal = fg.name }
                                                .then(if (isSelected) Modifier.border(1.5.dp, PrimaryGreen, RoundedCornerShape(16.dp)) else Modifier),
                                            shape = RoundedCornerShape(16.dp),
                                            glassColor = if (isSelected) PrimaryGreen.copy(alpha = 0.15f) else Color.Transparent,
                                            hazeState = hazeState,
                                            hasBorder = !isSelected
                                        ) {
                                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                                if (isSelected) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "Выбрано", tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                                                } else {
                                                    Box(modifier = Modifier.size(24.dp).border(2.dp, TextSecondary.copy(alpha = 0.5f), CircleShape))
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = fg.displayName,
                                                    fontSize = 16.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) PrimaryGreen else TextPrimary,
                                                    fontFamily = Manrope
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))

                                // Кнопка Сохранить
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
                                            quitNicotine = quitNicotine,
                                            showWaterBalance = showWaterBalance,
                                            showActivity = showActivity,
                                            showNutrition = showNutrition,
                                            showWorkoutPlan = showWorkoutPlan
                                        )
                                        showSavedMessage = true
                                        isDataExpanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Manrope)
                                }

                                if (showSavedMessage) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Профиль сохранён", color = PrimaryGreen, fontSize = 14.sp, fontFamily = Manrope, modifier = Modifier.align(Alignment.CenterHorizontally))
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Кнопка Удалить (Ghost button)
                                TextButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = TextSecondary, // <-- Изменили цвет иконки
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Удалить все данные",
                                        color = TextSecondary, // <-- Изменили цвет текста
                                        fontFamily = Manrope
                                    )
                                }

                                if (showDeleteDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteDialog = false },
                                        title = { Text("Удалить все данные?", fontFamily = Manrope) },
                                        text = { Text("Все ваши тренировки, записи о питании, вес и достижения будут удалены. Это действие нельзя отменить.", fontFamily = Manrope) },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                showDeleteDialog = false
                                                viewModel.clearAllData(context) { (context as Activity).recreate() }
                                            }) { Text("Удалить", color = Color(0xFFEF4444), fontFamily = Manrope) }
                                        },
                                        dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена", color = TextSecondary, fontFamily = Manrope) } }
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог взвешивания
    if (showWeighDialog) {
        Dialog(onDismissRequest = { showWeighDialog = false }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                hazeState = hazeState,
                hasBorder = true
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Взвешивание", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newWeightInput,
                        onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newWeightInput = v },
                        label = { Text("Текущий вес, кг", color = TextSecondary, fontFamily = Manrope) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showWeighDialog = false }) {
                            Text("Отмена", color = TextSecondary, fontFamily = Manrope)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val w = newWeightInput.toDoubleOrNull()
                                if (w != null && w > 0) {
                                    viewModel.addWeight(w)
                                    newWeightInput = ""
                                    showWeighDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Сохранить", color = Color.White, fontFamily = Manrope)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, color = TextPrimary, fontFamily = Manrope)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = TextSecondary.copy(alpha = 0.3f)
            )
        )
    }
}