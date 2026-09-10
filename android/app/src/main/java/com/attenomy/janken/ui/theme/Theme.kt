package com.attenomy.janken.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigoLight,
    onPrimary = DarkBackground,
    primaryContainer = PrimaryIndigoDark,
    onPrimaryContainer = DarkOnBackground,
    secondary = AccentCyan,
    onSecondary = DarkBackground,
    tertiary = AccentEmerald,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface,
    outline = DarkOutline,
    error = AccentRose
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = LightSurface,
    primaryContainer = PrimaryIndigoLight,
    onPrimaryContainer = LightOnBackground,
    secondary = AccentCyan,
    onSecondary = LightSurface,
    tertiary = AccentEmerald,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
    outline = LightOutline,
    error = AccentRose
)

@Composable
fun JankenTheme(
    themePreference: String = "SYSTEM",
    dynamicColor: Boolean = false, // Set false by default so custom curated dark grey theme is always shown!
    content: @Composable () -> Unit
) {
    val darkTheme = when (themePreference) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

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
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                // isAppearanceLightStatusBars = false makes status bar icons/text WHITE
                // isAppearanceLightStatusBars = true makes status bar icons/text BLACK/DARK
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
