package com.practicum.fitapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.DateRange
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
import com.practicum.fitapp.presentation.training.CatalogProgram
import com.practicum.fitapp.presentation.training.builtInPrograms
import com.practicum.fitapp.ui.theme.*

// Импорты для Haze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

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
                            "Каталог программ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            // ИСПРАВЛЕНИЕ: Column вместо Box с жестким отступом
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // 1. Фильтры
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(30.dp),
                    hazeState = hazeState,
                    hasBorder = true
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
                                targetValue = if (isSelected) PrimaryGreen else Color.Transparent,
                                label = "bgColor"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else TextSecondary,
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
                                    fontFamily = Manrope,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Список программ
                LazyColumn(
                    modifier = Modifier.weight(1f), // Занимает оставшееся место
                    contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp)
                ) {
                    items(filteredPrograms) { program ->
                        ProgramGlassCard(program = program, onClick = { onProgramClick(program) }, hazeState = hazeState)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramGlassCard(program: CatalogProgram, onClick: () -> Unit, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        hasShadow = true,
        hasBorder = true,
        hazeState = hazeState
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ЛЕВАЯ ЧАСТЬ: Текст
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = program.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope
                )
                Text(
                    text = program.subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Теги (пилюли)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagPill(text = program.level)
                    TagPill(text = program.goal)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Подвал карточки (Дни и Пол)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${program.days.size} ${if (program.days.size == 1) "день" else "дня"} · ${program.gender}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontFamily = Manrope
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ПРАВАЯ ЧАСТЬ: Картинка
            Box(
                modifier = Modifier
                    .size(100.dp) // Увеличили размер
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.5f)), // Стеклянная заглушка
                contentAlignment = Alignment.Center
            ) {
                if (program.imageResId != null) {
                    Image(
                        painter = painterResource(id = program.imageResId),
                        contentDescription = "Картинка программы",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Иконка, если нет картинки
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TagPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PrimaryGreen.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen,
            fontFamily = Manrope
        )
    }
}