package com.practicum.fitapp.data.repository

import android.content.Context
import com.practicum.fitapp.data.remote.gigachat.ChatRequest
import com.practicum.fitapp.data.remote.gigachat.GigaChatApi
import com.practicum.fitapp.data.remote.gigachat.Message
import com.practicum.fitapp.presentation.common.ImageUtils

class AiCalorieRepository(
    private val api: GigaChatApi,
    private val context: Context
) {
    // ⚠️ ВАЖНО: Для теста мы пока используем хардкод токена.
    // Позже мы заменим это на нормальную авторизацию.
    // Токен нужно взять из твоего личного кабинета GigaChat (он начинается на "Bearer ...")
    private val testToken = "Bearer ТВОЙ_ТОКЕН_СЮДА"

    suspend fun analyzeFoodImage(imageUri: android.net.Uri): String? {
        // 1. Конвертируем картинку в Base64
        val base64Image = ImageUtils.uriToBase64(context, imageUri) ?: return null

        // 2. Формируем промпт (инструкцию для нейросети)
        // Мы просим её вернуть строго JSON, чтобы нам было проще его распарсить
        val prompt = """
            Ты — профессиональный диетолог. Проанализируй это изображение еды.
            Верни ответ СТРОГО в формате JSON без каких-либо других слов.
            Формат JSON:
            {
              "name": "Название блюда",
              "weight": 150.0,
              "calories": 250,
              "protein": 15.0,
              "fat": 10.0,
              "carbs": 20.0
            }
            Если на картинке не еда, верни {"error": "Not food"}.
        """.trimIndent()

        // 3. Формируем сообщение с картинкой
        val messageContent = """
            <img src="data:image/jpeg;base64,$base64Image" />
            $prompt
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                Message(role = "user", content = messageContent)
            )
        )

        // 4. Отправляем запрос
        return try {
            val response = api.sendChatMessage(
                bearerToken = testToken,
                request = request
            )
            // Возвращаем текстовый ответ (внутри будет наш JSON)
            response.choices.firstOrNull()?.message?.content
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}