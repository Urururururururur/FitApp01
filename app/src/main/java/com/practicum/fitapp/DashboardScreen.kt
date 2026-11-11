package com.practicum.fitapp.presentation.dashboard

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.practicum.fitapp.ui.theme.*
import com.practicum.fitapp.presentation.common.GlassCard
import androidx.compose.material.icons.outlined.DirectionsWalk


// Импорты для Haze (размытие)
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

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

    // Стейт для Haze (размытие)
    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {

        // ФОН: Сгенерированная картинка
        Image(
            painter = painterResource(id = com.practicum.fitapp.R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState), // Фон работает как источник размытия
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 100.dp)
        ) {
            DashboardHeader(userName = userName, onProfileClick = onNavigateToProfile)
            Spacer(modifier = Modifier.height(24.dp))

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
                    onClick = onNavigateToNutrition,
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (settings.showWater) {
                WaterCard(
                    consumedMl = waterConsumed,
                    goalMl = viewModel.waterGoal,
                    onAddWater = { viewModel.addWater(250) },
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (settings.showActivity) {
                ActivityCard(
                    healthMetrics = healthMetrics,
                    onClick = onNavigateToJournal,
                    onRequestPermissions = { permissionsLauncher.launch(viewModel.hcPermissions) },
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (settings.quitAlcohol) {
                HabitTrackerCard("Не пью алкоголь", settings.alcoholQuitDate, settings.alcoholRelapses, "🍃", PrimaryGreen, hazeState)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (settings.quitNicotine) {
                HabitTrackerCard("Не курю", settings.nicotineQuitDate, settings.nicotineRelapses, "🫁", AccentBlue, hazeState)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (settings.showWorkoutPlan) {
                WorkoutPlanCard(workout = todayWorkout, onWorkoutClick = onNavigateToWorkout, hazeState = hazeState)
                Spacer(modifier = Modifier.height(24.dp))
            }

            AchievementsRow(achievements = achievements, onCommunityClick = onNavigateToCommunity, hazeState = hazeState)
        }
    }
}

// ==================== КАРТОЧКИ ====================

@Composable
fun DashboardHeader(userName: String, onProfileClick: () -> Unit) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("ru"))
    val dateString = today.format(formatter).replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = dateString,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Manrope,
                letterSpacing = 1.sp
            )
            Row {
                Text(text = "Привет, ", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, fontFamily = Manrope)
                Text(text = userName, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGreen, fontFamily = Manrope)
                Text(text = "!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, fontFamily = Manrope)
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(8.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.1f))
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
                .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Настройки",
                tint = PrimaryGreen,
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
    onClick: () -> Unit,
    hazeState: HazeState? = null
) {
    val remainingCal = (goalCal - consumedCal).coerceAtLeast(0.0)

    GlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }, hazeState = hazeState) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Питание", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = String.format("%,.0f", remainingCal),
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryGreen,
                fontFamily = Manrope,
                lineHeight = 48.sp
            )
            Text("ккал осталось", fontSize = 14.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium, fontFamily = Manrope)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Съедено: ${String.format("%,.0f", consumedCal)} из ${String.format("%,.0f", goalCal)}",
                fontSize = 12.sp,
                color = TextSecondary,
                fontFamily = Manrope
            )

            Spacer(modifier = Modifier.height(12.dp))

            val calProgress = (consumedCal / goalCal).toFloat().coerceIn(0f, 1f)
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
                        .background(PrimaryGreen)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MacroCapsule("Белки", protein, goalProtein, PrimaryGreen, Modifier.weight(1f))
                MacroCapsule("Жиры", fat, goalFat, AccentOrange, Modifier.weight(1f))
                MacroCapsule("Углеводы", carbs, goalCarbs, AccentBlue, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MacroCapsule(name: String, current: Double, goal: Double, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("$name ${current.toInt()}/${goal.toInt()}г", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(4.dp))
        val progress = if (goal > 0) (current / goal).toFloat().coerceIn(0f, 1f) else 0f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Composable
fun WaterCard(consumedMl: Int, goalMl: Int, onAddWater: () -> Unit, hazeState: HazeState? = null) {
    GlassCard(modifier = Modifier.fillMaxWidth(), hazeState = hazeState) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val progress = (consumedMl.toFloat() / goalMl).coerceIn(0f, 1f)

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.Black.copy(alpha = 0.1f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(LightGreen, AccentBlue)),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = String.format("%.1f л", consumedMl / 1000f), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary, fontFamily = Manrope)
                    Text(text = "из ${String.format("%.1f", goalMl / 1000f)} л", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Водный баланс", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                Text("Ещё ${goalMl - consumedMl} мл", fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    onClick = onAddWater,
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("250 мл", fontSize = 12.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    healthMetrics: com.practicum.fitapp.data.HealthConnectManager.HealthMetrics?,
    onClick: () -> Unit,
    onRequestPermissions: () -> Unit,
    hazeState: HazeState? = null
) {
    GlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }, hazeState = hazeState) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Активность", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingDot()
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Браслет онлайн", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
                }
            }
            Spacer(modifier = Modifier.height(20.dp)) // Чуть увеличили отступ перед цифрами

            if (healthMetrics == null) {
                Text(
                    text = "Нажмите, чтобы разрешить доступ",
                    color = AccentBlue,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onRequestPermissions() },
                    fontFamily = Manrope
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    // Шаги
                    ActivityMetric(
                        icon = Icons.Outlined.DirectionsWalk,
                        value = "${healthMetrics.steps}",
                        title = "Шаги",
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    // Калории (Убрали "Сожжено")
                    ActivityMetric(
                        icon = Icons.Outlined.LocalFireDepartment,
                        value = "${healthMetrics.calories.toInt()}",
                        title = "Ккал",
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    // Пульс
                    ActivityMetric(
                        icon = Icons.Outlined.MonitorHeart,
                        value = "${healthMetrics.avgHeartRate ?: 0}",
                        title = "Пульс",
                        color = AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, title: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally // Выравниваем всё по центру
    ) {
        // Огромная цифра
        Text(
            text = value,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            fontFamily = Manrope,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Иконка и подпись в одной строке
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
        }
    }
}

@Composable
fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(PrimaryGreen.copy(alpha = alpha), CircleShape)
    )
}

