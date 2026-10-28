package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.WorkoutStatus
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.training.TrainingViewModel
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.draw.clip


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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        // Просто пустое пространство. Никакого красного фона и корзины.
                        Box(modifier = Modifier.fillMaxSize())
                    },
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = true
                ) {
                    // ЗАМЕНИЛИ Card НА GlassCard
                    com.practicum.fitapp.presentation.common.GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onWorkoutClick(workout.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = workout.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface // Добавили цвет для читаемости на стекле
                            )
                            val statusEnum = WorkoutStatus.valueOf(workout.status)
                            Text(
                                text = statusEnum.displayName,
                                fontSize = 12.sp,
                                color = when (statusEnum) {
                                    WorkoutStatus.COMPLETED -> Color(0xFF4CAF50)
                                    WorkoutStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                                    WorkoutStatus.WAITING -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            val workoutTags = tags[workout.id] ?: emptyList()
                            if (workoutTags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    workoutTags.take(4).forEach { group ->
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = group.displayName,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
            com.practicum.fitapp.presentation.common.GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { showAddSheet = true },
                shape = RoundedCornerShape(16.dp),
                solidColor = Color(0xFF81C784), // ← Сплошной зеленый цвет
                hasShadow = true,               // Добавляем тень, чтобы кнопка выделялась
                hasBorder = false
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Добавить тренировку",
                        fontSize = 16.sp,
                        color = Color.White, // ← Белый текст для контраста
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
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
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                // ГЛАВНОЕ ОКНО (плотное, светло-зеленое)
                com.practicum.fitapp.presentation.common.GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    solidColor = Color(0xFFC8E6C9), // Плотный фон
                    hasShadow = false,
                    hasBorder = false
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Как создать тренировку?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Кнопка 1 (стеклянная, полупрозрачная)
                        com.practicum.fitapp.presentation.common.GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { showAddSheet = false; onCatalogClick() },
                            shape = RoundedCornerShape(16.dp),
                            glassColor = Color.White.copy(alpha = 0.6f), // ← Вернули прозрачность
                            hasShadow = false,
                            hasBorder = false
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Каталог тренировок", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                Text("Готовые программы на выбор", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Кнопка 2 (стеклянная, полупрозрачная)
                        com.practicum.fitapp.presentation.common.GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.addNewWorkout(); showAddSheet = false },
                            shape = RoundedCornerShape(16.dp),
                            glassColor = Color.White.copy(alpha = 0.6f), // ← Вернули прозрачность
                            hasShadow = false,
                            hasBorder = false
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Чистый шаблон", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                Text("Создать пустую тренировку", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Кнопка 3 (стеклянная, полупрозрачная)
                        com.practicum.fitapp.presentation.common.GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { showAddSheet = false; showHistoryDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            glassColor = Color.White.copy(alpha = 0.6f), // ← Вернули прозрачность
                            hasShadow = false,
                            hasBorder = false
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("История тренировок", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                Text("Скопировать из прошлой", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
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
    val dayNameColor = if (isWeekend) Color(0xFFE57373) else MaterialTheme.colorScheme.onSurfaceVariant
    val dayNumberColor = if (isWeekend) Color(0xFFE57373) else MaterialTheme.colorScheme.onSurface

    // Если день выбран — зеленый, если есть тренировка — полупрозрачный зеленый, иначе белый
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        hasWorkout -> Color(0xFF4CAF50).copy(alpha = 0.3f)
        else -> Color.White.copy(alpha = 0.8f)
    }

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 1.dp else 0.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
    // Используем базовый Dialog вместо AlertDialog, чтобы убрать стандартный белый/черный фон
    Dialog(onDismissRequest = onDismiss) {
        // Наш компонент "жидкого стекла" / белое окно
        com.practicum.fitapp.presentation.common.GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            // Используем solidColor, чтобы сделать фон плотным белым
            solidColor = Color.White,
            hasShadow = true
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Заголовок с переключением месяцев
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Предыдущий месяц", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Text(
                        // Используем наш DateUtils, чтобы месяц был на русском ("Октябрь 2026")
                        text = DateUtils.formatMonthYear(currentMonth.atDay(1)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                        Icon(Icons.Default.KeyboardArrowRight, "Следующий месяц", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Дни недели
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { name ->
                        Text(
                            text = name,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (name == "Сб" || name == "Вс") Color(0xFFE57373) else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Сетка дней
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
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                hasWorkout -> Color(0xFF4CAF50).copy(alpha = 0.3f)
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
                                            isSelected -> MaterialTheme.colorScheme.onPrimary
                                            isWeekend -> Color(0xFFE57373)
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка закрытия в стиле темы
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Закрыть", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}