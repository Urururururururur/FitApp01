package com.practicum.fitapp.data.repository

import android.content.Context
import com.google.gson.Gson
import com.practicum.fitapp.data.remote.gigachat.*
import com.practicum.fitapp.presentation.nutrition.AiFoodResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID

class AiCalorieRepository(
    private val api: GigaChatApi,
    private val context: Context
) {
    private val authorizationKey = "Basic ТУТ_БУДЕТ_ТВОЙ_КЛЮЧ" // Не забудь свой ключ

    private var cachedToken: String? = null
    private var tokenExpiresAt: Long = 0

    private suspend fun getValidToken(): String {
        val now = System.currentTimeMillis()
        if (cachedToken != null && now < tokenExpiresAt - 60_000) return cachedToken!!
        val response = api.getToken(authorizationKey = authorizationKey, rquid = UUID.randomUUID().toString())
        cachedToken = response.accessToken
        tokenExpiresAt = response.expiresAt
        return response.accessToken
    }

    // Базовый промпт для обоих случаев
    private val basePrompt = """
        Верни ответ СТРОГО в формате JSON без каких-либо других слов и без Markdown.
        Формат JSON:
        {
          "name": "Название блюда",
          "caloriesPer100g": 250,
          "proteinPer100g": 15.0,
          "fatPer100g": 10.0,
          "carbsPer100g": 20.0
        }
        Если это не еда, верни {"error": "Not food"}.
    """.trimIndent()

    suspend fun analyzeFoodImage(imageUri: android.net.Uri): AiFoodResult {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val tempFile = File.createTempFile("food_", ".jpg", context.cacheDir)
        inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }

        return try {
            val token = getValidToken()
            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
            val purposeBody = "general".toRequestBody("text/plain".toMediaTypeOrNull())

            val uploadResponse = api.uploadFile(bearerToken = "Bearer $token", file = filePart, purpose = purposeBody)
            val fileId = uploadResponse.id

            val request = ChatRequest(
                messages = listOf(Message(role = "user", content = "Проанализируй фото еды. $basePrompt", attachments = listOf(fileId)))
            )
            val response = api.sendChatMessage("Bearer $token", request)
            parseAiResponse(response)

        } catch (e: Exception) {
            e.printStackTrace()
            AiFoodResult(error = "Ошибка: ${e.message}")
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }

    // НОВЫЙ МЕТОД: Текстовый запрос
    suspend fun analyzeFoodText(query: String): AiFoodResult {
        return try {
            val token = getValidToken()
            val request = ChatRequest(
                messages = listOf(Message(role = "user", content = "Определи БЖУ для блюда: '$query'. $basePrompt"))
            )
            val response = api.sendChatMessage("Bearer $token", request)
            parseAiResponse(response)
        } catch (e: Exception) {
            e.printStackTrace()
            AiFoodResult(error = "Ошибка: ${e.message}")
        }
    }

    private fun parseAiResponse(response: ChatResponse): AiFoodResult {
        val jsonString = response.choices.firstOrNull()?.message?.content
        val cleanJson = jsonString?.replace("```json", "")?.replace("```", "")?.trim()
        return Gson().fromJson(cleanJson, AiFoodResult::class.java) ?: AiFoodResult(error = "Ошибка парсинга")
    }
}