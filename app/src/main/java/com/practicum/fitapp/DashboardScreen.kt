package com.practicum.fitapp.presentation.dashboard

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.ui.theme.*
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

    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Image(
            painter = painterResource(id = com.practicum.fitapp.R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 100.dp)
        ) {
            DashboardHeader(
                userName = userName,
                onProfileClick = onNavigateToProfile,
                hazeState = hazeState
            )
            Spacer(modifier = Modifier.height(16.dp))

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

            // КАРУСЕЛЬ
            if (settings.showActivity) {
                ActivityCarousel(
                    healthMetrics = healthMetrics,
                    onClick = onNavigateToJournal,
                    onRequestPermissions = { permissionsLauncher.launch(viewModel.hcPermissions) },
                    hazeState = hazeState
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // БЛОК С ТРЕНИРОВКОЙ
            if (settings.showWorkoutPlan) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Активность", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                }
                Spacer(modifier = Modifier.height(12.dp))

                WorkoutPlanCard(workout = todayWorkout, onWorkoutClick = onNavigateToWorkout, hazeState = hazeState)
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (settings.quitAlcohol) {
                HabitTrackerCard("Не пью алкоголь", settings.alcoholQuitDate, settings.alcoholRelapses, "🍃", PrimaryGreen, hazeState)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (settings.quitNicotine) {
                HabitTrackerCard("Не курю", settings.nicotineQuitDate, settings.nicotineRelapses, "🫁", AccentBlue, hazeState)
                Spacer(modifier = Modifier.height(16.dp))
            }

            AchievementsRow(achievements = achievements, onCommunityClick = onNavigateToCommunity, hazeState = hazeState)
        }
    }
}

// ==================== КОМПОНЕНТЫ ====================

@Composable
fun DashboardHeader(
    userName: String,
    onProfileClick: () -> Unit,
    hazeState: HazeState? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassCard(
            modifier = Modifier.weight(1f),
            hazeState = hazeState
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "Привет, ", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, fontFamily = Manrope)
                    Text(text = userName, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGreen, fontFamily = Manrope)
                    Text(text = "!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, fontFamily = Manrope)
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(8.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.1f))
                .clip(CircleShape)
                .background(Color.White)
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
    val remainingCal = goalCal - consumedCal
    val isOver = remainingCal < 0
    val absRemaining = kotlin.math.abs(remainingCal)

    GlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }, hazeState = hazeState) {
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
                Text("Питание", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Съедено: ${"%,.0f".format(consumedCal).replace(",", " ")} из ${"%,.0f".format(goalCal).replace(",", " ")}",
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
                            .background(if (isOver) Color(0xFFEF4444) else PrimaryGreen)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MacroCapsule("Белки", protein, goalProtein, AccentLightRed, Modifier.weight(1f))
                    MacroCapsule("Жиры", fat, goalFat, AccentOrange, Modifier.weight(1f))
                    MacroCapsule("Углеводы", carbs, goalCarbs, AccentBlue, Modifier.weight(1f))
                }
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
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        hazeState = hazeState
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            WaveBackground(modifier = Modifier.matchParentSize())

            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Водный баланс", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Выпито ${consumedMl} мл из ${goalMl}", fontSize = 13.sp, color = TextSecondary, fontFamily = Manrope)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = onAddWater,
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f)),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                "+ 250 мл",
                                fontSize = 14.sp,
                                color = AccentBlue,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalDrink,
                                contentDescription = "Выпить воды",
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                val progress = (consumedMl.toFloat() / goalMl).coerceIn(0f, 1f)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(100.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = AccentBlue.copy(alpha = 0.15f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = AccentBlue,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )

                        if (progress > 0f) {
                            val angle = 360f * progress - 90f
                            val radius = size.width / 2
                            val centerX = radius + radius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                            val centerY = radius + radius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()

                            drawCircle(
                                color = AccentBlue,
                                radius = 6.dp.toPx(),
                                center = Offset(centerX, centerY)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${consumedMl}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, fontFamily = Manrope)
                        Text("мл", fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
                    }
                }
            }
        }
    }
}

