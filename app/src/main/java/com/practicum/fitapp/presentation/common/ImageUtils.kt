package com.practicum.fitapp.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun rememberDrawablePainter(imageUrl: String?): Painter? {
    if (imageUrl.isNullOrBlank()) return null

    // Если это вдруг ссылка на сайт, не трогаем её (пусть грузится через Coil)
    if (imageUrl.startsWith("http")) return null

    val context = LocalContext.current

    // Берем имя файла без расширения и без пути
    val drawableName = imageUrl.substringBefore(".").substringAfterLast("/")

    // Ищем ID ресурса по имени
    val resId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)

    return if (resId != 0) {
        painterResource(id = resId)
    } else {
        null
    }
}