package com.practicum.fitapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.training.CatalogProgram
import com.practicum.fitapp.presentation.training.builtInPrograms

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramCatalogScreen(
    onBack: () -> Unit,
    onProgramClick: (CatalogProgram) -> Unit
) {
    var selectedLevel by remember { mutableStateOf<String?>(null) }

    val filteredPrograms = builtInPrograms.filter { program ->
        selectedLevel == null || program.level == selectedLevel
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Каталог программ") },
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
    ) { padding ->
        // Box позволяет накладывать элементы друг на друга
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. Список программ (нижний слой)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // contentPadding задает отступы внутри списка.
                // Top = 80.dp нужен, чтобы первая карточка не пряталась за фильтром.
                // Bottom = 100.dp, чтобы последняя карточка не пряталась за меню.
                contentPadding = PaddingValues(top = 80.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
            ) {
                items(filteredPrograms) { program ->
                    ProgramGlassCard(program = program, onClick = { onProgramClick(program) })
                }
            }

            // 2. Парящий фильтр (верхний слой)
            GlassCard(
                modifier = Modifier
                    .align(Alignment.TopCenter) // Прижимаем к верху
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                glassColor = Color(0xFFC8E6C9).copy(alpha = 0.75f), // Зеленоватое стекло
                hasShadow = true, // Включаем тень, чтобы он "парил"
                hasBorder = false
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val levels = listOf(null, "Новичок", "Средний", "Продвинутый")

                    levels.forEach { level ->
                        val isSelected = selectedLevel == level
                        val label = level ?: "Все"

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
                                .weight(1f)
                                .clip(RoundedCornerShape(50))
                                .background(bgColor)
                                .clickable { selectedLevel = level }
                                .padding(vertical = 8.dp),
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

@Composable
private fun ProgramGlassCard(program: CatalogProgram, onClick: () -> Unit) {
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
                text = program.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = program.subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = program.level,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = program.goal,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                program.tags.take(2).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${program.days.size} ${if (program.days.size == 1) "день" else "дня"} · ${program.gender}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}