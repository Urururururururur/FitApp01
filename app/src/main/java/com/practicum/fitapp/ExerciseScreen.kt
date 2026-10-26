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
    val exercise by viewModel.exercise.collectAsStateWithLifecycle()
    val category = exercise?.categoryEnum ?: com.practicum.fitapp.data.local.ExerciseCategory.STRENGTH
    val sets by viewModel.sets.collectAsStateWithLifecycle()
    val workout by viewModel.workout.collectAsStateWithLifecycle()
// Можно ли редактировать? Нет, если тренировка завершена.
    val isEditable = workout?.status != com.practicum.fitapp.data.local.WorkoutStatus.COMPLETED.name

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(exercise?.name ?: "Упражнение")  // ← Берем имя из БД, если загрузилось
                },
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
                            // Верхняя строка: поля ввода + удалить
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Поля ввода в зависимости от категории
                                when (category) {
                                    com.practicum.fitapp.data.local.ExerciseCategory.STRENGTH -> {
                                        // Вес + повторения
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Вес
                                            Column {
                                                Text("Вес, кг", fontSize = 10.sp, color = Color.Gray)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    TextButton(
                                                        onClick = {
                                                            val nw = (set.weight ?: 0.0) - 2.5
                                                            viewModel.updateSet(set.copy(weight = maxOf(nw, 0.0)))
                                                        },
                                                        contentPadding = PaddingValues(0.dp),
                                                        modifier = Modifier.size(28.dp)
                                                    ) { Text("−", fontSize = 16.sp) }

                                                    OutlinedTextField(
                                                        value = set.weight?.toString() ?: "",
                                                        onValueChange = { v ->
                                                            viewModel.updateSet(set.copy(weight = v.toDoubleOrNull()))
                                                        },
                                                        enabled = isEditable,
                                                        modifier = Modifier.width(70.dp),
                                                        singleLine = true

                                                    )

                                                    TextButton(
                                                        onClick = {
                                                            val nw = (set.weight ?: 0.0) + 2.5
                                                            viewModel.updateSet(set.copy(weight = nw))
                                                        },
                                                        contentPadding = PaddingValues(0.dp),
                                                        modifier = Modifier.size(28.dp),
                                                        enabled = isEditable
                                                    ) { Text("+", fontSize = 16.sp) }
                                                }
                                            }

                                            // Повторы
                                            Column {
                                                Text("Повторы", fontSize = 10.sp, color = Color.Gray)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    TextButton(
                                                        onClick = {
                                                            val nr = (set.reps ?: 0) - 1
                                                            viewModel.updateSet(set.copy(reps = maxOf(nr, 0)))
                                                        },
                                                        contentPadding = PaddingValues(0.dp),
                                                        modifier = Modifier.size(28.dp),
                                                        enabled = isEditable
                                                    ) { Text("−", fontSize = 16.sp) }

                                                    OutlinedTextField(
                                                        value = set.reps?.toString() ?: "",
                                                        onValueChange = { v ->
                                                            viewModel.updateSet(set.copy(reps = v.toIntOrNull()))
                                                        },
                                                        modifier = Modifier.width(70.dp),
                                                        enabled = isEditable,
                                                        singleLine = true
                                                    )

                                                    TextButton(
                                                        onClick = {
                                                            val nr = (set.reps ?: 0) + 1
                                                            viewModel.updateSet(set.copy(reps = nr))
                                                        },
                                                        contentPadding = PaddingValues(0.dp),
                                                        modifier = Modifier.size(28.dp),
                                                        enabled = isEditable
                                                    ) { Text("+", fontSize = 16.sp) }
                                                }
                                            }
                                        }
                                    }
                                    com.practicum.fitapp.data.local.ExerciseCategory.BODYWEIGHT -> {
                                        // Только повторения
                                        Column {
                                            Text("Повторы", fontSize = 10.sp, color = Color.Gray)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                TextButton(
                                                    onClick = {
                                                        val nr = (set.reps ?: 0) - 1
                                                        viewModel.updateSet(set.copy(reps = maxOf(nr, 0)))
                                                    },
                                                    contentPadding = PaddingValues(0.dp),
                                                    modifier = Modifier.size(28.dp),
                                                    enabled = isEditable
                                                ) { Text("−", fontSize = 16.sp) }

                                                OutlinedTextField(
                                                    value = set.reps?.toString() ?: "",
                                                    onValueChange = { v ->
                                                        viewModel.updateSet(set.copy(reps = v.toIntOrNull()))
                                                    },
                                                    modifier = Modifier.width(70.dp),
                                                    singleLine = true,
                                                    enabled = isEditable
                                                )

                                                TextButton(
                                                    onClick = {
                                                        val nr = (set.reps ?: 0) + 1
                                                        viewModel.updateSet(set.copy(reps = nr))
                                                    },
                                                    contentPadding = PaddingValues(0.dp),
                                                    modifier = Modifier.size(28.dp),
                                                    enabled = isEditable
                                                ) { Text("+", fontSize = 16.sp) }
                                            }
                                        }
                                    }
                                    com.practicum.fitapp.data.local.ExerciseCategory.CARDIO -> {
                                        // Дистанция + время
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Дистанция, м", fontSize = 10.sp, color = Color.Gray)
                                                OutlinedTextField(
                                                    value = set.distance?.toString() ?: "",
                                                    onValueChange = { v ->
                                                        viewModel.updateSet(set.copy(distance = v.toDoubleOrNull()))
                                                    },
                                                    modifier = Modifier.width(90.dp),
                                                    singleLine = true,
                                                    enabled = isEditable
                                                )
                                            }
                                            Column {
                                                Text("Время, сек", fontSize = 10.sp, color = Color.Gray)
                                                OutlinedTextField(
                                                    value = set.duration?.toString() ?: "",
                                                    onValueChange = { v ->
                                                        viewModel.updateSet(set.copy(duration = v.toIntOrNull()))
                                                    },
                                                    modifier = Modifier.width(90.dp),
                                                    singleLine = true,
                                                    enabled = isEditable
                                                )
                                            }
                                        }
                                    }
                                    com.practicum.fitapp.data.local.ExerciseCategory.HOLD -> {
                                        // Только время
                                        Column {
                                            Text("Время, сек", fontSize = 10.sp, color = Color.Gray)
                                            OutlinedTextField(
                                                value = set.duration?.toString() ?: "",
                                                onValueChange = { v ->
                                                    viewModel.updateSet(set.copy(duration = v.toIntOrNull()))
                                                },
                                                modifier = Modifier.width(120.dp),
                                                singleLine = true,
                                                enabled = isEditable
                                            )
                                        }
                                    }
                                }

                                // Кнопка удаления
                                if (isEditable) {
                                    TextButton(onClick = { viewModel.deleteSet(set.id) }) {
                                        Text("✕", color = Color.Red)
                                    }
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
                                    enabled = isEditable,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "WARMUP")) }
                                )
                                IntensityChip(
                                    label = "ЛЕГК",
                                    isSelected = set.intensity == "EASY",
                                    color = Color(0xFF4CAF50),
                                    enabled = isEditable,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "EASY")) }
                                )
                                IntensityChip(
                                    label = "НОРМ",
                                    isSelected = set.intensity == "NORMAL",
                                    color = Color(0xFFFF9800),
                                    enabled = isEditable,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "NORMAL")) }
                                )
                                IntensityChip(
                                    label = "СЛОЖ",
                                    isSelected = set.intensity == "HARD",
                                    color = Color(0xFFF44336),
                                    enabled = isEditable,
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
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditable
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
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
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