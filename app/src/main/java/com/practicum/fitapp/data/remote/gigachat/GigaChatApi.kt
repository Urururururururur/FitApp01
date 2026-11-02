package com.practicum.fitapp.data.remote.gigachat

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GigaChatApi {
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") authorizationKey: String,
        @Header("RqUID") rquid: String,
        @Field("scope") scope: String = "GIGACHAT_API_PERS",
        @Field("grant_type") grantType: String = "client_credentials"
    ): AuthResponse

    // Загрузка файла (ОФИЦИАЛЬНЫЙ АДРЕС)
    @Multipart
    @POST("https://api.giga.chat/v1/files")
    suspend fun uploadFile(
        @Header("Authorization") bearerToken: String,
        @Part file: MultipartBody.Part,
        @Part("purpose") purpose: RequestBody
    ): FileUploadResponse

    // Запрос к чату (ОФИЦИАЛЬНЫЙ АДРЕС)
    @POST("https://api.giga.chat/v1/chat/completions")
    suspend fun sendChatMessage(
        @Header("Authorization") bearerToken: String,
        @Body request: ChatRequest
    ): ChatResponse
}