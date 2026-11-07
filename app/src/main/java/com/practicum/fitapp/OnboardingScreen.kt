package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.onboarding.OnboardingViewModel

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Индикатор прогресса
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / 6f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = Color(0xFF2E7D32),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- ШАГ 1: ИМЯ ---
            if (currentStep == 1) {
                Text("Давай познакомимся", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Как нам к тебе обращаться?", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Твое имя") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Пол", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = gender == "MALE",
                        onClick = { viewModel.updateGender("MALE") },
                        label = { Text("Мужской") }
                    )
                    FilterChip(
                        selected = gender == "FEMALE",
                        onClick = { viewModel.updateGender("FEMALE") },
                        label = { Text("Женский") }
                    )
                }
            }

            // --- ШАГ 2: ВОЗРАСТ ---
            if (currentStep == 2) {
                Text("Сколько тебе лет?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Возраст помогает нам точнее рассчитать норму.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { v -> if (v.all { it.isDigit() }) viewModel.updateAge(v) },
                    label = { Text("Твой возраст") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // --- ШАГ 3: РОСТ И ВЕС ---
            if (currentStep == 3) {
                Text("Твоя отправная точка", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Добавь рост и текущий вес.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = height,
                    onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) viewModel.updateHeight(v) },
                    label = { Text("Рост (см)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) viewModel.updateWeight(v) },
                    label = { Text("Вес (кг)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // --- ШАГ 4: АКТИВНОСТЬ ---
            if (currentStep == 4) {
                Text("Как проходит твой день?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Выбери привычный уровень активности.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                val levels = listOf(
                    "SEDENTARY" to "Сидячий",
                    "LIGHT" to "Легкий",
                    "MODERATE" to "Средний",
                    "ACTIVE" to "Высокий",
                    "VERY_ACTIVE" to "Очень высокий"
                )

                levels.forEach { (key, label) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.updateActivityLevel(key) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (activityLevel == key) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
                        ),
                        border = if (activityLevel == key) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2E7D32)) else null
                    ) {
                        Text(label, modifier = Modifier.padding(16.dp), fontSize = 16.sp)
                    }
                }
            }

            // --- ШАГ 5: ЦЕЛЬ ---
            if (currentStep == 5) {
                Text("К чему идем?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Выбери главную цель.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                val goals = listOf(
                    "LOSE" to "Похудение",
                    "MAINTAIN" to "Поддержание",
                    "GAIN" to "Набор массы"
                )

                goals.forEach { (key, label) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.updateGoal(key) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (goal == key) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
                        ),
                        border = if (goal == key) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2E7D32)) else null
                    ) {
                        Text(label, modifier = Modifier.padding(16.dp), fontSize = 16.sp)
                    }
                }
            }

            // --- ШАГ 6: ГОТОВО ---
            if (currentStep == 6) {
                Text("Все готово, $name!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Твой профиль настроен. Давай посмотрим на твою норму калорий.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Отлично!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Нажми «Продолжить», чтобы перейти на Главный экран.", fontSize = 14.sp)
                    }
                }
            }

            // Кнопка "Продолжить"
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (currentStep < 6) {
                        viewModel.nextStep()
                    } else {
                        viewModel.saveProfile { onComplete() }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (currentStep == 6) "Перейти к приложению" else "Продолжить", fontSize = 16.sp)
            }

            // Кнопка "Назад" (если не первый шаг)
            if (currentStep > 1) {
                TextButton(
                    onClick = { viewModel.previousStep() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Назад", color = Color.Gray)
                }
            }
        }
    }
}