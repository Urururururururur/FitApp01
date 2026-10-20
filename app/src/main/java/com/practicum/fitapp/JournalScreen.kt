package com.practicum.fitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.journal.JournalViewModel
import com.practicum.fitapp.presentation.journal.NutritionDay
import com.practicum.fitapp.presentation.journal.WorkoutDay

@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val nutritionDays by viewModel.nutritionDays.collectAsStateWithLifecycle()
    val workoutDays by viewModel.workoutDays.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Журнал", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // ============ Тепловая карта питания ============
        Text(text = "Питание", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        NutritionHeatmap(nutritionDays)
        Spacer(modifier = Modifier.height(24.dp))

        // ============ Тепловая карта тренировок ============
        Text(text = "Тренировки", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        WorkoutHeatmap(workoutDays)
    }
}

@Composable
private fun NutritionHeatmap(days: List<NutritionDay>) {
    if (days.isEmpty()) {
        Text("Заполните профиль, чтобы видеть статистику", color = Color.Gray)
        return
    }

    // Сетка 7 колонок (дни недели) x N строк (недели месяца)
    // Первый день месяца может быть не понедельником — нужен сдвиг
    val firstDay = days.first().date
    val firstDayOfWeek = firstDay.dayOfWeek.value  // 1=Пн, 7=Вс
    val offset = firstDayOfWeek - 1  // сколько пустых ячеек в начале

    // Дни недели
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

    // Ячейки
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
    // Легенда
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
                        color = if (day.hasWorkout) Color(0xFF4CAF50) else Color(0xFFF5F5F5),
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
private fun HeatmapCell(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color),
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
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}

/**
 * Цвет квадратика по проценту от нормы.
 *  < 75%     → бледно-зелёный
 *  75–100%   → ярко-зелёный
 *  100–110%  → розовый
 *  110–130%  → красный
 *  > 130%    → тёмно-красный
 *  0 калорий → белый (данных нет)
 */
private fun nutritionColor(ratio: Double, calories: Double): Color {
    if (calories <= 0.0) return Color.White
    return when {
        ratio < 0.75 -> Color(0xFFC8E6C9)   // бледно-зелёный
        ratio < 1.0 -> Color(0xFF4CAF50)    // ярко-зелёный
        ratio < 1.10 -> Color(0xFFFFCDD2)   // светло-розовый
        ratio < 1.30 -> Color(0xFFF44336)   // красный
        else -> Color(0xFFB71C1C)           // тёмно-красный
    }
}