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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.MuscleGroup
import com.practicum.fitapp.data.local.WorkoutStatus
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.training.TrainingViewModel
import java.time.LocalDate

@Composable
fun TrainingScreen(
    onWorkoutClick: (Long) -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val workouts by viewModel.workoutsForDay.collectAsStateWithLifecycle()
    val tags by viewModel.workoutTags.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = DateUtils.formatMonthYear(selectedDate),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            WeekStrip(
                selectedDate = selectedDate,
                onDayClick = { viewModel.setSelectedDate(it) }
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
}

@Composable
private fun WeekStrip(
    selectedDate: LocalDate,
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