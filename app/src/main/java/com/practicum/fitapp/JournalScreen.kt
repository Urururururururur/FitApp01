package com.practicum.fitapp

import android.graphics.Paint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ExerciseCategory
import com.practicum.fitapp.data.local.ExerciseProgress
import com.practicum.fitapp.data.local.WeightEntryEntity
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.journal.ExerciseStatsViewModel
import com.practicum.fitapp.presentation.journal.JournalViewModel
import com.practicum.fitapp.presentation.journal.NutritionDay
import com.practicum.fitapp.presentation.journal.WorkoutDay
import com.practicum.fitapp.ui.theme.*

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel(),
    statsViewModel: ExerciseStatsViewModel = hiltViewModel()
) {
    val nutritionDays by viewModel.nutritionDays.collectAsStateWithLifecycle()
    val workoutDays by viewModel.workoutDays.collectAsStateWithLifecycle()
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val metrics by viewModel.healthMetrics.collectAsStateWithLifecycle()

    val exercisesWithProgress by statsViewModel.exercisesWithProgress.collectAsStateWithLifecycle()
    val progressData by statsViewModel.progressData.collectAsStateWithLifecycle()
    val selectedExerciseId by statsViewModel.selectedExerciseId.collectAsStateWithLifecycle()

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
        if (metrics == null) {
            permissionsLauncher.launch(viewModel.hcPermissions)
        } else {
            viewModel.loadHealthData()
        }
    }

    // Стейт для размытия
    val hazeState = remember { HazeState() }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(16.dp)
                .padding(bottom = 100.dp) // Отступ под нижнее меню
        ) {
            // ДАШБОРД ЗДОРОВЬЯ
            if (metrics != null) {
                HealthMetricsCard(metrics = metrics!!, hazeState = hazeState)
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { permissionsLauncher.launch(viewModel.hcPermissions) },
                    hazeState = hazeState,
                    hasBorder = true
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет доступа к данным здоровья. Нажмите для запроса.",
                            color = PrimaryGreen,
                            fontFamily = Manrope
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Навигация по месяцам
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Предыдущий", tint = TextPrimary)
                }
                Text(
                    text = DateUtils.formatMonthYear(selectedMonth.atDay(1)),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope
                )
                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Следующий", tint = TextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Питание
            Text(text = "Питание", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(8.dp))
            NutritionHeatmap(nutritionDays)
            Spacer(modifier = Modifier.height(24.dp))

            // Тренировки
            Text(text = "Тренировки", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(8.dp))
            WorkoutHeatmap(workoutDays)
            Spacer(modifier = Modifier.height(24.dp))

            // Вес
            Text(text = "Вес", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(8.dp))
            if (weightHistory.isEmpty()) {
                Text("Нет данных для графика", color = TextSecondary, fontFamily = Manrope)
            } else {
                WeightChart(weightHistory)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Статистика упражнений
            Text(text = "Статистика упражнений", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(8.dp))

            if (exercisesWithProgress.isEmpty()) {
                Text("Нет данных о подходах", color = TextSecondary, fontFamily = Manrope)
            } else {
                exercisesWithProgress.forEach { exercise ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { statsViewModel.selectExercise(exercise.id) },
                        shape = RoundedCornerShape(16.dp),
                        hazeState = hazeState,
                        hasBorder = true
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Text(text = "Подходов: ${exercise.totalSets}", fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
                        }
                    }
                }
            }
        }
    }

    // Диалог с графиком упражнения
    if (selectedExerciseId != null) {
        val selected = exercisesWithProgress.find { it.id == selectedExerciseId }
        if (selected != null) {
            Dialog(onDismissRequest = { statsViewModel.selectExercise(null) }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(24.dp),
                    hazeState = hazeState,
                    hasBorder = true
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(selected.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (progressData.isEmpty()) {
                            Text("Нет данных", color = TextSecondary, fontFamily = Manrope)
                        } else {
                            ExerciseProgressChart(data = progressData, category = selected.category)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(onClick = { statsViewModel.selectExercise(null) }) {
                                Text("Закрыть", color = PrimaryGreen, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============ КОМПОНЕНТЫ ============

@Composable
private fun HealthMetricsCard(metrics: com.practicum.fitapp.data.HealthConnectManager.HealthMetrics, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        hazeState = hazeState,
        hasBorder = true
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Данные с браслета",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = Manrope
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Шаги
            HealthMetricRow(
                icon = Icons.Outlined.DirectionsWalk,
                iconTint = AccentOrange,
                title = "Шаги",
                value = "${metrics.steps} шага"
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Калории
            HealthMetricRow(
                icon = Icons.Outlined.LocalFireDepartment,
                iconTint = AccentOrange,
                title = "Активные калории",
                value = "${metrics.calories.toInt()} ккал"
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Пульс
            HealthMetricRow(
                icon = Icons.Outlined.FavoriteBorder,
                iconTint = AccentBlue,
                title = "Пульс",
                value = if (metrics.avgHeartRate != null) "${metrics.avgHeartRate} уд/мин" else "Нет данных"
            )

            // Сон
            if (metrics.sleepMinutes > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                HealthMetricRow(
                    icon = Icons.Outlined.Bedtime,
                    iconTint = Color(0xFF9C27B0), // Оставил фиолетовый для сна, чтобы отличать
                    title = "Сон",
                    value = "${metrics.sleepMinutes / 60}ч ${metrics.sleepMinutes % 60}м"
                )
            }
        }
    }
}

@Composable
private fun HealthMetricRow(icon: ImageVector, iconTint: Color, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
            Text(text = value, fontSize = 13.sp, color = TextSecondary, fontFamily = Manrope)
        }
    }
}

@Composable
private fun NutritionHeatmap(days: List<NutritionDay>) {
    if (days.isEmpty()) {
        Text("Заполните профиль, чтобы видеть статистику", color = TextSecondary, fontFamily = Manrope)
        return
    }

    val firstDay = days.first().date
    val offset = firstDay.dayOfWeek.value - 1

    // Дни недели
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { name ->
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (name == "Сб" || name == "Вс") Color(0xFFE57373) else TextSecondary,
                textAlign = TextAlign.Center,
                fontFamily = Manrope
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))

    val totalCells = offset + days.size
    val rows = (totalCells + 6) / 7

    for (row in 0 until rows) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (col in 0 until 7) {
                val cellIndex = row * 7 + col
                val dayIndex = cellIndex - offset

                if (dayIndex in days.indices) {
                    val day = days[dayIndex]
                    val bgColor = nutritionColor(day.ratio, day.calories)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (day.calories <= 0.0) Color.White.copy(alpha = 0.5f) else bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (day.calories <= 0.0) TextSecondary else if (day.ratio < 1.10) TextPrimary else Color.White,
                            fontFamily = Manrope
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        LegendDot(PrimaryGreen.copy(alpha = 0.2f))
        Text("Мало", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
        LegendDot(PrimaryGreen.copy(alpha = 0.5f))
        Text("Норма", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
        LegendDot(Color(0xFFFFCDD2))
        Text("Перебор", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
    }
}

@Composable
private fun WorkoutHeatmap(days: List<WorkoutDay>) {
    if (days.isEmpty()) return
    val offset = days.first().date.dayOfWeek.value - 1

    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { name ->
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (name == "Сб" || name == "Вс") Color(0xFFE57373) else TextSecondary,
                textAlign = TextAlign.Center,
                fontFamily = Manrope
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))

    val totalCells = offset + days.size
    val rows = (totalCells + 6) / 7

    for (row in 0 until rows) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (col in 0 until 7) {
                val dayIndex = row * 7 + col - offset
                if (dayIndex in days.indices) {
                    val day = days[dayIndex]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!day.hasWorkout) Color.White.copy(alpha = 0.5f) else PrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (!day.hasWorkout) TextSecondary else Color.White,
                            fontFamily = Manrope
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
}

private fun nutritionColor(ratio: Double, calories: Double): Color {
    if (calories <= 0.0) return Color.Transparent
    return when {
        ratio < 0.75 -> PrimaryGreen.copy(alpha = 0.2f)
        ratio < 1.10 -> PrimaryGreen.copy(alpha = 0.5f)
        else -> Color(0xFFFFCDD2)
    }
}

// ============ ГРАФИКИ ============

@Composable
private fun WeightChart(history: List<WeightEntryEntity>) {
    val sorted = history.sortedBy { it.date }
    if (sorted.size < 2) {
        Text("Нужно минимум 2 взвешивания", color = TextSecondary, fontSize = 12.sp, fontFamily = Manrope)
        return
    }

    val minWeight = sorted.minOf { it.weightKg }
    val maxWeight = sorted.maxOf { it.weightKg }
    val weightRange = (maxWeight - minWeight).takeIf { it > 0 } ?: 1.0

    val gridColor = Color.Black.copy(alpha = 0.05f)
    val textColor = TextSecondary.toArgb()

    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp)) {
            val width = size.width
            val height = size.height
            val paddingLeft = 60.dp.toPx()
            val paddingBottom = 24.dp.toPx()
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom

            // Сетка и подписи по Y
            val labelPaint = Paint().apply {
                color = textColor
                textSize = 24f
                isAntiAlias = true
            }
            val steps = 4
            for (i in 0..steps) {
                val weight = minWeight + (weightRange * i / steps)
                val y = chartHeight - (chartHeight * i / steps)
                drawLine(color = gridColor, start = Offset(paddingLeft, y), end = Offset(width, y), strokeWidth = 2f)
                drawContext.canvas.nativeCanvas.drawText("%.0f".format(weight), 0f, y + 8f, labelPaint)
            }

            // Точки
            val points = mutableListOf<Offset>()
            sorted.forEachIndexed { index, entry ->
                val x = paddingLeft + (chartWidth * index / (sorted.size - 1))
                val normalizedWeight = ((entry.weightKg - minWeight) / weightRange).toFloat()
                val y = chartHeight - (chartHeight * normalizedWeight)
                points.add(Offset(x, y))
            }

            // Плавная линия (Cubic Bezier)
            val path = Path()
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val p0 = if (i == 1) points[0] else points[i - 2]
                val p1 = points[i - 1]
                val p2 = points[i]
                val p3 = if (i == points.size - 1) p2 else points[i + 1]

                // Control points for smoothing
                val cp1x = p1.x + (p2.x - p0.x) / 6f
                val cp1y = p1.y + (p2.y - p0.y) / 6f
                val cp2x = p2.x - (p3.x - p1.x) / 6f
                val cp2y = p2.y - (p3.y - p1.y) / 6f

                path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
            }

            // Градиентная заливка под линией
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, chartHeight)
                lineTo(points.first().x, chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryGreen.copy(alpha = 0.3f), Color.Transparent),
                    startY = points.minOf { it.y },
                    endY = chartHeight
                )
            )

            // Сама линия
            drawPath(path = path, color = PrimaryGreen, style = Stroke(width = 4f))

            // Точки (маленькие)
            points.forEach { drawCircle(color = PrimaryGreen, radius = 3f, center = it) }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(sorted.first().date)), fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(sorted.last().date)), fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
        }
    }
}

