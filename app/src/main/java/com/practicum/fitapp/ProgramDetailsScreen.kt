package com.practicum.fitapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.fitapp.presentation.training.CatalogDay
import com.practicum.fitapp.presentation.training.CatalogProgram

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailsScreen(
    program: CatalogProgram,
    onBack: () -> Unit,
    onAddToDiary: (CatalogDay) -> Unit // ← Теперь передаем выбранный День
) {
    // Стейт текущего выбранного дня (по умолчанию первый)
    var selectedDayIndex by remember { mutableStateOf(0) }
    val currentDay = program.days[selectedDayIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Программа") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onAddToDiary(currentDay) }, // Передаем текущий день
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Добавить ${currentDay.day} в дневник")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Шапка программы
            Column(modifier = Modifier.padding(16.dp)) {
                Text(program.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(program.subtitle, fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(program.description, fontSize = 14.sp)
            }

            // Если дней больше одного, показываем табы
            if (program.days.size > 1) {
                ScrollableTabRow(
                    selectedTabIndex = selectedDayIndex,
                    edgePadding = 16.dp
                ) {
                    program.days.forEachIndexed { index, day ->
                        Tab(
                            selected = selectedDayIndex == index,
                            onClick = { selectedDayIndex = index },
                            text = { Text(day.day) }
                        )
                    }
                }
            }

            // Заголовок текущего дня
            Text(
                text = "${currentDay.day}: ${currentDay.title}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            // Список упражнений (используем LazyColumn, чтобы прокручивалось)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(currentDay.exercises) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(exercise.name, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${exercise.sets} × ${exercise.reps} · Отдых: ${exercise.rest}",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            if (exercise.note != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    exercise.note,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}