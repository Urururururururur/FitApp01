package com.practicum.fitapp.presentation.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {
    /**
     * Конвертирует изображение (Uri) в строку Base64.
     * Мы также сжимаем картинку, чтобы она не весила слишком много.
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            // 1. Открываем поток данных из Uri
            val inputStream = context.contentResolver.openInputStream(uri)
            // 2. Декодируем в Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) return null

            // 3. Сжимаем картинку (уменьшаем качество и размер, чтобы не превысить лимиты API)
            val outputStream = ByteArrayOutputStream()
            // Сжимаем в JPEG с качеством 60% (для нейросети этого достаточно)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()

            // 4. Кодируем в Base64
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}