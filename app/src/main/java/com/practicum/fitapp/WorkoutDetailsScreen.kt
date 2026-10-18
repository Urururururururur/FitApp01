package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.training.WorkoutDetailsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    workoutId: Long,
    onBack: () -> Unit,
    viewModel: WorkoutDetailsViewModel = hiltViewModel()
) {
    var showExercisePicker by remember { mutableStateOf(false) }
    val workout by viewModel.workout.collectAsStateWithLifecycle()
    val exercises by viewModel.exercisesForWorkout.collectAsStateWithLifecycle()

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = workout!!.name,
                    fontSize = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Статус: ${if (workout!!.isCompleted) "Выполнена" else "Ожидание"}")
                Text(text = "Заметка: ${workout!!.note.ifBlank { "нет" }}")
                Spacer(modifier = Modifier.height(32.dp))

                // Здесь список упражнений
                Text(
                    text = "Упражнения (${exercises.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showExercisePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Добавить упражнение")
                }
                Spacer(modifier = Modifier.height(8.dp))


                if (exercises.isEmpty()) {
                    Text(
                        text = "Пока нет упражнений",
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    exercises.forEach { exercise ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = exercise.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = exercise.muscleGroupList.joinToString(", ") { it.displayName },
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
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