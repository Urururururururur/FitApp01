package com.practicum.fitapp.data.di

import android.content.Context
import com.practicum.fitapp.data.HealthConnectManager
import com.practicum.fitapp.data.remote.gigachat.GigaChatApi
import com.practicum.fitapp.data.remote.gigachat.GigaChatClient
import com.practicum.fitapp.data.repository.AiCalorieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule { // ← Открывающая скобка объекта

    @Provides
    @Singleton
    fun provideHealthConnectManager(@ApplicationContext context: Context): HealthConnectManager {
        return HealthConnectManager(context)
    }

    // 👇 ЭТА ФУНКЦИЯ ДОЛЖНА БЫТЬ ЗДЕСЬ (ВНУТРИ object AppModule) 👇
    @Provides
    @Singleton
    fun provideGigaChatApi(@ApplicationContext context: Context): GigaChatApi {
        return GigaChatClient.createApi(context)
    }

    @Provides
    @Singleton
    fun provideAiCalorieRepository(
        api: GigaChatApi,
        @ApplicationContext context: Context
    ): AiCalorieRepository {
        return AiCalorieRepository(api, context)
    }

} // ← Закрывающая скобка объекта