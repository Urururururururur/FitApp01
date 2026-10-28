package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import com.practicum.fitapp.presentation.common.GlassCard
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
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent, // Прозрачный фон для градиента
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: "Упражнение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Список подходов
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Отступ снизу (120dp), чтобы последняя карточка не пряталась за кнопкой
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp)
            ) {
                items(sets, key = { it.id }) { set ->

                    // Состояние свайпа для удаления
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart && isEditable) {
                                viewModel.deleteSet(set.id)
                                true
                            } else false
                        }
                    )

                    // Обертка свайпа
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(modifier = Modifier.fillMaxSize()) // Прозрачный фон при свайпе
                        },
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true
                    ) {
                        // Белая стеклянная карточка подхода
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            hasShadow = false,
                            hasBorder = false
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
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

                                // ВЕРХНЯЯ СТРОКА (Поля ввода разнесены по краям)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween, // ← Разносим по краям
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // ---- ВЕС (для силовых) ----
                                    if (category == com.practicum.fitapp.data.local.ExerciseCategory.STRENGTH) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Кнопка минус (увеличена)
                                            TextButton(
                                                onClick = {
                                                    val nw = (weightText.replace(',', '.').toDoubleOrNull() ?: 0.0) - 2.5
                                                    weightText = nw.toString()
                                                    viewModel.updateSet(set.copy(weight = maxOf(nw, 0.0)))
                                                },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(48.dp), // ← Больше размер
                                                enabled = isEditable
                                            ) { Text("−", fontSize = 28.sp) } // ← Больше шрифт

                                            OutlinedTextField(
                                                value = weightText,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*[.,]?\\d*"))) {
                                                        weightText = newValue
                                                        val parsed = newValue.replace(',', '.').toDoubleOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(weight = parsed))
                                                    }
                                                },
                                                label = { Text("Вес", fontSize = 10.sp) },
                                                modifier = Modifier.width(75.dp),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                enabled = isEditable
                                            )

                                            // Кнопка плюс (увеличена)
                                            TextButton(
                                                onClick = {
                                                    val nw = (weightText.replace(',', '.').toDoubleOrNull() ?: 0.0) + 2.5
                                                    weightText = nw.toString()
                                                    viewModel.updateSet(set.copy(weight = nw))
                                                },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(48.dp), // ← Больше размер
                                                enabled = isEditable
                                            ) { Text("+", fontSize = 28.sp) } // ← Больше шрифт
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
                                                modifier = Modifier.size(48.dp),
                                                enabled = isEditable
                                            ) { Text("−", fontSize = 28.sp) }

                                            OutlinedTextField(
                                                value = repsText,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                        repsText = newValue
                                                        val parsed = newValue.toIntOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(reps = parsed))
                                                    }
                                                },
                                                label = { Text("Повт.", fontSize = 10.sp) },
                                                modifier = Modifier.width(75.dp),
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
                                                modifier = Modifier.size(48.dp),
                                                enabled = isEditable
                                            ) { Text("+", fontSize = 28.sp) }
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
                                            label = { Text("Дист, м", fontSize = 10.sp) },
                                            modifier = Modifier.width(90.dp),
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
                                            label = { Text("Время, с", fontSize = 10.sp) },
                                            modifier = Modifier.width(90.dp),
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
                                            label = { Text("Время, с", fontSize = 10.sp) },
                                            modifier = Modifier.width(120.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            enabled = isEditable
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

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
                }
            }

            // 2. Плавающая кнопка "+ Добавить подход" (в стиле добавить тренировку)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isEditable) { viewModel.addSet() },
                    shape = RoundedCornerShape(16.dp),
                    solidColor = if (isEditable) Color(0xFF81C784) else Color.Gray, // Сплошной цвет
                    hasShadow = true,
                    hasBorder = false
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ Добавить подход",
                            color = Color.White, // Белый текст для контраста
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
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