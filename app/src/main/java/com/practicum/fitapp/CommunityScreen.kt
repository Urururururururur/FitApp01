package com.practicum.fitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.community.AchievementUiState
import com.practicum.fitapp.presentation.community.CommunityViewModel
import androidx.compose.foundation.border
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                // ИСПРАВЛЕНИЕ: Заголовок теперь прямо в TopAppBar, рядом со стрелкой
                title = {
                    Text(
                        text = "Достижения",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // ИСПРАВЛЕНИЕ: Убрал отсюда старый дублирующийся Text("Достижения")

            Text(
                text = "Получено: ${achievements.count { it.isUnlocked }} из ${achievements.size}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(achievements) { item ->
                    AchievementCard(item)
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(item: AchievementUiState) {
    val unlocked = item.isUnlocked

    // Цвета в зависимости от статуса
    val cardColor = if (unlocked) {
        Color(0xFFE8F5E9) // Светло-зеленый для открытых
    } else {
        Color(0xFFF5F5F5) // Серый для закрытых
    }

    val titleColor = if (unlocked) MaterialTheme.colorScheme.onSurface else Color(0xFF9E9E9E)
    val descColor = if (unlocked) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFBDBDBD)

    // Находим картинку (
    val imageResId = when (item.achievement.id) {
        "first_workout" -> R.drawable.ic_ach_first_workout
        "ten_workouts" -> R.drawable.ic_ach_ten_workouts
        "fifty_workouts" -> R.drawable.ic_ach_fifty_workouts
          "first_ton" -> R.drawable.ic_ach_first_ton
          "ten_tons" -> R.drawable.ic_ach_ten_tons
          "first_5km" -> R.drawable.ic_ach_first_5km
         "first_10km" -> R.drawable.ic_ach_first_10km
         "diary_7" -> R.drawable.ic_ach_diary_7
         "diary_30" -> R.drawable.ic_ach_diary_30
         "ten_exercises" -> R.drawable.ic_ach_ten_exercises
        "first_pr" -> R.drawable.ic_first_pr
        "calorie_balance_5" -> R.drawable.ic_calorie_balance_5
        "perfect_day" -> R.drawable.ic_perfect_day
        "return_after_break" -> R.drawable.ic_return_after_break
        else -> 0
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        glassColor = cardColor,
        hasShadow = false, // Тень убрана
        hasBorder = false  // Рамка убрана
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // === КРУГЛАЯ КАРТИНКА ===
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(if (unlocked) Color.White else Color(0xFFE0E0E0)), // Подложка
                contentAlignment = Alignment.Center
            ) {
                if (imageResId != 0) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = imageResId),
                        contentDescription = item.achievement.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        // Если ачивка закрыта — делаем картинку черно-белой
                        colorFilter = if (unlocked) null else androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                            androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }
                        )
                    )
                } else {
                    Text(
                        text = item.achievement.emoji,
                        fontSize = 32.sp,
                        modifier = Modifier.alpha(if (unlocked) 1f else 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // === ТЕКСТ ===
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.achievement.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Text(
                    text = item.achievement.description,
                    fontSize = 12.sp,
                    color = descColor
                )
                if (unlocked && item.unlockedAt != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Получено: ${DateUtils.formatShort(DateUtils.timestampToLocalDate(item.unlockedAt))}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}