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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint

@Composable
fun GlassCaard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    solidColor: Color? = null,
    glassColor: Color = Color.White.copy(alpha = 0.45f),
    hasShadow: Boolean = true,
    hasBorder: Boolean = true,
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
            Modifier.shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
        } else Modifier)
        .clip(shape)

    // Применяем размытие Haze (если стейт передан)
    boxModifier = if (hazeState != null) {
        boxModifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 40.dp,
                tints = listOf(HazeTint(glassColor))
            )
        )
    } else {
        boxModifier.background(glassColor)
    }

    // НЕОДНОРОДНАЯ ОБВОДКА (как в iOS)
    if (hasBorder) {
        boxModifier = boxModifier.border(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f), // Яркий блик сверху-слева
                    Color.White.copy(alpha = 0.05f) // Растворение (тень) снизу-справа
                ),
                start = Offset(0f, 0f),         // Начало градиента (верхний левый угол)
                end = Offset.Infinite          // Конец градиента (нижний правый угол)
            ),
            shape = shape
        )
    }

    Box(modifier = boxModifier) {
        content()
    }
}