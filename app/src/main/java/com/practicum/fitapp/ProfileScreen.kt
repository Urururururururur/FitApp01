package com.practicum.fitapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.practicum.fitapp.data.local.ActivityLevel
import com.practicum.fitapp.data.local.FitnessGoal
import com.practicum.fitapp.data.local.Gender
import com.practicum.fitapp.presentation.profile.ProfileViewModel
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.practicum.fitapp.data.local.WeightEntryEntity
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import com.practicum.fitapp.presentation.common.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    // Стейты полей формы
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE.name) }
    var age by remember { mutableStateOf("25") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE.name) }
    var goal by remember { mutableStateOf(FitnessGoal.MAINTAIN.name) }

    var showSavedMessage by remember { mutableStateOf(false) }
    // Расчёт плана: используем сохранённый профиль (или текущее состояние формы)
    val currentProfile = profile
    val plan = if (currentProfile != null) {
        NutritionCalculator.calculatePlan(currentProfile)
    } else null
    val lastWeight by viewModel.lastWeight.collectAsStateWithLifecycle()
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    var showWeighDialog by remember { mutableStateOf(false) }
    var newWeightInput by remember { mutableStateOf("") }

    // Когда профиль загрузился — заполняем поля его данными
    LaunchedEffect(profile) {
        profile?.let { p ->
            name = p.name
            gender = p.gender
            age = p.age.toString()
            height = p.heightCm.toString()
            weight = p.weightKg.toString()
            activityLevel = p.activityLevel
            goal = p.goal
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Профиль",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Имя
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Пол
        Text(text = "Пол", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Gender.entries.forEach { g ->
                FilterChip(
                    selected = gender == g.name,
                    onClick = { gender = g.name },
                    label = { Text(g.displayName) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Возраст
        OutlinedTextField(
            value = age,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) age = newValue
            },
            label = { Text("Возраст") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Рост
        OutlinedTextField(
            value = height,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() || it == '.' }) height = newValue
            },
            label = { Text("Рост, см") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Вес
        OutlinedTextField(
            value = weight,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() || it == '.' }) weight = newValue
            },
            label = { Text("Вес, кг") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Уровень активности
        Text(text = "Уровень активности", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        ActivityLevel.entries.forEach { level ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = activityLevel == level.name,
                    onClick = { activityLevel = level.name }
                )
                Text(text = level.displayName, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Цель
        Text(text = "Цель", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FitnessGoal.entries.forEach { fg ->
                FilterChip(
                    selected = goal == fg.name,
                    onClick = { goal = fg.name },
                    label = { Text(fg.displayName) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

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
                    goal = goal
                )
                showSavedMessage = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }

        if (showSavedMessage) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Профиль сохранён",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
        if (plan != null) {
            Spacer(modifier = Modifier.height(24.dp))

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
                        text = "Калорий: ${"%.0f".format(plan.targetCalories)} ккал/день",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Предупреждение, если норма неактуальна
                    if (isOutdated) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠ Норма неактуальна — пора взвеситься",
                            fontSize = 12.sp,
                            color = Color(0xFFC62828)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Б: ${"%.0f".format(plan.proteinGrams)} г · " +
                                "Ж: ${"%.0f".format(plan.fatGrams)} г · " +
                                "У: ${"%.0f".format(plan.carbsGrams)} г",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "BMR: ${"%.0f".format(plan.bmr)} ккал · TDEE: ${"%.0f".format(plan.tdee)} ккал",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Кнопка взвешивания
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showWeighDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Взвеситься сегодня")
                    }

                    // Последнее взвешивание
                    if (lastWeight != null) {
                        Text(
                            text = "Последнее взвешивание: ${DateUtils.formatShort(
                                DateUtils.timestampToLocalDate(lastWeight!!.date)
                            )} — ${"%.1f".format(lastWeight!!.weightKg)} кг",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
    if (showWeighDialog) {
        AlertDialog(
            onDismissRequest = { showWeighDialog = false },
            title = { Text("Взвешивание") },
            text = {
                OutlinedTextField(
                    value = newWeightInput,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' }) newWeightInput = newValue
                    },
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