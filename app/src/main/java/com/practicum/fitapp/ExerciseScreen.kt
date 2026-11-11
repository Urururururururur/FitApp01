package com.practicum.fitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ExerciseCategory
import com.practicum.fitapp.data.local.WorkoutStatus
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.ExerciseViewModel
import com.practicum.fitapp.ui.theme.*

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    workoutExerciseId: Long,
    onBack: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val workoutExercise by viewModel.workoutExercise.collectAsStateWithLifecycle()
    val exercise by viewModel.exercise.collectAsStateWithLifecycle()
    val category = exercise?.categoryEnum ?: ExerciseCategory.STRENGTH
    val sets by viewModel.sets.collectAsStateWithLifecycle()
    val workout by viewModel.workout.collectAsStateWithLifecycle()

    // Можно ли редактировать? Нет, если тренировка завершена.
    val isEditable = workout?.status != WorkoutStatus.COMPLETED.name

    // Стейт для размытия
    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        // Фон
        Image(
            painter = painterResource(id = com.practicum.fitapp.R.drawable.app_background),
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
                            text = exercise?.name ?: "Упражнение",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = TextPrimary
                            )
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
                            modifier = Modifier.padding(vertical = 4.dp), // <-- 1. ДОБАВИЛИ ОТСТУП СЮДА
                            backgroundContent = {
                                // Красная подложка с корзиной (теперь она идеально по размеру карточки)
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
                            enableDismissFromEndToStart = isEditable
                        ) {
                            // Карточка подхода
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth(), // <-- 2. УБРАЛИ ОТСТУП ОТСЮДА
                                shape = RoundedCornerShape(16.dp),
                                hazeState = hazeState,
                                hasShadow = true,
                                hasBorder = true
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
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

                                    // ВЕРХНЯЯ СТРОКА (Поля ввода)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (category == ExerciseCategory.STRENGTH) {
                                            SetValueInput(
                                                label = "Вес",
                                                value = weightText,
                                                isDecimal = true,
                                                enabled = isEditable,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*[.,]?\\d*"))) {
                                                        weightText = newValue
                                                        val parsed = newValue.replace(',', '.').toDoubleOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(weight = parsed))
                                                    }
                                                },
                                                onMinus = {
                                                    val nw = (weightText.replace(',', '.').toDoubleOrNull() ?: 0.0) - 2.5
                                                    weightText = nw.toString()
                                                    viewModel.updateSet(set.copy(weight = maxOf(nw, 0.0)))
                                                },
                                                onPlus = {
                                                    val nw = (weightText.replace(',', '.').toDoubleOrNull() ?: 0.0) + 2.5
                                                    weightText = nw.toString()
                                                    viewModel.updateSet(set.copy(weight = nw))
                                                }
                                            )
                                        }

                                        if (category == ExerciseCategory.STRENGTH || category == ExerciseCategory.BODYWEIGHT) {
                                            SetValueInput(
                                                label = "Повт.",
                                                value = repsText,
                                                isDecimal = false,
                                                enabled = isEditable,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                        repsText = newValue
                                                        val parsed = newValue.toIntOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(reps = parsed))
                                                    }
                                                },
                                                onMinus = {
                                                    val nr = (repsText.toIntOrNull() ?: 0) - 1
                                                    repsText = nr.toString()
                                                    viewModel.updateSet(set.copy(reps = maxOf(nr, 0)))
                                                },
                                                onPlus = {
                                                    val nr = (repsText.toIntOrNull() ?: 0) + 1
                                                    repsText = nr.toString()
                                                    viewModel.updateSet(set.copy(reps = nr))
                                                }
                                            )
                                        }

                                        if (category == ExerciseCategory.CARDIO) {
                                            SetValueInput(
                                                label = "Дист, м",
                                                value = distanceText,
                                                isDecimal = true,
                                                enabled = isEditable,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*[.,]?\\d*"))) {
                                                        distanceText = newValue
                                                        val parsed = newValue.replace(',', '.').toDoubleOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(distance = parsed))
                                                    }
                                                },
                                                onMinus = {
                                                    val nd = (distanceText.replace(',', '.').toDoubleOrNull() ?: 0.0) - 100.0
                                                    distanceText = nd.toString()
                                                    viewModel.updateSet(set.copy(distance = maxOf(nd, 0.0)))
                                                },
                                                onPlus = {
                                                    val nd = (distanceText.replace(',', '.').toDoubleOrNull() ?: 0.0) + 100.0
                                                    distanceText = nd.toString()
                                                    viewModel.updateSet(set.copy(distance = nd))
                                                }
                                            )
                                        }

                                        if (category == ExerciseCategory.CARDIO || category == ExerciseCategory.HOLD) {
                                            SetValueInput(
                                                label = "Время, с",
                                                value = durationText,
                                                isDecimal = false,
                                                enabled = isEditable,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                        durationText = newValue
                                                        val parsed = newValue.toIntOrNull()
                                                        if (parsed != null) viewModel.updateSet(set.copy(duration = parsed))
                                                    }
                                                },
                                                onMinus = {
                                                    val nd = (durationText.toIntOrNull() ?: 0) - 10
                                                    durationText = nd.toString()
                                                    viewModel.updateSet(set.copy(duration = maxOf(nd, 0)))
                                                },
                                                onPlus = {
                                                    val nd = (durationText.toIntOrNull() ?: 0) + 10
                                                    durationText = nd.toString()
                                                    viewModel.updateSet(set.copy(duration = nd))
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

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
                                            color = AccentBlue, enabled = isEditable,
                                            onClick = { viewModel.updateSet(set.copy(intensity = "EASY")) }
                                        )
                                        IntensityChip(
                                            label = "НОРМ", isSelected = set.intensity == "NORMAL",
                                            color = PrimaryGreen, enabled = isEditable,
                                            onClick = { viewModel.updateSet(set.copy(intensity = "NORMAL")) }
                                        )
                                        IntensityChip(
                                            label = "СЛОЖ", isSelected = set.intensity == "HARD",
                                            color = AccentOrange, enabled = isEditable,
                                            onClick = { viewModel.updateSet(set.copy(intensity = "HARD")) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Плавающая кнопка "+ Добавить подход"
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGreen.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isEditable) PrimaryGreen else Color.Gray)
                            .clickable(enabled = isEditable) { viewModel.addSet() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Добавить подход",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = Manrope
                        )
                    }
                }
            }
        }
    }
}

// Кастомный компонент для ввода значений (+/-)
@Composable
private fun SetValueInput(
    label: String,
    value: String,
    isDecimal: Boolean,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary,
            fontFamily = Manrope
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Кнопка минус
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
                    .clickable(enabled = enabled) { onMinus() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Минус", tint = TextPrimary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Поле ввода
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Manrope,
                    textAlign = TextAlign.Center,
                    color = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(PrimaryGreen),
                modifier = Modifier.width(60.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Кнопка плюс
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
                    .clickable(enabled = enabled) { onPlus() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Плюс", tint = TextPrimary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// Чип интенсивности
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
            color = if (isSelected) color else Color.Gray.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) color else TextSecondary,
            fontFamily = Manrope,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}