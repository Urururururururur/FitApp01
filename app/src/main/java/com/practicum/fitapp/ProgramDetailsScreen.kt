package com.practicum.fitapp

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack // <-- ОБНОВЛЕННЫЙ ИМПОРТ
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
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.CatalogDay
import com.practicum.fitapp.presentation.training.CatalogProgram
import com.practicum.fitapp.ui.theme.*

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailsScreen(
    program: CatalogProgram,
    onBack: () -> Unit,
    onAddToDiary: (CatalogDay) -> Unit
) {
    // 3. ИСПРАВЛЕНИЕ: mutableIntStateOf вместо mutableStateOf
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val currentDay = program.days[selectedDayIndex]

    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Image(
            painter = painterResource(id = R.drawable.app_background), // 4. Убрали лишнее com.practicum.fitapp
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
                            "Программа",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            // 2. ИСПРАВЛЕНИЕ: AutoMirrored версия иконки
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Шапка программы
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            text = program.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                        Text(
                            text = program.subtitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = program.description,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontFamily = Manrope,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Табы для дней
                    if (program.days.size > 1) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
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
                                program.days.forEachIndexed { index, day ->
                                    val isSelected = selectedDayIndex == index

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isSelected) PrimaryGreen else Color.Transparent)
                                            .clickable { selectedDayIndex = index }
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day.day,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextSecondary,
                                            fontFamily = Manrope
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${currentDay.day}: ${currentDay.title}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        fontFamily = Manrope,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    // Список упражнений
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp)
                    ) {
                        items(currentDay.exercises) { exercise ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                hazeState = hazeState,
                                hasShadow = true,
                                hasBorder = true
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = exercise.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary,
                                        fontFamily = Manrope
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${exercise.sets} × ${exercise.reps} · Отдых: ${exercise.rest}",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        fontFamily = Manrope
                                    )

                                    // 1. ИСПРАВЛЕНИЕ: Безопасная работа с nullable-полем note
                                    exercise.note?.let { note ->
                                        if (note.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = note,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = PrimaryGreen,
                                                fontFamily = Manrope
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Плавающая кнопка
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { onAddToDiary(currentDay) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Добавить ${currentDay.day} в дневник",
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