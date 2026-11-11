package com.practicum.fitapp.presentation.dashboard

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val BgColorStart = Color(0xFFE8F5E9)
val BgColorEnd = Color(0xFFF5F7F5)
val CardColor = Color.White
val GreenAccent = Color(0xFF2E7D32)
val OrangeAccent = Color(0xFFFF9800)
val BlueAccent = Color(0xFF1976D2)
val GrayText = Color(0xFF757575)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToNutrition: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToJournal: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {}
) {
    val userName by viewModel.userName.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val consumed by viewModel.consumed.collectAsState()
    val waterConsumed by viewModel.waterConsumed.collectAsState()
    val healthMetrics by viewModel.healthMetrics.collectAsState()
    val todayWorkout by viewModel.todayWorkout.collectAsState()
    val achievements by viewModel.unlockedAchievements.collectAsState()

    val settings by viewModel.settings.collectAsState()

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(viewModel.hcPermissions)) {
            viewModel.loadHealthData()
        } else {
            Log.d("HealthConnect", "Permissions denied.")
        }
    }

    LaunchedEffect(Unit) {
        if (healthMetrics == null) {
            permissionsLauncher.launch(viewModel.hcPermissions)
        } else {
            viewModel.loadHealthData()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(BgColorStart, BgColorEnd))
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 80.dp)
    ) {
        DashboardHeader(userName = userName, onProfileClick = onNavigateToProfile)
        Spacer(modifier = Modifier.height(20.dp))

        // === ПИТАНИЕ ===
        if (settings.showNutrition) {
            NutritionCard(
                consumedCal = consumed.calories,
                goalCal = goals.calories,
                protein = consumed.protein,
                goalProtein = goals.protein,
                fat = consumed.fat,
                goalFat = goals.fat,
                carbs = consumed.carbs,
                goalCarbs = goals.carbs,
                onDiaryClick = onNavigateToNutrition
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // === ВОДА ===
        if (settings.showWater) {
            WaterCard(
                consumedMl = waterConsumed,
                goalMl = viewModel.waterGoal,
                onAddWater = { viewModel.addWater(250) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // === АКТИВНОСТЬ ===
        if (settings.showActivity) {
            ActivityCard(
                healthMetrics = healthMetrics,
                onClick = onNavigateToJournal,
                onRequestPermissions = { permissionsLauncher.launch(viewModel.hcPermissions) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // === КАРТОЧКИ ПРИВЫЧЕК ===
        if (settings.quitAlcohol) {
            HabitTrackerCard(
                title = "Не пью алкоголь",
                quitDate = settings.alcoholQuitDate,
                relapses = settings.alcoholRelapses,
                icon = "🍃",
                color = GreenAccent
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (settings.quitNicotine) {
            HabitTrackerCard(
                title = "Не курю",
                quitDate = settings.nicotineQuitDate,
                relapses = settings.nicotineRelapses,
                icon = "🫁",
                color = BlueAccent
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // === МОЙ ПЛАН (ТРЕНИРОВКИ) ===
        if (settings.showWorkoutPlan) {
            WorkoutPlanCard(workout = todayWorkout, onWorkoutClick = onNavigateToWorkout)
            Spacer(modifier = Modifier.height(24.dp))
        }

        AchievementsRow(achievements = achievements, onCommunityClick = onNavigateToCommunity)
    }
}

// ... (Остальные компоненты остаются без изменений: DashboardHeader, NutritionCard, MacroColumn, WaterCard, ActivityCard, ActivityMetric, WorkoutPlanCard, AchievementsRow, HabitTrackerCard) ...

@Composable
fun DashboardHeader(userName: String, onProfileClick: () -> Unit) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("ru"))
    val dateString = today.format(formatter).replaceFirstChar { it.titlecase() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = dateString, color = GrayText, fontSize = 14.sp)
            Text(text = "Привет, $userName!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFC8E6C9), CircleShape)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Профиль и настройки",
                tint = GreenAccent,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun NutritionCard(
    consumedCal: Double, goalCal: Double,
    protein: Double, goalProtein: Double,
    fat: Double, goalFat: Double,
    carbs: Double, goalCarbs: Double,
    onDiaryClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Питание", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Дневник →",
                    color = GreenAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onDiaryClick() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = String.format("%,.0f", consumedCal), fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ккал", fontSize = 14.sp, color = GrayText, modifier = Modifier.padding(bottom = 6.dp))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format("%,.0f", (goalCal - consumedCal).coerceAtLeast(0.0)),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenAccent
                    )
                    Text(text = "ккал осталось", fontSize = 12.sp, color = GrayText)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val calProgress = (consumedCal / goalCal).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { calProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = GreenAccent,
                trackColor = Color(0xFFE0E0E0)
            )
            Text(
                text = "съедено из ${String.format("%,.0f", goalCal)}",
                fontSize = 12.sp,
                color = GrayText,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MacroColumn("Белки", protein, goalProtein, GreenAccent, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                MacroColumn("Жиры", fat, goalFat, OrangeAccent, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                MacroColumn("Углеводы", carbs, goalCarbs, BlueAccent, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MacroColumn(name: String, current: Double, goal: Double, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = name, fontSize = 12.sp, color = GrayText)
        Row {
            Text(text = "${current.toInt()} / ${goal.toInt()} г", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        val progress = if (goal > 0) (current / goal).toFloat().coerceIn(0f, 1f) else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

@Composable
fun WaterCard(consumedMl: Int, goalMl: Int, onAddWater: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val progress = (consumedMl.toFloat() / goalMl).coerceIn(0f, 1f)
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFFE0E0E0),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = BlueAccent,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = String.format("%.1f л", consumedMl / 1000f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "из ${String.format("%.1f", goalMl / 1000f)} л", fontSize = 10.sp, color = GrayText)
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Водный баланс", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Ещё ${goalMl - consumedMl} мл до цели", fontSize = 12.sp, color = GrayText)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onAddWater,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenAccent)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("250 мл", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    healthMetrics: com.practicum.fitapp.data.HealthConnectManager.HealthMetrics?,
    onClick: () -> Unit,
    onRequestPermissions: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Активность", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Браслет онлайн", fontSize = 10.sp, color = GreenAccent, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (healthMetrics == null) {
                Text(
                    text = "Нет доступа к Health Connect. Нажмите, чтобы разрешить.",
                    color = BlueAccent,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onRequestPermissions() }
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ActivityMetric("Шаги", "${healthMetrics.steps}", OrangeAccent, Modifier.weight(1f))
                    ActivityMetric("Сожжено", "${healthMetrics.calories.toInt()} ккал", OrangeAccent, Modifier.weight(1f))
                    ActivityMetric("Пульс", "${healthMetrics.avgHeartRate ?: 0} уд/мин", BlueAccent, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                val stepsProgress = (healthMetrics.steps / 8000f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { stepsProgress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = OrangeAccent,
                    trackColor = Color(0xFFE0E0E0)
                )
                Text("Синхронизация: сегодня", fontSize = 10.sp, color = GrayText, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun ActivityMetric(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Icon(Icons.Default.Favorite, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = title, fontSize = 12.sp, color = GrayText)
    }
}

@Composable
fun WorkoutPlanCard(workout: com.practicum.fitapp.data.local.WorkoutEntity?, onWorkoutClick: () -> Unit) {
    val workoutName = workout?.name ?: "Новичок: фулбоди"

    val programImage = com.practicum.fitapp.presentation.training.builtInPrograms.find { program ->
        workoutName.contains(program.title, ignoreCase = true) ||
                program.title.contains(workoutName, ignoreCase = true)
    }?.imageResId

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Мой план", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Открыть →", color = GreenAccent, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().clickable { onWorkoutClick() }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (programImage != null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = programImage),
                            contentDescription = "Обложка",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GrayText)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = workoutName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("СЕГОДНЯ · 18:30", fontSize = 12.sp, color = OrangeAccent, fontWeight = FontWeight.Medium)
                    Text("40 мин · 6 упражнений", fontSize = 12.sp, color = GrayText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(16.dp))
                        Text("К тренировке", color = GreenAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsRow(
    achievements: List<com.practicum.fitapp.data.local.Achievement>,
    onCommunityClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable { onCommunityClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Твои награды", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Все 8 →", color = GreenAccent, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (achievements.isEmpty()) {
                Text("Пока нет наград", color = GrayText, fontSize = 14.sp)
            } else {
                achievements.take(3).forEach { achievement ->
                    val imageResId = when (achievement.id) {
                        "first_workout" -> com.practicum.fitapp.R.drawable.ic_ach_first_workout
                        "ten_workouts" -> com.practicum.fitapp.R.drawable.ic_ach_ten_workouts
                        "fifty_workouts" -> com.practicum.fitapp.R.drawable.ic_ach_fifty_workouts
                        "first_ton" -> com.practicum.fitapp.R.drawable.ic_ach_first_ton
                        "ten_tons" -> com.practicum.fitapp.R.drawable.ic_ach_ten_tons
                        "first_5km" -> com.practicum.fitapp.R.drawable.ic_ach_first_5km
                        "first_10km" -> com.practicum.fitapp.R.drawable.ic_ach_first_10km
                        "diary_7" -> com.practicum.fitapp.R.drawable.ic_ach_diary_7
                        "diary_30" -> com.practicum.fitapp.R.drawable.ic_ach_diary_30
                        "ten_exercises" -> com.practicum.fitapp.R.drawable.ic_ach_ten_exercises
                        else -> 0
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageResId != 0) {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = imageResId),
                                        contentDescription = achievement.title,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Text("🏅", fontSize = 24.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = achievement.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text("Сегодня", fontSize = 10.sp, color = GrayText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitTrackerCard(
    title: String,
    quitDate: Long?,
    relapses: Int,
    icon: String,
    color: Color
) {
    val days = if (quitDate != null) {
        (System.currentTimeMillis() - quitDate) / (1000 * 60 * 60 * 24)
    } else 0L

    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "$days дней", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
                if (relapses > 0) {
                    Text(text = "Срывов: $relapses", fontSize = 12.sp, color = GrayText)
                }
            }
        }
    }
}