package com.practicum.fitapp

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ExerciseCategory
import com.practicum.fitapp.data.local.ExerciseProgress
import com.practicum.fitapp.data.local.WeightEntryEntity
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.journal.ExerciseStatsViewModel
import com.practicum.fitapp.presentation.journal.JournalViewModel
import com.practicum.fitapp.presentation.journal.NutritionDay
import com.practicum.fitapp.presentation.journal.WorkoutDay

@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel(),
    statsViewModel: ExerciseStatsViewModel = hiltViewModel()
) {
    val nutritionDays by viewModel.nutritionDays.collectAsStateWithLifecycle()
    val workoutDays by viewModel.workoutDays.collectAsStateWithLifecycle()
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    val exercisesWithProgress by statsViewModel.exercisesWithProgress.collectAsStateWithLifecycle()
    val progressData by statsViewModel.progressData.collectAsStateWithLifecycle()
    val selectedExerciseId by statsViewModel.selectedExerciseId.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(text = "Журнал", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Навигация по месяцам
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Предыдущий")
            }
            Text(
                text = DateUtils.formatMonthYear(selectedMonth.atDay(1)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Следующий")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Тепловая карта питания
        Text(text = "Питание", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        NutritionHeatmap(nutritionDays)
        Spacer(modifier = Modifier.height(24.dp))

        // Тепловая карта тренировок
        Text(text = "Тренировки", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        WorkoutHeatmap(workoutDays)
        Spacer(modifier = Modifier.height(24.dp))

        // График веса
        Text(text = "Вес", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        if (weightHistory.isEmpty()) {
            Text("Нет данных для графика", color = Color.Gray)
        } else {
            WeightChart(weightHistory)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Статистика упражнений
        Text(text = "Статистика упражнений", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (exercisesWithProgress.isEmpty()) {
            Text("Нет данных о подходах", color = Color.Gray)
        } else {
            exercisesWithProgress.forEach { exercise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { statsViewModel.selectExercise(exercise.id) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Подходов: ${exercise.totalSets}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }

    // Диалог с графиком упражнения (вынесен из LazyColumn/Column)
    if (selectedExerciseId != null) {
        val selected = exercisesWithProgress.find { it.id == selectedExerciseId }
        if (selected != null) {
            AlertDialog(
                onDismissRequest = { statsViewModel.selectExercise(null) },
                title = { Text(selected.name) },
                text = {
                    if (progressData.isEmpty()) {
                        Text("Нет данных")
                    } else {
                        ExerciseProgressChart(data = progressData, category = selected.category)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { statsViewModel.selectExercise(null) }) {
                        Text("Закрыть")
                    }
                }
            )
        }
    }
}

@Composable
private fun NutritionHeatmap(days: List<NutritionDay>) {
    if (days.isEmpty()) {
        Text("Заполните профиль, чтобы видеть статистику", color = Color.Gray)
        return
    }

    val firstDay = days.first().date
    val firstDayOfWeek = firstDay.dayOfWeek.value
    val offset = firstDayOfWeek - 1

    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { name ->
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (name == "Сб" || name == "Вс") Color.Red else Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    HeatmapCell(
                        label = day.date.dayOfMonth.toString(),
                        color = nutritionColor(day.ratio, day.calories),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Мало", fontSize = 10.sp, color = Color.Gray)
        LegendDot(Color(0xFFC8E6C9))
        LegendDot(Color(0xFF4CAF50))
        Text("Норма", fontSize = 10.sp, color = Color.Gray)
        LegendDot(Color(0xFFFFCDD2))
        LegendDot(Color(0xFFF44336))
        LegendDot(Color(0xFFB71C1C))
        Text("Перебор", fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
private fun WorkoutHeatmap(days: List<WorkoutDay>) {
    if (days.isEmpty()) return
    val firstDay = days.first().date
    val offset = firstDay.dayOfWeek.value - 1

    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { name ->
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (name == "Сб" || name == "Вс") Color.Red else Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    HeatmapCell(
                        label = days[dayIndex].date.dayOfMonth.toString(),
                        color = if (days[dayIndex].hasWorkout) Color(0xFF4CAF50) else Color(0xFFF5F5F5),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
private fun HeatmapCell(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.aspectRatio(1f).padding(2.dp).clip(RoundedCornerShape(4.dp)).background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (color == Color.White || color == Color(0xFFF5F5F5)) Color.Gray else Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(color))
}

private fun nutritionColor(ratio: Double, calories: Double): Color {
    if (calories <= 0.0) return Color.White
    return when {
        ratio < 0.75 -> Color(0xFFC8E6C9)
        ratio < 1.0 -> Color(0xFF4CAF50)
        ratio < 1.10 -> Color(0xFFFFCDD2)
        ratio < 1.30 -> Color(0xFFF44336)
        else -> Color(0xFFB71C1C)
    }
}

@Composable
private fun WeightChart(history: List<WeightEntryEntity>) {
    val sorted = history.sortedBy { it.date }
    if (sorted.size < 2) {
        Text("Нужно минимум 2 взвешивания", color = Color.Gray, fontSize = 12.sp)
        return
    }

    val minWeight = sorted.minOf { it.weightKg }
    val maxWeight = sorted.maxOf { it.weightKg }
    val weightRange = (maxWeight - minWeight).takeIf { it > 0 } ?: 1.0

    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp)) {
            val width = size.width
            val height = size.height
            val paddingLeft = 60.dp.toPx()
            val paddingBottom = 24.dp.toPx()
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom

            val labelPaint = Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 28f
                isAntiAlias = true
            }
            val steps = 4
            for (i in 0..steps) {
                val weight = minWeight + (weightRange * i / steps)
                val y = chartHeight - (chartHeight * i / steps)
                drawLine(color = Color.LightGray, start = Offset(paddingLeft, y), end = Offset(width, y), strokeWidth = 1f)
                drawContext.canvas.nativeCanvas.drawText("%.0f".format(weight), 0f, y + 10f, labelPaint)
            }

            val path = Path()
            val points = mutableListOf<Offset>()
            sorted.forEachIndexed { index, entry ->
                val x = paddingLeft + (chartWidth * index / (sorted.size - 1))
                val normalizedWeight = ((entry.weightKg - minWeight) / weightRange).toFloat()
                val y = chartHeight - (chartHeight * normalizedWeight)
                val point = Offset(x, y)
                points.add(point)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(path = path, color = Color(0xFF1976D2), style = Stroke(width = 4f))
            points.forEach { drawCircle(color = Color(0xFF1976D2), radius = 6f, center = it) }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(sorted.first().date)), fontSize = 10.sp, color = Color.Gray)
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(sorted.last().date)), fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ExerciseProgressChart(data: List<ExerciseProgress>, category: String) {
    if (data.size < 2) {
        Text("Нужно минимум 2 дня с тренировками", color = Color.Gray, fontSize = 12.sp)
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

    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp).padding(8.dp)) {
            val width = size.width
            val height = size.height
            val paddingLeft = 60.dp.toPx()
            val paddingBottom = 24.dp.toPx()
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom

            val gridPaint = Paint().apply {
                color = android.graphics.Color.LTGRAY
                strokeWidth = 1f
                textSize = 24f
                isAntiAlias = true
            }
            val steps = 4
            for (i in 0..steps) {
                val v = minValue + (range * i / steps)
                val y = chartHeight - (chartHeight * i / steps)
                drawLine(color = Color.LightGray, start = Offset(paddingLeft, y), end = Offset(width, y), strokeWidth = 1f)
                drawContext.canvas.nativeCanvas.drawText("%.0f".format(v), 0f, y + 8f, gridPaint)
            }

            val path = Path()
            val points = mutableListOf<Offset>()
            data.forEachIndexed { index, item ->
                val x = paddingLeft + (chartWidth * index / (data.size - 1))
                val normalized = ((values[index] - minValue) / range).toFloat()
                val y = chartHeight - (chartHeight * normalized)
                val p = Offset(x, y)
                points.add(p)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(path = path, color = Color(0xFF1976D2), style = Stroke(width = 4f))
            points.forEach { drawCircle(color = Color(0xFF1976D2), radius = 6f, center = it) }
        }

        Text("Ед.: $unit", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(data.first().dayTimestamp)), fontSize = 10.sp, color = Color.Gray)
            Text(DateUtils.formatShort(DateUtils.timestampToLocalDate(data.last().dayTimestamp)), fontSize = 10.sp, color = Color.Gray)
        }
    }
}