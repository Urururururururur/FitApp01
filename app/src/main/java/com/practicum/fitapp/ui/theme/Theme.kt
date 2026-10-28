package com.practicum.fitapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// СВЕТЛАЯ ТЕМА (ЗЕЛЕНАЯ)
private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = GreenContainerLight,
    onPrimaryContainer = OnGreenContainerLight,

    secondary = Blue40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = BlueContainerLight,
    onSecondaryContainer = OnBlueContainerLight,

    tertiary = Orange40,

    background = LightBackground,
    onBackground = androidx.compose.ui.graphics.Color.Black,

    surface = LightSurface,
    onSurface = androidx.compose.ui.graphics.Color.Black,

    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
)

// ТЕМНАЯ ТЕМА (ЗЕЛЕНАЯ)
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    secondary = DarkPrimary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DarkPrimaryContainer,
    onSecondaryContainer = DarkOnPrimaryContainer,

    tertiary = DarkPrimary,
    onTertiary = DarkOnPrimary,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
)

@Composable
fun FitAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Делаем системные панели полностью прозрачными
            window.statusBarColor = androidx.compose.ui.graphics.Color.Transparent.toArgb()
            window.navigationBarColor = androidx.compose.ui.graphics.Color.Transparent.toArgb()

            // Настраиваем цвет иконок (темные на светлой теме, светлые на темной)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}