@Composable
private fun ExerciseProgressChart(data: List<ExerciseProgress>, category: String) {
    if (data.size < 2) {
        Text("Нужно минимум 2 дня с тренировками", color = TextSecondary, fontSize = 12.sp, fontFamily = Manrope)
        return
    }

    val categoryEnum = ExerciseCategory.valueOf(category)
    val values: List<Double> = when (categoryEnum) {
        ExerciseCategory.STRENGTH -> data.map { it.maxWeight }
        ExerciseCategory.BODYWEIGHT -> data.map { it.totalReps.toDouble() }
        ExerciseCategory.CARDIO -> data.map { it.totalDuration.toDouble() }
        ExerciseCategory.HOLD -> data.map { it.totalDuration.toDouble() }
    }
    val unit = when (categoryEnum) {
        ExerciseCategory.STRENGTH -> "кг"
        ExerciseCategory.BODYWEIGHT -> "повт"
        ExerciseCategory.CARDIO, ExerciseCategory.HOLD -> "сек"
    }

    val minValue = values.minOrNull() ?: 0.0
    val maxValue = values.maxOrNull() ?: 1.0
    val range = (maxValue - minValue).takeIf { it > 0 } ?: 1.0

    val gridColor = Color.Black.copy(alpha = 0.05f)
    val textColor = TextSecondary.toArgb()

    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp).padding(8.dp)) {
            val width = size.width
            val height = size.height
            val paddingLeft = 60.dp.toPx()
            val paddingBottom = 24.dp.toPx()
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom

            val gridPaint = Paint().apply {
                color = textColor
                strokeWidth = 2f
                textSize = 24f
                isAntiAlias = true
            }
            val steps = 4
            for (i in 0..steps) {
                val v = minValue + (range * i / steps)
                val y = chartHeight - (chartHeight * i / steps)
                drawLine(color = gridColor, start = Offset(paddingLeft, y), end = Offset(width, y), strokeWidth = 2f)
                drawContext.canvas.nativeCanvas.drawText("%.0f".format(v), 0f, y + 8f, gridPaint)
            }

            val points = mutableListOf<Offset>()
            data.forEachIndexed { index, item ->
                val x = paddingLeft + (chartWidth * index / (data.size - 1))
                val normalized = ((values[index] - minValue) / range).toFloat()
                val y = chartHeight - (chartHeight * normalized)
                points.add(Offset(x, y))
            }

            // Плавная линия
            val path = Path()
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val p0 = if (i == 1) points[0] else points[i - 2]
                val p1 = points[i - 1]
                val p2 = points[i]
                val p3 = if (i == points.size - 1) p2 else points[i + 1]

                val cp1x = p1.x + (p2.x - p0.x) / 6f
                val cp1y = p1.y + (p2.y - p0.y) / 6f
                val cp2x = p2.x - (p3.x - p1.x) / 6f
                val cp2y = p2.y - (p3.y - p1.y) / 6f

                path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
            }

            // Градиент
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, chartHeight)
                lineTo(points.first().x, chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryGreen.copy(alpha = 0.3f), Color.Transparent),
                    startY = points.minOf { it.y },
                    endY = chartHeight
                )
            )

            // Линия
            drawPath(path = path, color = PrimaryGreen, style = Stroke(width = 4f))

            // Точки
            points.forEach { drawCircle(color = PrimaryGreen, radius = 3f, center = it) }
        }

        Text("Ед.: $unit", fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope, modifier = Modifier.padding(start = 8.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(data.first().dayTimestamp)), fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(data.last().dayTimestamp)), fontSize = 10.sp, color = TextSecondary, fontFamily = Manrope)
        }
    }
}