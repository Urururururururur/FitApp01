package com.practicum.fitapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.MuscleGroup
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.ExerciseCatalogViewModel
import com.practicum.fitapp.ui.theme.*

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCatalogScreen(
    onBack: () -> Unit,
    onExerciseAdded: () -> Unit,
    viewModel: ExerciseCatalogViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedMuscleGroup by viewModel.selectedMuscleGroup.collectAsStateWithLifecycle()
    val groupedExercises by viewModel.groupedExercises.collectAsStateWithLifecycle()

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
            containerColor = Color.Transparent, // Прозрачный фон
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Выбор упражнения",
                            fontSize = 24.sp,
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
            // ИСПРАВЛЕНИЕ: Убрали Box с наложением. Теперь обычная колонка.
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // 1. Поиск
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(30.dp),
                    hazeState = hazeState,
                    hasBorder = true // Включаем обводку для стекла
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text("Поиск упражнения...", color = TextSecondary, fontFamily = Manrope)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontFamily = Manrope, color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                // 2. Фильтры
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(30.dp),
                    hazeState = hazeState,
                    hasBorder = true
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val items = listOf<MuscleGroup?>(null) + MuscleGroup.entries

                        items.forEach { group ->
                            val isSelected = selectedMuscleGroup == group
                            val label = group?.displayName ?: "Все"

                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) PrimaryGreen else Color.Transparent,
                                label = "bgColor"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else TextSecondary,
                                label = "textColor"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(bgColor)
                                    .clickable { viewModel.setMuscleGroup(group) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textColor,
                                    fontFamily = Manrope,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Список упражнений
                if (groupedExercises.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ничего не найдено", color = TextSecondary, fontFamily = Manrope)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(), // weight(1f) заставляет список занимать оставшееся место
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
                    ) {
                        groupedExercises.forEach { group ->
                            item {
                                Text(
                                    text = group.subcategory,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen,
                                    fontFamily = Manrope,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                )
                            }
                            items(group.exercises) { exercise ->
                                ExerciseGlassItem(
                                    exercise = exercise,
                                    hazeState = hazeState,
                                    onClick = {
                                        viewModel.addExercise(exercise)
                                        onExerciseAdded()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Стеклянная карточка упражнения
@Composable
private fun ExerciseGlassItem(
    exercise: ExerciseEntity,
    hazeState: HazeState,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        hasShadow = true,  // Включили тень
        hasBorder = true,  // Включили обводку
        hazeState = hazeState
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая часть (Текст)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope
                )
                if (exercise.subcategory.isNotBlank()) {
                    Text(
                        text = exercise.subcategory,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = Manrope
                    )
                }
            }

            // Правая часть (Иконка)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Добавить",
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}