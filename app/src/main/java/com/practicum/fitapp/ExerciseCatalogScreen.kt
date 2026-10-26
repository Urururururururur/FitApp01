package com.practicum.fitapp

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
import com.practicum.fitapp.data.local.ExerciseEntity
import com.practicum.fitapp.data.local.MuscleGroup
import com.practicum.fitapp.presentation.training.ExerciseCatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCatalogScreen(
    onBack: () -> Unit,
    onExerciseAdded: () -> Unit, // Колбэк, чтобы вернуться назад после добавления
    viewModel: ExerciseCatalogViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedMuscleGroup by viewModel.selectedMuscleGroup.collectAsStateWithLifecycle()
    val groupedExercises by viewModel.groupedExercises.collectAsStateWithLifecycle()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор упражнения") },
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
        ) {
            // Поиск
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Поиск упражнения...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            // Фильтры по группам мышц
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Чип "Все"
                FilterChip(
                    selected = selectedMuscleGroup == null,
                    onClick = { viewModel.setMuscleGroup(null) },
                    label = { Text("Все") }
                )
                // Чипы групп мышц
                MuscleGroup.entries.forEach { group ->
                    FilterChip(
                        selected = selectedMuscleGroup == group,
                        onClick = { viewModel.setMuscleGroup(group) },
                        label = { Text(group.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Список упражнений
            if (groupedExercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ничего не найдено", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedExercises.forEach { group ->
                        // Заголовок субкатегории
                        item {
                            Text(
                                text = group.subcategory,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        // Упражнения внутри субкатегории
                        items(group.exercises) { exercise ->
                            ExerciseItem(
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
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: ExerciseEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = exercise.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (exercise.subcategory.isNotBlank()) {
                Text(
                    text = exercise.subcategory,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}