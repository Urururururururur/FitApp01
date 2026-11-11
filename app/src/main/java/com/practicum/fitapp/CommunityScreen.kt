package com.practicum.fitapp


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.common.DateUtils
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.community.AchievementUiState
import com.practicum.fitapp.presentation.community.CommunityViewModel
import com.practicum.fitapp.ui.theme.BgColor
import com.practicum.fitapp.ui.theme.Manrope
import com.practicum.fitapp.ui.theme.PrimaryGreen
import com.practicum.fitapp.ui.theme.TextPrimary
import com.practicum.fitapp.ui.theme.TextSecondary
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import android.annotation.SuppressLint
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()

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
                            text = "Достижения",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = TextPrimary
                            )
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
                Text(
                    text = "Получено: ${achievements.count { it.isUnlocked }} из ${achievements.size}",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(achievements) { item ->
                        AchievementCard(item = item, hazeState = hazeState)
                    }
                }
            }
        }
    }
}
@SuppressLint("DiscouragedApi")
@Composable
fun getAchievementImageResId(id: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier("ic_ach_$id", "drawable", context.packageName)
}
@Composable
private fun AchievementCard(item: AchievementUiState, hazeState: HazeState) {
    val unlocked = item.isUnlocked

    // Цвета текста
    val titleColor = if (unlocked) TextPrimary else Color.Black.copy(alpha = 0.4f)
    val descColor = if (unlocked) TextSecondary else Color.Black.copy(alpha = 0.4f)

    // Находим картинку (ИСПРАВЛЕНО: item.achievement.id)
    val imageResId = getAchievementImageResId(item.achievement.id)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        hazeState = hazeState
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // === КРУГЛАЯ КАРТИНКА ===
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = item.achievement.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        colorFilter = if (unlocked) null else ColorFilter.colorMatrix(
                            ColorMatrix().apply { setToSaturation(0f) }
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
                    color = titleColor,
                    fontFamily = Manrope
                )
                Text(
                    text = item.achievement.description,
                    fontSize = 12.sp,
                    color = descColor,
                    fontFamily = Manrope
                )
                if (unlocked && item.unlockedAt != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Получено: ${DateUtils.formatShort(DateUtils.timestampToLocalDate(item.unlockedAt))}",
                        fontSize = 11.sp,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Manrope
                    )
                }
            }

            // === ИКОНКА ГАЛОЧКИ (Только для открытых) ===
            if (unlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Выполнено",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}