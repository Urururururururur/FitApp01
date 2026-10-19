package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.practicum.fitapp.presentation.training.ExerciseViewModel

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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Верхняя строка: вес и кнопка удаления
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Вес
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

                                TextButton(onClick = { viewModel.deleteSet(set.id) }) {
                                    Text("✕", color = Color.Red)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Средняя строка: повторы
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
                                        viewModel.updateSet(set.copy(reps = maxOf(newReps, 0)))
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("+", fontSize = 20.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Нижняя строка: чипы интенсивности
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IntensityChip(
                                    label = "РАЗМ",
                                    isSelected = set.intensity == "WARMUP",
                                    color = Color.Gray,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "WARMUP")) }
                                )
                                IntensityChip(
                                    label = "ЛЕГК",
                                    isSelected = set.intensity == "EASY",
                                    color = Color(0xFF4CAF50),   // зелёный
                                    onClick = { viewModel.updateSet(set.copy(intensity = "EASY")) }
                                )
                                IntensityChip(
                                    label = "НОРМ",
                                    isSelected = set.intensity == "NORMAL",
                                    color = Color(0xFFFF9800),   // оранжевый
                                    onClick = { viewModel.updateSet(set.copy(intensity = "NORMAL")) }
                                )
                                IntensityChip(
                                    label = "СЛОЖ",
                                    isSelected = set.intensity == "HARD",
                                    color = Color(0xFFF44336),   // красный
                                    onClick = { viewModel.updateSet(set.copy(intensity = "HARD")) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка добавления подхода (одна на весь экран)
            Button(
                onClick = { viewModel.addSet() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Добавить подход")
            }
        }
    }
}

@Composable
private fun IntensityChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) color else Color.LightGray
        )
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) color else Color.Gray,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}