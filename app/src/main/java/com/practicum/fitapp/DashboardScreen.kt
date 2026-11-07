package com.practicum.fitapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.common.ProgressRing
import com.practicum.fitapp.presentation.dashboard.DashboardViewModel
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val calorieData by viewModel.calorieProgress.collectAsStateWithLifecycle()
    val waterConsumed by viewModel.waterConsumed.collectAsStateWithLifecycle()
    val healthMetrics by viewModel.healthMetrics.collectAsStateWithLifecycle()
    val todayWorkout by viewModel.todayWorkout.collectAsStateWithLifecycle()

    val consumedCal = calorieData.first
    val goalCal = calorieData.second
    val calorieProgress = if (goalCal > 0) (consumedCal / goalCal).toFloat() else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 100.dp)
    ) {
        // 1. Шапка
        item {
            Text(
                text = "Привет, Андрей!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Сегодня",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Блок Калории и Вода
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Калории
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Калории", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProgressRing(
                            progress = calorieProgress,
                            size = 100.dp,
                            color = Color(0xFF4CAF50)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${consumedCal.roundToInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("из ${goalCal.roundToInt()}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Вода
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Вода", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProgressRing(
                            progress = waterConsumed.toFloat() / viewModel.waterGoal,
                            size = 100.dp,
                            color = Color(0xFF2196F3)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${waterConsumed / 1000.0} л", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("из 2 л", fontSize = 10.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                // Кнопка добавления воды
                                Button(
                                    onClick = { viewModel.addWater(250) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3).copy(alpha = 0.2f)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("+ 250 мл", fontSize = 12.sp, color = Color(0xFF2196F3))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Активность (Health Connect)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Активность", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (healthMetrics != null) {
                        val metrics = healthMetrics!!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ActivityStat("👟 Шаги", "${metrics.steps}", "из 8000")
                            ActivityStat("🔥 Сожжено", "${metrics.calories.roundToInt()}", "ккал")
                            ActivityStat("❤️ Пульс", if (metrics.avgHeartRate != null) "${metrics.avgHeartRate}" else "--", "уд/мин")
                        }
                    } else {
                        Text("Нет доступа к Health Connect. Зайдите в Журнал для запроса.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
        // 4. Мой план (Тренировка)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Мой план", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (todayWorkout != null) {
                        Text("Сегодня: ${todayWorkout!!.name}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("${todayWorkout!!.status}", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        Text("Нет тренировок на сегодня", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
        // 5. Награды
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Твои награды", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val achievements by viewModel.unlockedAchievements.collectAsStateWithLifecycle()
                if (achievements.isEmpty()) {
                    Text("Пока нет наград", fontSize = 12.sp, color = Color.Gray)
                } else {
                    achievements.forEach { achievement ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(achievement.emoji, fontSize = 24.sp)
                                Text(achievement.title, fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityStat(title: String, value: String, subtitle: String) {
    Column {
        Text(title, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, fontSize = 10.sp, color = Color.Gray)
    }
}