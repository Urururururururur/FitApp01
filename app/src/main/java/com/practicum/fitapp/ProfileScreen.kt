package com.practicum.fitapp

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ActivityLevel
import com.practicum.fitapp.data.local.FitnessGoal
import com.practicum.fitapp.data.local.Gender
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.profile.ProfileViewModel

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
    var showNutrition by remember { mutableStateOf(true) }       // <-- НОВОЕ
    var showWorkoutPlan by remember { mutableStateOf(true) }     // <-- НОВОЕ

    var isDataExpanded by remember { mutableStateOf(false) }
    var showWeighDialog by remember { mutableStateOf(false) }
    var newWeightInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Настройки",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
        ) {
            // ==================== КАРТОЧКА НОРМЫ ====================
            if (plan != null) {
                val isOutdated = NutritionCalculator.isWeightOutdated(lastWeight?.date)

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    glassColor = Color(0xFFC8E6C9).copy(alpha = 1.4f),
                    hasShadow = false,
                    hasBorder = false
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Твоя норма", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${"%.0f".format(plan.targetCalories)} ккал/день",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (isOutdated) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "⚠ Норма неактуальна — пора взвеситься", fontSize = 12.sp, color = Color(0xFFC62828))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Б: ${"%.0f".format(plan.proteinGrams)} г · Ж: ${"%.0f".format(plan.fatGrams)} г · У: ${"%.0f".format(plan.carbsGrams)} г",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "BMR: ${"%.0f".format(plan.bmr)} ккал · TDEE: ${"%.0f".format(plan.tdee)} ккал",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showWeighDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            glassColor = Color(0xFF81C784).copy(alpha = 0.7f),
                            hasShadow = false,
                            hasBorder = false
                        ) {
                            Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text("Взвеситься сегодня", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (lastWeight != null) {
                            Text(
                                text = "Последнее: ${DateUtils.formatShort(DateUtils.timestampToLocalDate(lastWeight!!.date))} — ${"%.1f".format(lastWeight!!.weightKg)} кг",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    glassColor = Color.White.copy(alpha = 0.4f),
                    hasShadow = false,
                    hasBorder = false
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Заполните профиль", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Чтобы рассчитать твою норму калорий", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== КАРТОЧКА НАСТРОЕК ДАШБОРДА ====================
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Настройки главного экрана",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsSwitchRow("Питание", showNutrition) { isChecked ->
                        showNutrition = isChecked
                        viewModel.updateDashboardSettings(
                            showWater = showWaterBalance, showActivity = showActivity,
                            quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                            showNutrition = isChecked, showWorkoutPlan = showWorkoutPlan
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    SettingsSwitchRow("Водный баланс", showWaterBalance) { isChecked ->
                        showWaterBalance = isChecked
                        viewModel.updateDashboardSettings(
                            showWater = isChecked, showActivity = showActivity,
                            quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                            showNutrition = showNutrition, showWorkoutPlan = showWorkoutPlan
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    SettingsSwitchRow("Активность (Health Connect)", showActivity) { isChecked ->
                        showActivity = isChecked
                        viewModel.updateDashboardSettings(
                            showWater = showWaterBalance, showActivity = isChecked,
                            quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                            showNutrition = showNutrition, showWorkoutPlan = showWorkoutPlan
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    SettingsSwitchRow("Мой план (Тренировки)", showWorkoutPlan) { isChecked ->
                        showWorkoutPlan = isChecked
                        viewModel.updateDashboardSettings(
                            showWater = showWaterBalance, showActivity = showActivity,
                            quitAlcohol = quitAlcohol, quitNicotine = quitNicotine,
                            showNutrition = showNutrition, showWorkoutPlan = isChecked
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    SettingsSwitchRow("Отказ от алкоголя", quitAlcohol) { isChecked ->
                        quitAlcohol = isChecked
                        viewModel.updateDashboardSettings(
                            showWater = showWaterBalance, showActivity = showActivity,
                            quitAlcohol = isChecked, quitNicotine = quitNicotine,
                            showNutrition = showNutrition, showWorkoutPlan = showWorkoutPlan
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

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
                glassColor = Color.White.copy(alpha = 0.4f),
                hasShadow = false,
                hasBorder = false
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDataExpanded = !isDataExpanded }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Мои данные", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Icon(
                            imageVector = if (isDataExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isDataExpanded) "Свернуть" else "Развернуть",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isDataExpanded) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 30 && newValue.matches(Regex("^[a-zA-Zа-яА-ЯёЁ\\s-]*$"))) {
                                        name = newValue
                                    }
                                },
                                label = { Text("Имя") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = name.isBlank()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(text = "Пол", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
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

                            val ageInt = age.toIntOrNull() ?: 0
                            OutlinedTextField(
                                value = age,
                                onValueChange = { newValue -> if (newValue.length <= 3 && newValue.all { it.isDigit() }) age = newValue },
                                label = { Text("Возраст") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = ageInt < 10 || ageInt > 100,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            if (ageInt < 10 || ageInt > 100) {
                                Text("Возраст должен быть от 10 до 100 лет", color = Color.Red, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            val heightDouble = height.toDoubleOrNull() ?: 0.0
                            OutlinedTextField(
                                value = height,
                                onValueChange = { newValue -> if (newValue.length <= 5 && newValue.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) height = newValue },
                                label = { Text("Рост, см") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = heightDouble < 50.0 || heightDouble > 250.0,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            if (heightDouble < 50.0 || heightDouble > 250.0) {
                                Text("Рост должен быть от 50 до 250 см", color = Color.Red, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            val weightDouble = weight.toDoubleOrNull() ?: 0.0
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { newValue -> if (newValue.length <= 5 && newValue.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) weight = newValue },
                                label = { Text("Вес, кг") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = weightDouble < 20.0 || weightDouble > 300.0,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            if (weightDouble < 20.0 || weightDouble > 300.0) {
                                Text("Вес должен быть от 20 до 300 кг", color = Color.Red, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Уровень активности", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            ActivityLevel.entries.forEach { level ->
                                val isSelected = activityLevel == level.name
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { activityLevel = level.name },
                                    shape = RoundedCornerShape(12.dp),
                                    glassColor = if (isSelected) Color(0xFFC8E6C9).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.3f),
                                    hasShadow = false,
                                    hasBorder = false
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { activityLevel = level.name },
                                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                        )
                                        Text(
                                            text = level.displayName,
                                            fontSize = 14.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Цель", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                FitnessGoal.entries.forEach { fg ->
                                    val isSelected = goal == fg.name
                                    GlassCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { goal = fg.name },
                                        shape = RoundedCornerShape(12.dp),
                                        glassColor = if (isSelected) Color(0xFFC8E6C9).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.3f),
                                        hasShadow = false,
                                        hasBorder = false
                                    ) {
                                        Box(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = fg.displayName,
                                                fontSize = 14.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
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
                                shape = RoundedCornerShape(16.dp),
                                solidColor = Color(0xFF81C784),
                                hasShadow = true,
                                hasBorder = false
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                                    Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            if (showSavedMessage) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Профиль сохранён", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDeleteDialog = true },
                                shape = RoundedCornerShape(16.dp),
                                solidColor = Color(0xFFE53935),
                                hasShadow = true,
                                hasBorder = false
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                                    Text("Удалить все данные", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text("Удалить все данные?") },
                                    text = { Text("Все ваши тренировки, записи о питании, вес и достижения будут удалены. Это действие нельзя отменить.") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            showDeleteDialog = false
                                            viewModel.clearAllData(context) { (context as Activity).recreate() }
                                        }) { Text("Удалить", color = Color.Red) }
                                    },
                                    dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") } }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Диалоги дат и взвешивания (остаются без изменений)
    if (showWeighDialog) {
        AlertDialog(
            onDismissRequest = { showWeighDialog = false },
            title = { Text("Взвешивание") },
            text = {
                OutlinedTextField(
                    value = newWeightInput,
                    onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newWeightInput = v },
                    label = { Text("Текущий вес, кг") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
                }) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { showWeighDialog = false }) { Text("Отмена") } }
        )
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
        Text(text = title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF81C784)
            )
        )
    }
}