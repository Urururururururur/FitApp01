package com.practicum.fitapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    solidColor: Color? = null,
    glassColor: Color = Color.White,
    hasShadow: Boolean = true,
    hasBorder: Boolean = true, // ← НОВЫЙ ПАРАМЕТР (включает/выключает обводку)
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF121212)

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

    // Теперь градиент уважает прозрачность, которую мы передаем
    val glassGradient = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                glassColor,
                glassColor.copy(alpha = glassColor.alpha * 0.5f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                glassColor,
                glassColor.copy(alpha = glassColor.alpha * 0.5f)
            )
        )
    }

    val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.9f)

    Box(
        modifier = modifier
            .then(if (hasShadow) {
                Modifier.shadow(
                    elevation = 8.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.1f),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
            } else Modifier)
            .clip(shape)
            .background(brush = glassGradient)
            // Применяем обводку только если hasBorder = true
            .then(if (hasBorder) Modifier.border(width = 1.dp, color = borderColor, shape = shape) else Modifier)
    ) {
        content()
    }
}