package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.UserProfileEntity
import com.practicum.fitapp.presentation.calculator.NutritionCalculator
import com.practicum.fitapp.presentation.onboarding.OnboardingViewModel

// Основные цвета
val OnboardingGreen = Color(0xFF2E7D32)
val OnboardingBgStart = Color(0xFFE8F5E9) // Для градиента сверху
val OnboardingBgEnd = Color(0xFFF5F7F5)   // Для градиента снизу
val OnboardingTextGray = Color(0xFF757575)
val OnboardingBorderGray = Color(0xFFE0E0E0)
val OnboardingLightGreen = Color(0xFFE8F5E9)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()

    // Состояния всех полей
    val name by viewModel.name.collectAsStateWithLifecycle()
    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val age by viewModel.age.collectAsStateWithLifecycle()
    val height by viewModel.height.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()
    val activityLevel by viewModel.activityLevel.collectAsStateWithLifecycle()
    val goal by viewModel.goal.collectAsStateWithLifecycle()

    // Проверка валидности
    val isCurrentStepValid = when (currentStep) {
        1 -> name.isNotBlank()
        2 -> (age.toIntOrNull() ?: 0) in 10..100
        3 -> (height.toDoubleOrNull() ?: 0.0) in 50.0..250.0 && (weight.toDoubleOrNull() ?: 0.0) in 20.0..300.0
        else -> true
    }

    // ЗАМЕНИЛИ SURFACE НА BOX С ГРАДИЕНТОМ
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(OnboardingBgStart, OnboardingBgEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            // Индикатор прогресса
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / 6f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = OnboardingGreen,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- ШАГ 1: ИМЯ ---
            if (currentStep == 1) {
                Text("Как тебя зовут?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Давай познакомимся. Как нам обращаться к тебе в приложении?", fontSize = 14.sp, color = OnboardingTextGray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(32.dp))

                Text("Твоё имя", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { newValue ->
                        if (newValue.length <= 30 && newValue.matches(Regex("^[a-zA-Zа-яА-ЯёЁ\\s-]*$"))) {
                            viewModel.updateName(newValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                    singleLine = true,
                    isError = name.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = OnboardingGreen,
                        unfocusedBorderColor = OnboardingBorderGray,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Можно использовать имя или никнейм", fontSize = 12.sp, color = OnboardingTextGray)

                Spacer(modifier = Modifier.height(32.dp))

                Text("Пол", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CustomGenderButton(
                        text = "Женский",
                        isSelected = gender == "FEMALE",
                        onClick = { viewModel.updateGender("FEMALE") },
                        modifier = Modifier.weight(1f)
                    )
                    CustomGenderButton(
                        text = "Мужской",
                        isSelected = gender == "MALE",
                        onClick = { viewModel.updateGender("MALE") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Пол нужен для расчёта индивидуальной нормы калорий", fontSize = 12.sp, color = OnboardingTextGray)
            }

            // --- ШАГ 2: ВОЗРАСТ ---
            if (currentStep == 2) {
                var isManualEntry by remember { mutableStateOf(false) }
                val currentAgeFloat = age.toFloatOrNull() ?: 25f

                Text("Сколько тебе лет?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Возраст поможет точнее подобрать ориентиры для питания и нагрузки.", fontSize = 14.sp, color = OnboardingTextGray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(48.dp))

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ТВОЙ ВОЗРАСТ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnboardingTextGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = "${currentAgeFloat.toInt()}", fontSize = 72.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E1E1E), lineHeight = 72.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "лет", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = OnboardingTextGray, modifier = Modifier.padding(bottom = 12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (!isManualEntry) {
                    Slider(
                        value = currentAgeFloat,
                        onValueChange = { viewModel.updateAge(it.toInt().toString()) },
                        valueRange = 10f..100f,
                        steps = 89,
                        colors = SliderDefaults.colors(thumbColor = OnboardingGreen, activeTrackColor = OnboardingGreen, inactiveTrackColor = Color(0xFFE0E0E0), activeTickColor = Color.Transparent, inactiveTickColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("10", fontSize = 12.sp, color = OnboardingTextGray)
                        Text("50", fontSize = 12.sp, color = OnboardingTextGray)
                        Text("100", fontSize = 12.sp, color = OnboardingTextGray)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(onClick = { isManualEntry = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = OnboardingGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ввести вручную", color = OnboardingGreen, fontWeight = FontWeight.Medium)
                    }
                } else {
                    val ageInt = age.toIntOrNull() ?: 0
                    OutlinedTextField(
                        value = age,
                        onValueChange = { v -> if (v.length <= 3 && v.all { it.isDigit() }) viewModel.updateAge(v) },
                        label = { Text("Твой возраст") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        isError = ageInt < 10 || ageInt > 100,
                        supportingText = { if (ageInt < 10 || ageInt > 100) Text("Возраст от 10 до 100 лет") },
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = OnboardingGreen, unfocusedBorderColor = OnboardingBorderGray)
                    )
                    TextButton(onClick = { isManualEntry = false }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Вернуть слайдер", color = OnboardingTextGray)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Surface(shape = RoundedCornerShape(16.dp), color = Color.White, border = BorderStroke(1.dp, OnboardingBorderGray), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = OnboardingGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Твои данные видны только тебе. Мы используем их для настройки профиля.", fontSize = 12.sp, color = OnboardingTextGray, lineHeight = 16.sp)
                    }
                }
            }

            // --- ШАГ 3: РОСТ И ВЕС ---
            if (currentStep == 3) {
                Text("Твоя отправная точка", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Добавь рост и текущий вес. Это ориентиры, а не оценка твоей формы.", fontSize = 14.sp, color = OnboardingTextGray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))

                MetricSliderCard(
                    title = "Рост",
                    valueStr = height,
                    unit = "см",
                    icon = Icons.Default.Edit,
                    valueRange = 100f..220f,
                    onValueChange = { viewModel.updateHeight(it.toInt().toString()) },
                    isValid = (height.toDoubleOrNull() ?: 0.0) in 50.0..250.0
                )
                Spacer(modifier = Modifier.height(16.dp))
                MetricSliderCard(
                    title = "Вес",
                    valueStr = weight,
                    unit = "кг",
                    icon = Icons.Default.MonitorWeight,
                    valueRange = 30f..150f,
                    onValueChange = { viewModel.updateWeight(it.toInt().toString()) },
                    isValid = (weight.toDoubleOrNull() ?: 0.0) in 20.0..300.0
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = OnboardingTextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Нажми на число, чтобы изменить значение", fontSize = 12.sp, color = OnboardingTextGray)
                }
            }

            // --- ШАГ 4: АКТИВНОСТЬ ---
            if (currentStep == 4) {
                Text("Как проходит твой день?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 34.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Выбери привычный уровень активности, учитывая прогулки, работу и тренировки.", fontSize = 14.sp, color = OnboardingTextGray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(32.dp))

                val levels = listOf(
                    Triple("SEDENTARY", "Сидячий", "В основном сижу, мало двигаюсь"),
                    Triple("MODERATE", "Средний", "Гуляю и тренируюсь 2–3 раза в неделю"),
                    Triple("ACTIVE", "Высокий", "Много движения, спорт 4–5 раз в неделю")
                )

                levels.forEach { (key, title, desc) ->
                    val icon = when (key) {
                        "SEDENTARY" -> Icons.Outlined.Chair
                        "MODERATE" -> Icons.Outlined.DirectionsWalk
                        else -> Icons.Outlined.LocalFireDepartment
                    }
                    SelectionOptionCard(title, desc, icon, activityLevel == key) { viewModel.updateActivityLevel(key) }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Spa, contentDescription = null, tint = OnboardingGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Начнём с реального ритма, без завышенных ожиданий.", fontSize = 12.sp, color = OnboardingTextGray, lineHeight = 16.sp)
                }
            }

            // --- ШАГ 5: ЦЕЛЬ ---
            if (currentStep == 5) {
                Text("К чему идём?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 34.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Выбери главную цель. Подстроим питание и тренировочный план под тебя.", fontSize = 14.sp, color = OnboardingTextGray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(32.dp))

                val goals = listOf(
                    Triple("LOSE", "Похудение", "Постепенно снижать вес"),
                    Triple("GAIN", "Набор массы", "Расти в силе и мышечном объёме"),
                    Triple("MAINTAIN", "Поддержание", "Сохранять вес и чувствовать себя лучше")
                )

                goals.forEach { (key, title, desc) ->
                    val icon = when (key) {
                        "LOSE" -> Icons.Outlined.TrendingDown
                        "GAIN" -> Icons.Outlined.FitnessCenter
                        else -> Icons.Outlined.FavoriteBorder
                    }
                    SelectionOptionCard(title, desc, icon, goal == key) { viewModel.updateGoal(key) }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Eco, contentDescription = null, tint = OnboardingGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Твой прогресс — не только цифра на весах. Энергия и самочувствие тоже важны.", fontSize = 12.sp, color = OnboardingTextGray, lineHeight = 16.sp)
                }
            }

            // --- ШАГ 6: ГОТОВО (ИНФОРМАТИВНЫЙ) ---
            if (currentStep == 6) {
                val tempProfile = UserProfileEntity(
                    id = 1,
                    name = name,
                    gender = gender,
                    age = age.toIntOrNull() ?: 25,
                    heightCm = height.toDoubleOrNull() ?: 175.0,
                    weightKg = weight.toDoubleOrNull() ?: 70.0,
                    activityLevel = activityLevel,
                    goal = goal
                )
                val plan = NutritionCalculator.calculatePlan(tempProfile)

                val goalText = when(goal) {
                    "LOSE" -> "Похудение"
                    "GAIN" -> "Набор массы"
                    else -> "Поддержание"
                }
                val activityText = when(activityLevel) {
                    "SEDENTARY" -> "Сидячая активность"
                    "ACTIVE" -> "Высокая активность"
                    else -> "Средняя активность"
                }

                Text("Твои ориентиры\nна каждый день", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 34.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$name, вот пример норм для $goalText.", fontSize = 14.sp, color = OnboardingTextGray)

                Spacer(modifier = Modifier.height(24.dp))

                // Теги
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag(text = goalText, icon = Icons.Outlined.FavoriteBorder, isActive = true)
                    Tag(text = activityText, icon = null, isActive = false)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Главная карточка калорий (без обводки, с легкой тенью)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Дневная норма", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnboardingGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${"%,.0f".format(plan.targetCalories)}".replace(",", " "),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E1E1E),
                            lineHeight = 56.sp
                        )
                        Text(text = "ккал / день", fontSize = 14.sp, color = OnboardingTextGray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // БЖУ (без кнопки "Изменить вручную")
                Text("Баланс БЖУ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val pKcal = plan.proteinGrams * 4
                    val fKcal = plan.fatGrams * 9
                    val cKcal = plan.carbsGrams * 4
                    val total = pKcal + fKcal + cKcal

                    val pPct = if (total > 0) (pKcal / total * 100).toInt() else 0
                    val fPct = if (total > 0) (fKcal / total * 100).toInt() else 0
                    val cPct = if (total > 0) (cKcal / total * 100).toInt() else 0

                    MacroCard("Белки", plan.proteinGrams.toInt(), pPct, Modifier.weight(1f))
                    MacroCard("Жиры", plan.fatGrams.toInt(), fPct, Modifier.weight(1f))
                    MacroCard("Углеводы", plan.carbsGrams.toInt(), cPct, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Дисклеймер (без обводки, серый фон)
                Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = OnboardingTextGray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Демонстрационный расчёт, не индивидуальная медицинская рекомендация. При ограничениях по здоровью обсуди нормы со специалистом.",
                            fontSize = 11.sp,
                            color = OnboardingTextGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Отступ перед кнопками
            Spacer(modifier = Modifier.height(32.dp))

            // --- КНОПКИ В КОНЦЕ КОНТЕНТА ---
            Button(
                onClick = {
                    if (currentStep < 6) {
                        viewModel.nextStep()
                    } else {
                        viewModel.saveProfile { onComplete() }
                    }
                },
                enabled = isCurrentStepValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OnboardingGreen, disabledContainerColor = Color(0xFFA5D6A7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (currentStep == 6) "Перейти к приложению" else "Продолжить", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (currentStep > 1) {
                TextButton(onClick = { viewModel.previousStep() }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text("Назад", color = OnboardingTextGray)
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Тег (например, "Поддержание")
@Composable
fun Tag(text: String, icon: ImageVector? = null, isActive: Boolean) {
    val bgColor = if (isActive) OnboardingLightGreen else Color.White
    val textColor = if (isActive) OnboardingGreen else OnboardingTextGray
    val border = if (isActive) null else BorderStroke(1.dp, OnboardingBorderGray)

    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

// Карточка БЖУ (без обводки, с легкой тенью)
@Composable
fun MacroCard(title: String, grams: Int, percent: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = OnboardingTextGray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "$grams", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "г", fontSize = 12.sp, color = OnboardingTextGray, modifier = Modifier.padding(bottom = 2.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$percent%", fontSize = 11.sp, color = OnboardingGreen, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SelectionOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) OnboardingLightGreen else Color.White
    val borderColor = if (isSelected) OnboardingGreen else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val iconBgColor = if (isSelected) Color.White else OnboardingBgStart
    val iconTint = if (isSelected) OnboardingGreen else OnboardingTextGray

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        border = if (isSelected) BorderStroke(borderWidth, borderColor) else BorderStroke(1.dp, OnboardingBorderGray)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(iconBgColor, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 12.sp, color = OnboardingTextGray, lineHeight = 16.sp)
            }
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = OnboardingGreen, unselectedColor = Color.LightGray)
            )
        }
    }
}

@Composable
fun MetricSliderCard(
    title: String,
    valueStr: String,
    unit: String,
    icon: ImageVector,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    isValid: Boolean
) {
    var isManual by remember { mutableStateOf(false) }
    val currentValueFloat = valueStr.toFloatOrNull() ?: valueRange.start

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (!isValid) Color.Red else OnboardingBorderGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Icon(icon, contentDescription = null, tint = OnboardingGreen, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isManual) {
                OutlinedTextField(
                    value = valueStr,
                    onValueChange = { newVal ->
                        if (newVal.length <= 5 && newVal.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?$"))) {
                            onValueChange(newVal.toFloatOrNull() ?: 0f)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OnboardingGreen, unfocusedBorderColor = OnboardingBorderGray)
                )
            } else {
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.clickable { isManual = true }) {
                    Text(text = currentValueFloat.toInt().toString(), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E1E1E), lineHeight = 40.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = unit, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnboardingTextGray, modifier = Modifier.padding(bottom = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isManual) {
                Slider(
                    value = currentValueFloat.coerceIn(valueRange.start, valueRange.endInclusive),
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    steps = (valueRange.endInclusive - valueRange.start).toInt() - 1,
                    colors = SliderDefaults.colors(thumbColor = OnboardingGreen, activeTrackColor = OnboardingGreen, inactiveTrackColor = Color(0xFFE0E0E0), activeTickColor = Color.Transparent, inactiveTickColor = Color.Transparent),
                    modifier = Modifier.height(32.dp)
                )
                TextButton(onClick = { isManual = true }, modifier = Modifier.align(Alignment.CenterHorizontally).height(32.dp)) {
                    Text("Или введи вручную", color = OnboardingGreen, fontSize = 12.sp)
                }
            } else {
                TextButton(onClick = { isManual = false }, modifier = Modifier.align(Alignment.CenterHorizontally).height(32.dp)) {
                    Text("Вернуть слайдер", color = OnboardingTextGray, fontSize = 12.sp)
                }
            }

            if (!isValid) {
                Text(text = if (title == "Рост") "Рост должен быть от 50 до 250 см" else "Вес должен быть от 20 до 300 кг", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun CustomGenderButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) OnboardingGreen else OnboardingBorderGray
    val textColor = if (isSelected) OnboardingGreen else Color.Black
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Surface(
        modifier = modifier.height(56.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}