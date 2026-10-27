package com.practicum.fitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.practicum.fitapp.presentation.training.WorkoutDetailsViewModel
import kotlinx.coroutines.delay

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали тренировки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (workout == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = workout!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            editedName = workout!!.name
                            showNameDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать название")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val statusEnum = WorkoutStatus.valueOf(workout!!.status)
                    Text(text = "Статус: ${statusEnum.displayName}")

                    Text(
                        text = "Заметка: ${workout!!.note.ifBlank { "нет" }}",
                        modifier = Modifier.clickable {
                            editedNote = workout!!.note
                            showNoteDialog = true
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Блок с таймером/кнопками
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
                        Text(
                            text = "Идёт: ${formatDuration(elapsedSeconds)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (statusEnum == WorkoutStatus.IN_PROGRESS) {
                        Button(
                            onClick = { viewModel.finishWorkout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                        ) { Text("ЗАКОНЧИТЬ ТРЕНИРОВКУ", fontSize = 16.sp) }
                    } else if (statusEnum == WorkoutStatus.WAITING) {
                        Button(
                            onClick = { viewModel.startWorkout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                        ) { Text("НАЧАТЬ ТРЕНИРОВКУ", fontSize = 16.sp) }
                    } else if (statusEnum == WorkoutStatus.COMPLETED) {
                        Text(
                            text = "Тренировка завершена",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "Упражнения (${exercisesWithId.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAddExerciseClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = workout!!.status != WorkoutStatus.COMPLETED.name
                    ) {
                        Text("+ Добавить упражнение")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (exercisesWithId.isEmpty()) {
                    item {
                        Text("Пока нет упражнений", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    .clickable(enabled = workout!!.status != WorkoutStatus.COMPLETED.name) {
                                        onExerciseClick(item.workoutExerciseId)
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(item.exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = item.exercise.muscleGroupList.joinToString(", ") { it.displayName },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = statsParts.joinToString(" · "),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

    // Диалоги
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Заметка к тренировке") },
            text = {
                OutlinedTextField(
                    value = editedNote,
                    onValueChange = { editedNote = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Текст заметки") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateNote(editedNote)
                    showNoteDialog = false
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Название тренировки") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Название") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateName(editedName)
                    showNameDialog = false
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("Отмена") }
            }
        )
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, secs)
    else "%02d:%02d".format(minutes, secs)
}