@Composable
fun WorkoutPlanCard(workout: com.practicum.fitapp.data.local.WorkoutEntity?, onWorkoutClick: () -> Unit, hazeState: HazeState? = null) {
    val workoutName = workout?.name ?: "Новичок: фулбоди"
    val programImage = com.practicum.fitapp.presentation.training.builtInPrograms.find { program ->
        workoutName.contains(program.title, ignoreCase = true) || program.title.contains(workoutName, ignoreCase = true)
    }?.imageResId

    GlassCard(modifier = Modifier.fillMaxWidth().clickable { onWorkoutClick() }, hazeState = hazeState) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                if (programImage != null) {
                    Image(
                        painter = painterResource(id = programImage),
                        contentDescription = "Обложка",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = workoutName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                // СТРОКА С ВРЕМЕНЕМ УДАЛЕНА
                Text("40 мин · 6 упражнений", fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = PrimaryGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("К тренировке", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsRow(
    achievements: List<com.practicum.fitapp.data.local.Achievement>,
    onCommunityClick: () -> Unit,
    hazeState: HazeState? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Твои награды", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(12.dp))

        if (achievements.isEmpty()) {
            Text("Пока нет наград", color = TextSecondary, fontSize = 14.sp, fontFamily = Manrope)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(achievements.take(4)) { achievement ->

                    val imageResId = getAchievementImageResId(achievement.id)

                    GlassCard(modifier = Modifier.width(110.dp).clickable { onCommunityClick() }, hazeState = hazeState) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageResId != 0) {
                                    Image(
                                        painter = painterResource(id = imageResId),
                                        contentDescription = achievement.title,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
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
                                color = TextPrimary,
                                fontFamily = Manrope,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitTrackerCard(title: String, quitDate: Long?, relapses: Int, icon: String, color: Color, hazeState: HazeState? = null) {
    val days = if (quitDate != null) (System.currentTimeMillis() - quitDate) / (1000 * 60 * 60 * 24) else 0L

    GlassCard(modifier = Modifier.fillMaxWidth(), hazeState = hazeState) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                Text(text = "$days дней", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color, fontFamily = Manrope)
                if (relapses > 0) {
                    Text(text = "Срывов: $relapses", fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
                }
            }
        }
    }
}
@Composable
fun getAchievementImageResId(id: String): Int {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Формируем имя: ic_ach_ + id (например, ic_ach_first_pr)
    return context.resources.getIdentifier("ic_ach_$id", "drawable", context.packageName)
}