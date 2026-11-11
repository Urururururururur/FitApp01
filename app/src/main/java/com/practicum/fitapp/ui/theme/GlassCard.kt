package com.practicum.fitapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    solidColor: Color? = null,
    glassColor: Color = Color.White.copy(alpha = 0.85f), // 85% плотности
    hasShadow: Boolean = false,
    hasBorder: Boolean = false,
    hazeState: HazeState? = null, // Параметр остался для совместимости, но не используется
    content: @Composable BoxScope.() -> Unit
) {
    if (solidColor != null) {
        Box(
            modifier = modifier
                .then(if (hasShadow) Modifier.shadow(4.dp, shape) else Modifier)
                .clip(shape)
                .background(solidColor)
        ) {
            content()
        }
        return
    }

    // Просто рисуем фон, без всякого Haze
    var boxModifier = modifier
        .then(if (hasShadow) {
            Modifier.shadow(12.dp, shape, ambientColor = Color.Black.copy(alpha = 0.08f))
        } else Modifier)
        .clip(shape)
        .background(glassColor) // Используем только цвет

    // Градиентная обводка (если включена)
    if (hasBorder) {
        boxModifier = boxModifier.border(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.05f))
            ),
            shape = shape
        )
    }

    Box(modifier = boxModifier) {
        content()
    }
}