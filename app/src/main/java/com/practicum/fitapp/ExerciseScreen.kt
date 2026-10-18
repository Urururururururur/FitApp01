package com.practicum.fitapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.training.ExerciseViewModel
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    workoutExerciseId: Long,
    onBack: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val workoutExercise by viewModel.workoutExercise.collectAsStateWithLifecycle()
    val sets by viewModel.sets.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Упражнение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = workoutExercise?.note ?: "Загрузка...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Подходы", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(sets) { set ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Поля ввода: Вес и Повторения
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        val newWeight = (set.weight ?: 0.0) - 2.5
                                        viewModel.updateSet(set.copy(weight = maxOf(newWeight, 0.0)))
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("−", fontSize = 20.sp)
                                }

                                OutlinedTextField(
                                    value = set.weight?.toString() ?: "",
                                    onValueChange = { newValue ->
                                        val newWeight = newValue.toDoubleOrNull()
                                        viewModel.updateSet(set.copy(weight = newWeight))
                                    },
                                    label = { Text("Вес, кг", fontSize = 10.sp) },
                                    modifier = Modifier.width(80.dp),
                                    singleLine = true
                                )

                                TextButton(
                                    onClick = {
                                        val newWeight = (set.weight ?: 0.0) + 2.5
                                        viewModel.updateSet(set.copy(weight = newWeight))
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("+", fontSize = 20.sp)
                                }
                            }

                                // Повторения
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        val newReps = (set.reps ?: 0) - 1
                                        viewModel.updateSet(set.copy(reps = maxOf(newReps, 0)))
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("−", fontSize = 20.sp)
                                }

                                OutlinedTextField(
                                    value = set.reps?.toString() ?: "",
                                    onValueChange = { newValue ->
                                        val newReps = newValue.toIntOrNull()
                                        viewModel.updateSet(set.copy(reps = newReps))
                                    },
                                    label = { Text("Повторы", fontSize = 10.sp) },
                                    modifier = Modifier.width(80.dp),
                                    singleLine = true
                                )

                                TextButton(
                                    onClick = {
                                        val newReps = (set.reps ?: 0) + 1
                                        viewModel.updateSet(set.copy(reps = newReps))
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("+", fontSize = 20.sp)
                                }
                            }

                            // Кнопка удаления
                            TextButton(onClick = { viewModel.deleteSet(set.id) }) {
                                Text("✕", color = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.addSet() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Добавить подход")
            }
        }
    }
}