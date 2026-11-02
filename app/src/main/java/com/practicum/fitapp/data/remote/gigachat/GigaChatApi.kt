package com.practicum.fitapp.data.remote.gigachat

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GigaChatApi {
    // 1. Получение токена (мы пока просто захардкодим тестовый токен, но архитектуру заложим)
    @POST("api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") basicAuth: String,
        @Body request: AuthRequest
    ): AuthResponse

    // 2. Отправка запроса к нейросети (текст или фото)
    @POST("api/v1/chat/completions")
    suspend fun sendChatMessage(
        @Header("Authorization") bearerToken: String, // Сюда мы передадим "Bearer <Твой токен>"
        @Body request: ChatRequest
    ): ChatResponse
}