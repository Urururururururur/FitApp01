package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.CatalogDay
import com.practicum.fitapp.presentation.training.CatalogProgram

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailsScreen(
    program: CatalogProgram,
    onBack: () -> Unit,
    onAddToDiary: (CatalogDay) -> Unit
) {
    var selectedDayIndex by remember { mutableStateOf(0) }
    val currentDay = program.days[selectedDayIndex]

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Программа") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
        // Убрали bottomBar, чтобы список мог прокручиваться под кнопкой
    ) { padding ->
        // Используем Box для наложения кнопки поверх контента
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. Основной контент (Скроллится под кнопкой)
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Шапка программы
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = program.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = program.subtitle,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = program.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Табы для дней
                if (program.days.size > 1) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedDayIndex,
                        edgePadding = 16.dp,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
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

                // Список упражнений
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    // Отступ снизу (120.dp), чтобы последняя карточка могла уехать выше кнопки
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp)
                ) {
                    items(currentDay.exercises) { exercise ->
                        // Белые стеклянные карточки (glassColor по умолчанию White)
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            hasShadow = false,
                            hasBorder = false
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = exercise.name,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${exercise.sets} × ${exercise.reps} · Отдых: ${exercise.rest}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (exercise.note != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = exercise.note,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Парящая кнопка "Добавить в дневник" (Поверх списка)
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
                        .clickable { onAddToDiary(currentDay) },
                    shape = RoundedCornerShape(16.dp),
                    solidColor = Color(0xFF81C784), // ← Сплошной зеленый цвет (без прозрачности)
                    hasShadow = true,
                    hasBorder = false
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Добавить ${currentDay.day} в дневник",
                            color = Color.White, // ← Делаем текст белым для контраста с плотным фоном
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}