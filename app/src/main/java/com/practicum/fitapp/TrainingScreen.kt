package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    onWorkoutClick: (Long) -> Unit,
    onCatalogClick: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val workouts by viewModel.workoutsForDay.collectAsStateWithLifecycle()
    val tags by viewModel.workoutTags.collectAsStateWithLifecycle()
    val datesWithWorkouts by viewModel.workoutDates.collectAsStateWithLifecycle()

    var showCalendar by remember { mutableStateOf(false) }
    var calendarMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = DateUtils.formatMonthYear(selectedDate),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    calendarMonth = YearMonth.from(selectedDate)
                    showCalendar = true
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            WeekStrip(
                selectedDate = selectedDate,
                datesWithWorkouts = datesWithWorkouts,
                onDayClick = { date -> viewModel.setSelectedDate(date) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
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
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteWorkout(workout.id)
                            true
                        } else false
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp)
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
                    .clickable { showAddSheet = true }
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

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Как создать тренировку?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                ListItem(
                    headlineContent = { Text("Каталог тренировок") },
                    supportingContent = { Text("Готовые программы на выбор") },
                    modifier = Modifier.clickable {
                        showAddSheet = false
                        onCatalogClick() // Нужно передать этот колбэк в TrainingScreen
                    }
                )
                ListItem(
                    headlineContent = { Text("Чистый шаблон") },
                    supportingContent = { Text("Создать пустую тренировку") },
                    modifier = Modifier.clickable {
                        viewModel.addNewWorkout()
                        showAddSheet = false
                    }
                )
                ListItem(
                    headlineContent = { Text("История тренировок") },
                    supportingContent = { Text("Скопировать из прошлой") },
                    modifier = Modifier.clickable {
                        showAddSheet = false
                        showHistoryDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showHistoryDialog) {
        val allWorkouts by viewModel.allWorkouts.collectAsStateWithLifecycle()
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = { Text("Выберите тренировку") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(allWorkouts) { workout ->
                        Text(
                            text = "${workout.name} — ${DateUtils.formatShort(DateUtils.timestampToLocalDate(workout.date))}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.copyWorkoutToDate(workout.id, selectedDate)
                                    showHistoryDialog = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) { Text("Закрыть") }
            }
        )
    }
}

@Composable
private fun WeekStrip(
    selectedDate: LocalDate,
    datesWithWorkouts: Set<LocalDate>,
    onDayClick: (LocalDate) -> Unit
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
                onClick = { onDayClick(date) },
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
                val firstDayOfMonth = currentMonth.atDay(1)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
                val daysInMonth = currentMonth.lengthOfMonth()
                val totalCells = firstDayOfWeek - 1 + daysInMonth
                val rows = (totalCells + 6) / 7
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
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}