package com.practicum.fitapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Точка входа приложения. Создаётся один раз при запуске.
 * @HiltAndroidApp — говорит Hilt: «здесь начинается контейнер зависимостей».
 */
@HiltAndroidApp
class FitAppApplication : Application()