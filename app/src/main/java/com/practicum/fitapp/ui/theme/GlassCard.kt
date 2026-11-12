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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    solidColor: Color? = null,
    glassColor: Color = Color.White.copy(alpha = 1f),
    hasShadow: Boolean = false,
    hasBorder: Boolean = false,
    hazeState: HazeState? = null,
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

    var boxModifier = modifier
        .then(if (hasShadow) {
            // Убрали ambientColor и spotColor, оставили стандартную черную тень.
            // Это решит проблему с белыми квадратами позади карточек.
            Modifier.shadow(
                elevation = 6.dp,
                shape = shape
            )
        } else Modifier)
        .clip(shape)
        .background(glassColor)

    // Если включена обводка, используем обычный белый цвет вместо градиента
    if (hasBorder) {
        boxModifier = boxModifier.border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.6f),
            shape = shape
        )
    }

    Box(modifier = boxModifier) {
        content()
    }
}