@Composable
fun WaveBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path1 = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.5f)
            quadraticBezierTo(w * 0.25f, h * 0.3f, w * 0.5f, h * 0.5f)
            quadraticBezierTo(w * 0.75f, h * 0.7f, w, h * 0.5f)
            lineTo(w, h)
            close()
        }
        drawPath(path1, AccentBlue.copy(alpha = 0.06f))

        val path2 = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.6f)
            quadraticBezierTo(w * 0.25f, h * 0.8f, w * 0.5f, h * 0.6f)
            quadraticBezierTo(w * 0.75f, h * 0.4f, w, h * 0.6f)
            lineTo(w, h)
            close()
        }
        drawPath(path2, AccentBlue.copy(alpha = 0.12f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityCarousel(
    healthMetrics: com.practicum.fitapp.data.HealthConnectManager.HealthMetrics?,
    onClick: () -> Unit,
    onRequestPermissions: () -> Unit,
    hazeState: HazeState? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (healthMetrics != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulsingDot()
                Spacer(modifier = Modifier.width(6.dp))
                Text("Браслет онлайн", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (healthMetrics == null) {
            GlassCard(modifier = Modifier.fillMaxWidth().clickable { onRequestPermissions() }, hazeState = hazeState) {
                Text(
                    text = "Нажмите, чтобы разрешить доступ к Health Connect",
                    color = AccentBlue,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(20.dp),
                    fontFamily = Manrope
                )
            }
        } else {
            val listState = rememberLazyListState()
            val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ActivityMetricCard(
                        title = "Шаги",
                        value = "${healthMetrics.steps}",
                        imageResId = com.practicum.fitapp.R.drawable.img_activity_steps,
                        onClick = onClick,
                        hazeState = hazeState
                    )
                }
                item {
                    ActivityMetricCard(
                        title = "Ккал",
                        value = "${healthMetrics.calories.toInt()}",
                        imageResId = com.practicum.fitapp.R.drawable.img_activity_calories,
                        onClick = onClick,
                        hazeState = hazeState
                    )
                }
                item {
                    ActivityMetricCard(
                        title = "Пульс",
                        value = "${healthMetrics.avgHeartRate ?: 0}",
                        imageResId = com.practicum.fitapp.R.drawable.img_activity_heart,
                        onClick = onClick,
                        hazeState = hazeState
                    )
                }
                item {
                    val sleepHours = healthMetrics.sleepMinutes / 60
                    val sleepMins = healthMetrics.sleepMinutes % 60
                    ActivityMetricCard(
                        title = "Сон",
                        value = "${sleepHours}ч ${sleepMins}м",
                        imageResId = com.practicum.fitapp.R.drawable.img_activity_sleep,
                        onClick = onClick,
                        hazeState = hazeState
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityMetricCard(
    title: String,
    value: String,
    imageResId: Int,
    onClick: () -> Unit,
    hazeState: HazeState? = null
) {
    GlassCard(
        modifier = Modifier
            .width(150.dp)
            .height(130.dp)
            .clickable { onClick() },
        hazeState = hazeState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
                    .alpha(0.8f),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontFamily = Manrope,
                    maxLines = 1
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium
                )
            }
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

        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Перейти к тренировке",
            tint = PrimaryGreen.copy(alpha = 0.15f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(96.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                Spacer(modifier = Modifier.height(4.dp))
                Text("40 мин · 6 упражнений", fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
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

                    // ФИКСИРУЕМ ВЫСОТУ И ШИРИНУ
                    GlassCard(
                        modifier = Modifier
                            .width(110.dp)
                            .height(120.dp)
                            .clickable { onCommunityClick() },
                        hazeState = hazeState
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center // Выравниваем контент по центру
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

                            // ТОЛЬКО НАЗВАНИЕ, БЕЗ ОПИСАНИЯ
                            Text(
                                text = achievement.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = TextPrimary,
                                fontFamily = Manrope,
                                maxLines = 2,
                                lineHeight = 14.sp
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
    return context.resources.getIdentifier("ic_ach_$id", "drawable", context.packageName)
}