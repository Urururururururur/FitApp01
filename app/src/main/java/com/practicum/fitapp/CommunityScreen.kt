package com.practicum.fitapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.community.AchievementUiState
import com.practicum.fitapp.presentation.community.CommunityViewModel

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Достижения",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
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
            // Отступ внизу, чтобы последняя карточка не пряталась за меню
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun AchievementCard(item: AchievementUiState) {
    val unlocked = item.isUnlocked

    // Цвет стекла: если открыто — зеленоватый, если закрыто — нейтральный белый/серый
    val cardColor = if (unlocked) {
        Color(0xFFC8E6C9).copy(alpha = 0.6f) // Зеленоватое стекло
    } else {
        Color.White.copy(alpha = 0.5f) // Нейтральное стекло
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        glassColor = cardColor,
        hasShadow = false,
        hasBorder = false
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Эмодзи (большое)
            Text(
                text = item.achievement.emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.achievement.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Цвет текста из темы
                )
                Text(
                    text = item.achievement.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

            // Галочка / замок
            Text(
                text = if (unlocked) "✅" else "🔒",
                fontSize = 20.sp
            )
        }
    }
}