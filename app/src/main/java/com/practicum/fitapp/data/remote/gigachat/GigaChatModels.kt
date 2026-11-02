package com.practicum.fitapp.data.remote.gigachat

import com.google.gson.annotations.SerializedName

// --- Модели для получения токена ---
data class AuthRequest(
    @SerializedName("scope") val scope: String = "GIGACHAT_API_PERS"
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_at") val expiresAt: Long
)

// --- Модель для загрузки файла ---
data class FileUploadResponse(
    @SerializedName("id") val id: String
)

// --- Модели для чата (запрос) ---
data class ChatRequest(
    @SerializedName("model") val model: String = "GigaChat-2-Max", // Используем модель, которая видит картинки
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("temperature") val temperature: Double = 0.1
)

data class Message(
    @SerializedName("role") val role: String, // "user"
    @SerializedName("content") val content: String, // Текст запроса
    @SerializedName("attachments") val attachments: List<String>? = null // ID загруженного файла
)

// --- Модели для чата (ответ) ---
data class ChatResponse(
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("message") val message: MessageResponse
)

data class MessageResponse(
    @SerializedName("content") val content: String
)