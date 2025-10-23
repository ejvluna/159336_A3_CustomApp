// ui/theme/Theme.kt

/**
 * Theme configuration for the Verifica app using Material Design 3.
 *
 * This file defines:
 * - Light and dark color schemes
 * - Custom theme composables
 * - Typography settings
 * - Shape configurations
 *
 * The theme supports dynamic color theming on Android 12+ devices while maintaining consistent branding across all API levels.
 */

package com.example.customapp.ui.theme

// Import packages for Android framework, Compose, and Material Design 3
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
import androidx.compose.ui.graphics.Color

// Defines the color scheme for dark mode
private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    secondary = Coral80,
    tertiary = BlueGrey80
)
// Defines the color scheme for light mode
private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    secondary = Coral40,
    tertiary = BlueGrey40
)

// Composable to apply the theme to the app

@Composable
fun CustomAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


// Extension to access status colors from the color scheme
val androidx.compose.material3.ColorScheme.statusTrue: Color
    get() = StatusTrue

val androidx.compose.material3.ColorScheme.statusFalse: Color
    get() = StatusFalse

val androidx.compose.material3.ColorScheme.statusMisleading: Color
    get() = StatusMisleading

val androidx.compose.material3.ColorScheme.statusUnverified: Color
    get() = StatusUnverified