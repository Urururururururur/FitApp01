package com.practicum.fitapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GradientBackground(
    isDarkTheme: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    // Цвета для темной темы (глубокий зеленый + черный)
    val darkBg = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0B1E12), // Почти черный с зеленым оттенком
            Color(0xFF121212), // Стандартный темный
            Color(0xFF1B2E20)  // Темно-зеленый
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    // Цвета для светлой темы (мягкий кремовый + светло-зеленый)
    val lightBg = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF1F8E9), // Очень светло-зеленый
            Color(0xFFFFFFFF), // Белый
            Color(0xFFE8F5E9)  // Чуть темнее зеленого
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) darkBg else lightBg)
    ) {
        // Опционально: можно добавить размытые пятна (blobs) для большего эффекта
         Box(
            modifier = Modifier
                 .size(300.dp)
                 .offset(x = (-50).dp, y = 100.dp)
                 .background(Color(0xFF4CAF50).copy(alpha = 0.15f), shape = CircleShape)
                .blur(80.dp)
         )

        content()
    }
}