package com.cookieinformation.mobileconsents.sdk.ui.ui.theme

import android.app.Activity

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Customizable colors (based as much as possible on component colors from https://m3.material.io/components)
 *
 * primary
 * onPrimary
 * surface
 * onSurface
 * onSurfaceVariant
 * surfaceVariant
 * outline
 * outlineVariant
 * primaryContainer
 */


/**
 * Customizable typography
 *
 * headlineSmall (Privacy policy headline)
 * titleMedium (Privacy policy short, header section text, consent item tile)
 * bodyMedium (consent item body (when no html), privacy policy body (when no html))
 * labelLarge - Read more, Bottom bar button texts
 * labelSmall - Powered by Cookie Information, Device identifier
 *
 */

@Composable
fun AndroidUiSDKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    customerLightColorScheme: ColorScheme? = null,
    customerDarkColorScheme: ColorScheme? = null,
    typography: Typography? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> customerDarkColorScheme ?: darkColorScheme()
        else -> customerLightColorScheme ?: lightColorScheme()
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
        typography = typography ?: Typography(),
        content = content
    )
}

