package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.WorkoutStatus
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.training.TrainingViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun TrainingScreen(
    onWorkoutClick: (Long) -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val workouts by viewModel.workoutsForDay.collectAsStateWithLifecycle()
    val tags by viewModel.workoutTags.collectAsStateWithLifecycle()

    // Стейты для календаря
    var showCalendar by remember { mutableStateOf(false) }
    var calendarMonth by remember { mutableStateOf(java.time.YearMonth.from(selectedDate)) }
    val datesWithWorkouts by viewModel.workoutDates.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Заголовок (Октябрь 2026)
        item {
            Text(
                text = DateUtils.formatMonthYear(selectedDate),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    calendarMonth = java.time.YearMonth.from(selectedDate)
                    showCalendar = true
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Лента недели
        item {
            WeekStrip(
                selectedDate = selectedDate,
                datesWithWorkouts = datesWithWorkouts,
                onDayClick = { date -> viewModel.setSelectedDate(date) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Дата строкой (14.10.2026)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.formatShort(selectedDate),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (workouts.isEmpty()) {
            item {
                Text(
                    text = "Нет тренировок на этот день",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            items(workouts, key = { it.id }) { workout ->
                // Настройка свайпа для удаления
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteWorkout(workout.id)
                            true
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp) // Соответствует отступу карточки
                                .background(Color(0xFFE53935), shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = Color.White,
                                modifier = Modifier.padding(end = 20.dp)
                            )
                        }
                    },
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = true
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onWorkoutClick(workout.id) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = workout.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val statusEnum = WorkoutStatus.valueOf(workout.status)
                            Text(
                                text = statusEnum.displayName,
                                fontSize = 12.sp,
                                color = when (statusEnum) {
                                    WorkoutStatus.COMPLETED -> Color(0xFF2E7D32)
                                    WorkoutStatus.IN_PROGRESS -> Color(0xFF1976D2)
                                    WorkoutStatus.WAITING -> Color.Gray
                                }
                            )

                            // Теги групп мышц
                            val workoutTags = tags[workout.id] ?: emptyList()
                            if (workoutTags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    workoutTags.take(4).forEach { group ->
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color(0xFFE3F2FD)
                                        ) {
                                            Text(
                                                text = group.displayName,
                                                fontSize = 10.sp,
                                                color = Color(0xFF1976D2),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.addNewWorkout() }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+ Добавить тренировку",
                    fontSize = 16.sp,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
    if (showCalendar) {
        MonthCalendarDialog(
            currentMonth = calendarMonth,
            selectedDate = selectedDate,
            datesWithWorkouts = datesWithWorkouts,
            onMonthChange = { calendarMonth = it },
            onDateSelected = {
                viewModel.setSelectedDate(it)
                showCalendar = false
            },
            onDismiss = { showCalendar = false }
        )
    }
}

@Composable
private fun WeekStrip(
    selectedDate: LocalDate,
    datesWithWorkouts: Set<LocalDate>,
    onDayClick: (LocalDate) -> Unit  // ← Вот здесь явно указываем, что принимаем дату
) {
    val week = DateUtils.weekDays(selectedDate)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        week.forEach { date ->
            DayCell(
                date = date,
                isSelected = date == selectedDate,
                hasWorkout = date in datesWithWorkouts,
                onClick = { onDayClick(date) }, // ← Передаем дату в лямбду
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    hasWorkout: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWeekend = DateUtils.isWeekend(date)
    val dayNameColor = if (isWeekend) Color(0xFFD32F2F) else Color.Gray
    val dayNumberColor = if (isWeekend) Color(0xFFD32F2F) else Color.Black

    // Определяем цвет фона: выбранный — синий, с тренировкой — зеленый
    val backgroundColor = when {
        isSelected -> Color(0xFFE3F2FD)
        hasWorkout -> Color(0xFFA5D6A7)
        else -> Color.White
    }

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF1976D2)) else null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = DateUtils.dayOfWeekShort(date),
                fontSize = 11.sp,
                color = dayNameColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = dayNumberColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
private fun MonthCalendarDialog(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    datesWithWorkouts: Set<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Предыдущий месяц")
                }
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                    Icon(Icons.Default.KeyboardArrowRight, "Следующий месяц")
                }
            }
        },
        text = {
            Column {
                // Дни недели
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { name ->
                        Text(
                            text = name,
                            modifier = Modifier.weight(1f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (name == "Сб" || name == "Вс") Color.Red else Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Ячейки дней
                val firstDayOfMonth = currentMonth.atDay(1)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1=Пн, 7=Вс
                val daysInMonth = currentMonth.lengthOfMonth()

                // Сдвиг для пустых ячеек в начале
                val totalCells = firstDayOfWeek - 1 + daysInMonth
                val rows = (totalCells + 6) / 7  // округление вверх

                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - (firstDayOfWeek - 1) + 1

                            if (dayNumber in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayNumber)
                                val isSelected = date == selectedDate
                                val isWeekend = DateUtils.isWeekend(date)
                                val hasWorkout = date in datesWithWorkouts

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .background(
                                            color = when {
                                                isSelected -> Color(0xFF1976D2)
                                                hasWorkout -> Color(0xFFA5D6A7)
                                                else -> Color.Transparent
                                            },
                                            shape = RoundedCornerShape(50)
                                        )
                                        .clickable { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> Color.White
                                            isWeekend -> Color.Red
                                            else -> Color.Black
                                        }
                                    )
                                }
                            } else {
                                // пустая ячейка
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}
@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWeekend = DateUtils.isWeekend(date)
    val dayNameColor = if (isWeekend) Color(0xFFD32F2F) else Color.Gray
    val dayNumberColor = if (isWeekend) Color(0xFFD32F2F) else Color.Black

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF1976D2)) else null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = DateUtils.dayOfWeekShort(date),
                fontSize = 11.sp,
                color = dayNameColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = dayNumberColor
            )
        }
    }
}