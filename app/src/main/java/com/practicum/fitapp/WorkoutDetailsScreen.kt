package com.practicum.fitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.WorkoutDetailsViewModel
import com.practicum.fitapp.ui.theme.*
import kotlinx.coroutines.delay

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    workoutId: Long,
    onBack: () -> Unit,
    onExerciseClick: (Long) -> Unit,
    onAddExerciseClick: () -> Unit,
    viewModel: WorkoutDetailsViewModel = hiltViewModel()
) {
    val workout by viewModel.workout.collectAsStateWithLifecycle()
    val exercisesWithId by viewModel.exercisesWithId.collectAsStateWithLifecycle()
    val exerciseStats by viewModel.exerciseStats.collectAsStateWithLifecycle()

    var showNoteDialog by remember { mutableStateOf(false) }
    var editedNote by remember { mutableStateOf("") }
    var showNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }

    // Стейт для Haze
    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        // Фон
        Image(
            painter = painterResource(id = R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Детали тренировки",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            if (workout == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
                ) {
                    item {
                        // Шапка (Название, статус, заметка)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = workout!!.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                fontFamily = Manrope,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                editedName = workout!!.name
                                showNameDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Редактировать название", tint = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val statusEnum = WorkoutStatus.valueOf(workout!!.status)

                        // Статус-пилюля
                        val statusColor = when (statusEnum) {
                            WorkoutStatus.IN_PROGRESS -> PrimaryGreen
                            WorkoutStatus.COMPLETED -> PrimaryGreen
                            else -> TextSecondary
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusEnum.displayName,
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Заметка: ${workout!!.note.ifBlank { "нет" }}",
                            modifier = Modifier.clickable {
                                editedNote = workout!!.note
                                showNoteDialog = true
                            },
                            color = PrimaryGreen,
                            fontSize = 14.sp,
                            fontFamily = Manrope
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Блок с таймером
                        var elapsedSeconds by remember { mutableLongStateOf(0L) }
                        LaunchedEffect(workout!!.status) {
                            if (statusEnum == WorkoutStatus.IN_PROGRESS) {
                                val startTime = workout!!.date
                                while (true) {
                                    elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                                    delay(1000)
                                }
                            }
                        }

                        if (statusEnum == WorkoutStatus.IN_PROGRESS) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Идёт:",
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    fontFamily = Manrope
                                )
                                Text(
                                    text = formatDuration(elapsedSeconds),
                                    fontSize = 56.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontFamily = Manrope
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Кнопки управления (Начать / Закончить)
                        if (statusEnum == WorkoutStatus.IN_PROGRESS) {
                            Button(
                                onClick = { viewModel.finishWorkout() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), // Мягкий красный
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("ЗАКОНЧИТЬ ТРЕНИРОВКУ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Manrope)
                            }
                        } else if (statusEnum == WorkoutStatus.WAITING) {
                            Button(
                                onClick = { viewModel.startWorkout() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("НАЧАТЬ ТРЕНИРОВКУ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Manrope)
                            }
                        } else if (statusEnum == WorkoutStatus.COMPLETED) {
                            Text(
                                text = "Тренировка завершена",
                                color = PrimaryGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = Manrope,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    item {
                        Text(
                            text = "Упражнения (${exercisesWithId.size})",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Кнопка добавить упражнение (Secondary style)
                        if (workout!!.status != WorkoutStatus.COMPLETED.name) {
                            OutlinedButton(
                                onClick = onAddExerciseClick,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen)
                            ) {
                                Text("Добавить упражнение", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = Manrope)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (exercisesWithId.isEmpty()) {
                        item {
                            Text("Пока нет упражнений", color = TextSecondary, fontFamily = Manrope)
                        }
                    } else {
                        items(exercisesWithId, key = { it.workoutExerciseId }) { item ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart && workout!!.status != WorkoutStatus.COMPLETED.name) {
                                        viewModel.deleteExercise(item.workoutExerciseId)
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                modifier = Modifier.padding(vertical = 4.dp),
                                backgroundContent = {
                                    // Красная подложка при свайпе
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
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = workout!!.status != WorkoutStatus.COMPLETED.name) {
                                            onExerciseClick(item.workoutExerciseId)
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    hazeState = hazeState,
                                    hasShadow = true,
                                    hasBorder = true
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = item.exercise.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontFamily = Manrope
                                        )
                                        Text(
                                            text = item.exercise.muscleGroupList.joinToString(", ") { it.displayName },
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            fontFamily = Manrope
                                        )

                                        val stat = exerciseStats.find { it.workoutExerciseId == item.workoutExerciseId }
                                        if (stat != null) {
                                            val category = item.exercise.categoryEnum
                                            val statsParts = mutableListOf<String>()

                                            if (category == com.practicum.fitapp.data.local.ExerciseCategory.STRENGTH) {
                                                if (stat.maxWeight > 0) statsParts.add("Макс: ${"%.0f".format(stat.maxWeight)} кг")
                                                if (stat.totalWeight > 0) statsParts.add("Всего: ${"%.0f".format(stat.totalWeight)} кг")
                                            }
                                            if (stat.totalReps > 0) statsParts.add("${stat.totalReps} повт.")
                                            if (category == com.practicum.fitapp.data.local.ExerciseCategory.CARDIO) {
                                                if (stat.totalDistance > 0) statsParts.add("Дист: ${"%.0f".format(stat.totalDistance)} м")
                                                if (stat.totalDuration > 0) statsParts.add("Время: ${formatDuration(stat.totalDuration.toLong())}")
                                            }
                                            if (category == com.practicum.fitapp.data.local.ExerciseCategory.HOLD) {
                                                if (stat.totalDuration > 0) statsParts.add("Время: ${formatDuration(stat.totalDuration.toLong())}")
                                            }

                                            if (statsParts.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    statsParts.forEach { part ->
                                                        val isMaxWeight = part.contains("Макс:")
                                                        Text(
                                                            text = part,
                                                            fontSize = 12.sp,
                                                            fontWeight = if (isMaxWeight) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isMaxWeight) AccentOrange else TextSecondary,
                                                            fontFamily = Manrope
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог Заметки
    if (showNoteDialog) {
        Dialog(onDismissRequest = { showNoteDialog = false }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                hazeState = hazeState,
                hasBorder = true
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Заметка к тренировке", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editedNote,
                        onValueChange = { editedNote = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Текст заметки", color = TextSecondary, fontFamily = Manrope) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showNoteDialog = false }) {
                            Text("Отмена", color = TextSecondary, fontFamily = Manrope)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.updateNote(editedNote)
                                showNoteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Сохранить", color = Color.White, fontFamily = Manrope)
                        }
                    }
                }
            }
        }
    }

    // Диалог Названия
    if (showNameDialog) {
        Dialog(onDismissRequest = { showNameDialog = false }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                hazeState = hazeState,
                hasBorder = true
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Название тренировки", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Название", color = TextSecondary, fontFamily = Manrope) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showNameDialog = false }) {
                            Text("Отмена", color = TextSecondary, fontFamily = Manrope)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.updateName(editedName)
                                showNameDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Сохранить", color = Color.White, fontFamily = Manrope)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, secs)
    else "%02d:%02d".format(minutes, secs)
}