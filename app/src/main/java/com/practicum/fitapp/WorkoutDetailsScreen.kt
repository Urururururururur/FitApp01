package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.practicum.fitapp.presentation.training.WorkoutDetailsViewModel
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    workoutId: Long,
    onBack: () -> Unit,
    onExerciseClick: (Long) -> Unit,
    viewModel: WorkoutDetailsViewModel = hiltViewModel()
) {
    var showExercisePicker by remember { mutableStateOf(false) }
    val workout by viewModel.workout.collectAsStateWithLifecycle()
    val exerciseStats by viewModel.exerciseStats.collectAsStateWithLifecycle()
    val exercisesWithId by viewModel.exercisesWithId.collectAsStateWithLifecycle()
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
                CircularProgressIndicator() // Пока данные грузятся
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // 1. Заголовок тренировки
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
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать название"
                            )
                        }
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
                                }) {
                                    Text("Сохранить")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showNameDialog = false }) {
                                    Text("Отмена")
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val statusEnum = com.practicum.fitapp.data.local.WorkoutStatus.valueOf(workout!!.status)
                    Text(text = "Статус: ${statusEnum.displayName}")
                    Text(
                        text = "Заметка: ${workout!!.note.ifBlank { "нет" }}",
                        modifier = Modifier.clickable {
                            editedNote = workout!!.note
                            showNoteDialog = true
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                // Блок с кнопкой и статусом
                item {
                    val statusEnum = WorkoutStatus.valueOf(workout!!.status)

                    // Таймер (обновляется каждую секунду, если тренировка идёт)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Показываем таймер
                    if (statusEnum == WorkoutStatus.IN_PROGRESS) {
                        Text(
                            text = "Идёт: ${formatDuration(elapsedSeconds)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Кнопки управления
                    if (statusEnum == WorkoutStatus.IN_PROGRESS) {
                        Button(
                            onClick = { viewModel.finishWorkout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                        ) {
                            Text("ЗАКОНЧИТЬ ТРЕНИРОВКУ", fontSize = 16.sp)
                        }
                    } else if (statusEnum == WorkoutStatus.WAITING) {
                        Button(
                            onClick = { viewModel.startWorkout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                        ) {
                            Text("НАЧАТЬ ТРЕНИРОВКУ", fontSize = 16.sp)
                        }
                    } else if (statusEnum == WorkoutStatus.COMPLETED) {
                        Text(
                            text = "Тренировка завершена",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. Заголовок "Упражнения"
                item {
                    Text(
                        text = "Упражнения (${exercisesWithId.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 3. Кнопка "Добавить упражнение"
                item {
                    Button(
                        onClick = { showExercisePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Добавить упражнение")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. Список упражнений (используем items)
                if (exercisesWithId.isEmpty()) {
                    item {
                        Text("Пока нет упражнений", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    items(exercisesWithId) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onExerciseClick(item.workoutExerciseId) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = item.exercise.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = item.exercise.muscleGroupList.joinToString(", ") { it.displayName },
                                    fontSize = 12.sp
                                )
                                val stat = exerciseStats.find { it.workoutExerciseId == item.workoutExerciseId }
                                if (stat != null && stat.totalReps > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Макс: ${"%.0f".format(stat.maxWeight)} кг · Всего: ${"%.0f".format(stat.totalWeight)} кг · ${stat.totalReps} повт.",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
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
                            }) {
                                Text("Сохранить")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showNoteDialog = false }) {
                                Text("Отмена")
                            }
                        }
                    )
                }


            if (showExercisePicker) {
                // Простой диалог: список всех упражнений
                val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()

                AlertDialog(
                    onDismissRequest = { showExercisePicker = false },
                    title = { Text("Выберите упражнение") },
                    text = {
                        LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                            items(allExercises) { exercise ->
                                Text(
                                    text = "${exercise.name} (${exercise.categoryEnum.displayName})",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addExercise(exercise)
                                            showExercisePicker = false
                                        }
                                        .padding(12.dp)
                                )
                            }
                        }

                    },
                    confirmButton = {
                        TextButton(onClick = { showExercisePicker = false }) {
                            Text("Отмена")
                        }
                    }

                )
            }
        }
    }
}
/** Форматирует секунды в "ЧЧ:ММ:СС" (или "ММ:СС", если меньше часа). */
private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, secs)
    } else {
        "%02d:%02d".format(minutes, secs)
    }
}