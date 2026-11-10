package com.practicum.fitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Цвета
val DarkGreen = Color(0xFF1D7A4A)
val LightGreenBg = Color(0xFFE8F5E9)
val TextGray = Color(0xFF757575)

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        // Блок с шапкой (логотип и кнопка "Войти") удален.
        // Теперь контент начинается сразу с картинки.

        // --- БЛОК С КАРТИНКОЙ ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.LightGray) // Заглушка, пока нет картинки
        ) {

            Image(
                painter = painterResource(id = R.drawable.welcome_bg),
                contentDescription = "Welcome",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )


            // Бейдж сверху слева "В своём темпе"
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("В своём темпе", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                }
            }

            // Бейдж снизу "Маленькие шаги..."
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Маленькие шаги. Большие перемены.", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ЗАГОЛОВОК ---
        Text(
            text = "Твой день.\nТвой здоровый\nритм.",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 40.sp,
            color = Color(0xFF1E1E1E)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- ПОДЗАГОЛОВОК ---
        Text(
            text = "Питание, движение и тренировки — вместе, чтобы заботиться о себе было проще.",
            fontSize = 16.sp,
            color = TextGray,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- ИКОНКИ ФУНКЦИЙ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FeatureItem(icon = Icons.Default.Restaurant, text = "Питание", modifier = Modifier.weight(1f))
            FeatureItem(icon = Icons.Default.FitnessCenter, text = "Тренировки", modifier = Modifier.weight(1f))
            FeatureItem(icon = Icons.Default.MonitorHeart, text = "Трекер", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- КНОПКА ---
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Начать свой путь", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ФУТЕР ---
        Text(
            text = "Всего 2 минуты на знакомство",
            fontSize = 12.sp,
            color = TextGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Продолжая, вы принимаете Условия использования и Политику конфиденциальности",
            fontSize = 10.sp,
            color = Color.LightGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}