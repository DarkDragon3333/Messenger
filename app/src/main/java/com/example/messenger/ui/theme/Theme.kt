package com.example.messenger.ui.theme

import android.app.Activity
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

private val LightColorScheme = lightColorScheme(
    surface = unfocusedContainerColorLightMode,
    background = unfocusedContainerColorLightMode,
    surfaceVariant = unfocusedContainerColorLightMode,
    inverseSurface = unfocusedContainerColorLightMode,
    inversePrimary = unfocusedContainerColorLightMode,
    surfaceTint = unfocusedContainerColorLightMode,
    scrim = unfocusedContainerColorLightMode,

    primaryContainer = unfocusedContainerColorLightMode,
    tertiary = unfocusedTextColorLightMode,

    secondaryContainer = focusedContainerColorLightMode,
    onTertiary = focusedTextColorLightMode,

    onSecondaryContainer = disabledContainerColorLightMode,
    tertiaryContainer = disabledTextColorLightMode,
    outline = disabledLabelColorLightMode,
    outlineVariant = disabledIndicatorColorLightMode,
)


private val DarkColorScheme = darkColorScheme(
    primaryContainer = unfocusedContainerColorDarkMode,
    tertiary = unfocusedTextColorDarkMode,

    secondaryContainer = focusedContainerColorDarkMode,
    onTertiary = focusedTextColorDarkMode,

    onSecondaryContainer = disabledContainerColorDarkMode,
    tertiaryContainer = disabledTextColorDarkMode,
    outline = disabledLabelColorDarkMode,
    outlineVariant = disabledIndicatorColorDarkMode,

    //DarkMode
)

@Composable
fun MessengerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}