package com.practicum.fitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.WorkoutStatus
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.TrainingViewModel
import com.practicum.fitapp.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text(
                    text = DateUtils.formatMonthYear(selectedDate),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope,
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
                    onDayClick = { date -> viewModel.setSelectedDate(date) },
                    hazeState = hazeState
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
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontFamily = Manrope
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (workouts.isEmpty()) {
                item {
                    Text(
                        text = "Нет тренировок на этот день",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = Manrope
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
                        modifier = Modifier.padding(vertical = 4.dp), // Отступ для свайпа
                        backgroundContent = {
                            // Красная подложка с корзиной
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFEF4444).copy(alpha = 0.3f))
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Удалить",
                                    tint = Color.White
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true
                    ) {
                        val programImage = com.practicum.fitapp.presentation.training.builtInPrograms.find { program ->
                            workout.name.contains(program.title, ignoreCase = true) ||
                                    program.title.contains(workout.name, ignoreCase = true)
                        }?.imageResId

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onWorkoutClick(workout.id) },
                            shape = RoundedCornerShape(16.dp),
                            hasShadow = true,
                            hasBorder = true,
                            hazeState = hazeState
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // ЛЕВАЯ ЧАСТЬ: Текст и теги
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = workout.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontFamily = Manrope
                                    )
                                    val statusEnum = WorkoutStatus.valueOf(workout.status)
                                    Text(
                                        text = statusEnum.displayName,
                                        fontSize = 12.sp,
                                        color = when (statusEnum) {
                                            WorkoutStatus.COMPLETED -> PrimaryGreen
                                            WorkoutStatus.IN_PROGRESS -> AccentOrange
                                            WorkoutStatus.WAITING -> TextSecondary
                                        },
                                        fontFamily = Manrope
                                    )
                                    val workoutTags = tags[workout.id] ?: emptyList()
                                    if (workoutTags.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            workoutTags.take(3).forEach { group ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(50))
                                                        .background(PrimaryGreen.copy(alpha = 0.15f))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = group.displayName,
                                                        fontSize = 10.sp,
                                                        color = PrimaryGreen,
                                                        fontFamily = Manrope
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // ПРАВАЯ ЧАСТЬ: Картинка
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.5f)), // Стеклянная заглушка
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
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = PrimaryGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            item {
                Button(
                    onClick = { showAddSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGreen.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Добавить тренировку",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                }
            }
        }
    }

    // ДИАЛОГ КАЛЕНДАРЯ
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
            onDismiss = { showCalendar = false },
            hazeState = hazeState
        )
    }

    // НИЖНЕЕ МЕНЮ СОЗДАНИЯ
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    hazeState = hazeState,
                    hasBorder = true
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Как создать тренировку?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val options = listOf(
                            Triple("Каталог тренировок", "Готовые программы на выбор") { showAddSheet = false; onCatalogClick() },
                            Triple("Чистый шаблон", "Создать пустую тренировку") { viewModel.addNewWorkout(); showAddSheet = false },
                            Triple("История тренировок", "Скопировать из прошлой") { showAddSheet = false; showHistoryDialog = true }
                        )

                        options.forEach { (title, subtitle, action) ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { action() },
                                shape = RoundedCornerShape(16.dp),
                                hazeState = hazeState,
                                hasBorder = true
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                                    Text(subtitle, fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ДИАЛОГ ИСТОРИИ
    if (showHistoryDialog) {
        val allWorkouts by viewModel.allWorkouts.collectAsStateWithLifecycle()
        Dialog(onDismissRequest = { showHistoryDialog = false }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                hazeState = hazeState,
                hasBorder = true
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Выберите тренировку", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(allWorkouts) { workout ->
                            Text(
                                text = "${workout.name} — ${DateUtils.formatShort(DateUtils.timestampToLocalDate(workout.date))}",
                                fontSize = 14.sp,
                                fontFamily = Manrope,
                                color = TextPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.copyWorkoutToDate(workout.id, selectedDate)
                                        showHistoryDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { showHistoryDialog = false }) {
                            Text("Закрыть", color = PrimaryGreen, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(
    selectedDate: LocalDate,
    datesWithWorkouts: Set<LocalDate>,
    onDayClick: (LocalDate) -> Unit,
    hazeState: HazeState
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
                modifier = Modifier.weight(1f),
                hazeState = hazeState
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
    modifier: Modifier = Modifier,
    hazeState: HazeState
) {
    val isWeekend = DateUtils.isWeekend(date)

    val dayNameColor = if (isSelected) Color.White.copy(alpha = 0.8f) else if (isWeekend) Color(0xFFEF4444) else TextSecondary
    val dayNumberColor = if (isSelected) Color.White else if (isWeekend) Color(0xFFEF4444) else TextPrimary

    // Если выбран или есть тренировка - используем сплошной цвет/подсветку. Иначе - стекло.
    val cardModifier = modifier.clickable(onClick = onClick)

    if (isSelected || hasWorkout) {
        val backgroundColor = when {
            isSelected -> PrimaryGreen
            hasWorkout -> PrimaryGreen.copy(alpha = 0.15f)
            else -> Color.Transparent
        }
        Box(
            modifier = cardModifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            DayContent(date, dayNameColor, dayNumberColor, isSelected)
        }
    } else {
        GlassCard(
            modifier = cardModifier,
            shape = RoundedCornerShape(12.dp),
            hazeState = hazeState,
            hasBorder = true
        ) {
            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                DayContent(date, dayNameColor, dayNumberColor, isSelected)
            }
        }
    }
}

@Composable
private fun DayContent(date: LocalDate, dayNameColor: Color, dayNumberColor: Color, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = DateUtils.dayOfWeekShort(date),
            fontSize = 11.sp,
            color = dayNameColor,
            textAlign = TextAlign.Center,
            fontFamily = Manrope,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = dayNumberColor,
            textAlign = TextAlign.Center,
            fontFamily = Manrope,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MonthCalendarDialog(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    datesWithWorkouts: Set<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    hazeState: HazeState
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            hazeState = hazeState,
            hasBorder = true
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Предыдущий месяц", tint = TextPrimary)
                    }
                    Text(
                        text = DateUtils.formatMonthYear(currentMonth.atDay(1)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = Manrope
                    )
                    IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                        Icon(Icons.Default.KeyboardArrowRight, "Следующий месяц", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { name ->
                        Text(
                            text = name,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (name == "Сб" || name == "Вс") Color(0xFFEF4444) else TextSecondary,
                            textAlign = TextAlign.Center,
                            fontFamily = Manrope
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

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
                                                isSelected -> PrimaryGreen
                                                hasWorkout -> PrimaryGreen.copy(alpha = 0.3f)
                                                else -> Color.Transparent
                                            },
                                            shape = CircleShape
                                        )
                                        .clip(CircleShape)
                                        .clickable { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> Color.White
                                            isWeekend -> Color(0xFFEF4444)
                                            else -> TextPrimary
                                        },
                                        fontFamily = Manrope
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Закрыть", color = PrimaryGreen, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}