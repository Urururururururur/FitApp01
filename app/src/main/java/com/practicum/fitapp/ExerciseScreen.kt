package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
                    Text(exercise?.name ?: "Упражнение")
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
                items(sets, key = { it.id }) { set ->
                    // Локальный текст для полей
                    var weightText by remember(set.id) { mutableStateOf(set.weight?.toString() ?: "") }
                    var repsText by remember(set.id) { mutableStateOf(set.reps?.toString() ?: "") }
                    var distanceText by remember(set.id) { mutableStateOf(set.distance?.toString() ?: "") }
                    var durationText by remember(set.id) { mutableStateOf(set.duration?.toString() ?: "") }

                    // Синхронизация
                    LaunchedEffect(set.weight) {
                        if (set.weight != weightText.replace(',', '.').toDoubleOrNull()) {
                            weightText = set.weight?.toString() ?: ""
                        }
                    }
                    LaunchedEffect(set.reps) {
                        if (set.reps != repsText.toIntOrNull()) {
                            repsText = set.reps?.toString() ?: ""
                        }
                    }
                    LaunchedEffect(set.distance) {
                        if (set.distance != distanceText.replace(',', '.').toDoubleOrNull()) {
                            distanceText = set.distance?.toString() ?: ""
                        }
                    }
                    LaunchedEffect(set.duration) {
                        if (set.duration != durationText.toIntOrNull()) {
                            durationText = set.duration?.toString() ?: ""
                        }
                    }

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
                            // ВЕРХНЯЯ СТРОКА (Поля ввода + Кнопка удаления)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Контейнер для полей ввода (чтобы они были на одной строке)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // ---- ВЕС (для силовых) ----
                                    if (category == com.practicum.fitapp.data.local.ExerciseCategory.STRENGTH) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TextButton(
                                                onClick = {
                                                    val nw = (weightText.replace(',', '.').toDoubleOrNull() ?: 0.0) - 2.5
                                                    weightText = nw.toString()
                                                    viewModel.updateSet(set.copy(weight = maxOf(nw, 0.0)))
                                                },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(28.dp),
                                                enabled = isEditable
                                            ) { Text("−", fontSize = 18.sp) }

                                            OutlinedTextField(
                                                value = weightText,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*[.,]?\\d*"))) {
                                                        weightText = newValue
                                                        val parsed = newValue.replace(',', '.').toDoubleOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(weight = parsed))
                                                    }
                                                },
                                                label = { Text("Вес", fontSize = 9.sp) },
                                                modifier = Modifier.width(65.dp),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                enabled = isEditable
                                            )

                                            TextButton(
                                                onClick = {
                                                    val nw = (weightText.replace(',', '.').toDoubleOrNull() ?: 0.0) + 2.5
                                                    weightText = nw.toString()
                                                    viewModel.updateSet(set.copy(weight = nw))
                                                },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(28.dp),
                                                enabled = isEditable
                                            ) { Text("+", fontSize = 18.sp) }
                                        }
                                    }

                                    // ---- ПОВТОРЫ (для силовых и bodyweight) ----
                                    if (category == com.practicum.fitapp.data.local.ExerciseCategory.STRENGTH || category == com.practicum.fitapp.data.local.ExerciseCategory.BODYWEIGHT) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TextButton(
                                                onClick = {
                                                    val nr = (repsText.toIntOrNull() ?: 0) - 1
                                                    repsText = nr.toString()
                                                    viewModel.updateSet(set.copy(reps = maxOf(nr, 0)))
                                                },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(28.dp),
                                                enabled = isEditable
                                            ) { Text("−", fontSize = 18.sp) }

                                            OutlinedTextField(
                                                value = repsText,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                        repsText = newValue
                                                        val parsed = newValue.toIntOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(reps = parsed))
                                                    }
                                                },
                                                label = { Text("Повт.", fontSize = 9.sp) },
                                                modifier = Modifier.width(60.dp),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                enabled = isEditable
                                            )

                                            TextButton(
                                                onClick = {
                                                    val nr = (repsText.toIntOrNull() ?: 0) + 1
                                                    repsText = nr.toString()
                                                    viewModel.updateSet(set.copy(reps = nr))
                                                },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(28.dp),
                                                enabled = isEditable
                                            ) { Text("+", fontSize = 18.sp) }
                                        }
                                    }

                                    // ---- КАРДИО (Дистанция + Время) ----
                                    if (category == com.practicum.fitapp.data.local.ExerciseCategory.CARDIO) {
                                        OutlinedTextField(
                                            value = distanceText,
                                            onValueChange = { newValue ->
                                                if (newValue.isEmpty() || newValue.matches(Regex("\\d*[.,]?\\d*"))) {
                                                    distanceText = newValue
                                                    val parsed = newValue.replace(',', '.').toDoubleOrNull()
                                                    if (parsed != null) viewModel.updateSet(set.copy(distance = parsed))
                                                }
                                            },
                                            label = { Text("Дист, м", fontSize = 9.sp) },
                                            modifier = Modifier.width(75.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            enabled = isEditable
                                        )
                                        OutlinedTextField(
                                            value = durationText,
                                            onValueChange = { newValue ->
                                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                    durationText = newValue
                                                    val parsed = newValue.toIntOrNull()
                                                    if (parsed != null) viewModel.updateSet(set.copy(duration = parsed))
                                                }
                                            },
                                            label = { Text("Время, с", fontSize = 9.sp) },
                                            modifier = Modifier.width(75.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            enabled = isEditable
                                        )
                                    }

                                    // ---- HOLD (Только время) ----
                                    if (category == com.practicum.fitapp.data.local.ExerciseCategory.HOLD) {
                                        OutlinedTextField(
                                            value = durationText,
                                            onValueChange = { newValue ->
                                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                    durationText = newValue
                                                    val parsed = newValue.toIntOrNull()
                                                    if (parsed != null) viewModel.updateSet(set.copy(duration = parsed))
                                                }
                                            },
                                            label = { Text("Время, с", fontSize = 9.sp) },
                                            modifier = Modifier.width(100.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            enabled = isEditable
                                        )
                                    }
                                }

                                // Кнопка удаления
                                if (isEditable) {
                                    TextButton(
                                        onClick = { viewModel.deleteSet(set.id) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text("✕", color = Color.Red, fontSize = 18.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ---- ИНТЕНСИВНОСТЬ ----
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IntensityChip(
                                    label = "РАЗМ", isSelected = set.intensity == "WARMUP",
                                    color = Color.Gray, enabled = isEditable,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "WARMUP")) }
                                )
                                IntensityChip(
                                    label = "ЛЕГК", isSelected = set.intensity == "EASY",
                                    color = Color(0xFF4CAF50), enabled = isEditable,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "EASY")) }
                                )
                                IntensityChip(
                                    label = "НОРМ", isSelected = set.intensity == "NORMAL",
                                    color = Color(0xFFFF9800), enabled = isEditable,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "NORMAL")) }
                                )
                                IntensityChip(
                                    label = "СЛОЖ", isSelected = set.intensity == "HARD",
                                    color = Color(0xFFF44336), enabled = isEditable,
                                    onClick = { viewModel.updateSet(set.copy(intensity = "HARD")) }
                                )
                            }
                        }
                    }
                }
            } // ← Вот здесь закрывается LazyColumn

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка добавления подхода (она ВНЕ LazyColumn, всегда внизу)
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