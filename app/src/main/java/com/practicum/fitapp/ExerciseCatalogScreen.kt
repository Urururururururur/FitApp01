package com.practicum.fitapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.MuscleGroup
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.ExerciseCatalogViewModel

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent, // Прозрачный фон
        topBar = {
            TopAppBar(
                title = { Text("Выбор упражнения") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        // Box позволяет накладывать фильтры поверх списка
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1. Список упражнений (нижний слой)
            if (groupedExercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ничего не найдено", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // Отступ сверху (120.dp) для поиска и фильтров, снизу (16.dp) так как нижнего меню тут нет
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 120.dp, bottom = 16.dp)
                ) {
                    groupedExercises.forEach { group ->
                        item {
                            Text(
                                text = group.subcategory,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                            )
                        }
                        items(group.exercises) { exercise ->
                            ExerciseGlassItem(
                                exercise = exercise,
                                onClick = {
                                    viewModel.addExercise(exercise)
                                    onExerciseAdded()
                                }
                            )
                        }
                    }
                }
            }

            // 2. Парящая панель с поиском и фильтрами (верхний слой)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Поиск в стеклянном стиле
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    glassColor = Color.White.copy(alpha = 0.7f),
                    hasShadow = true,
                    hasBorder = false
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Поиск упражнения...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Фильтры по мышцам
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    glassColor = Color(0xFFC8E6C9).copy(alpha = 0.75f),
                    hasShadow = true,
                    hasBorder = false
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
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.Transparent,
                                label = "bgColor"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Белая стеклянная карточка упражнения
@Composable
private fun ExerciseGlassItem(
    exercise: ExerciseEntity,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        hasShadow = false,
        hasBorder = false
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = exercise.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (exercise.subcategory.isNotBlank()) {
                Text(
                    text = exercise.subcategory,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}