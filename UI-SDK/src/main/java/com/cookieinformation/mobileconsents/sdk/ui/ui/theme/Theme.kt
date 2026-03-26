package com.cookieinformation.mobileconsents.sdk.ui.ui.theme

import android.app.Activity

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.cookieinformation.mobileconsents.sdk.ui.CustomTypography
import com.cookieinformation.mobileconsents.sdk.ui.ui.LocalAdditionalColors
import com.cookieinformation.mobileconsents.sdk.ui.ui.LocalAdditionalTypography
import com.cookieinformation.mobileconsents.sdk.ui.ui.MaterialColorSchemeWithCustom

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
 * additionalLightColors - wrapper for ColorScheme with additional colors
 * additionalDarkColors- wrapper for ColorScheme with additional colors
 */

@Composable
fun AndroidUiSDKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    customerLightColorScheme: ColorScheme? = null,
    customerDarkColorScheme: ColorScheme? = null,
    additionalLightColors: MaterialColorSchemeWithCustom? = null,
    additionalDarkColors: MaterialColorSchemeWithCustom? = null,
    additionalTypography: CustomTypography? = null,
    typography: Typography? = null,
    content: @Composable () -> Unit
) {

    val colorScheme = remember(darkTheme, customerLightColorScheme, customerDarkColorScheme, additionalLightColors, additionalDarkColors) {
        when {
            darkTheme -> customerDarkColorScheme ?: additionalDarkColors?.materialColorScheme ?: darkColorScheme()
            else -> customerLightColorScheme ?: additionalLightColors?.materialColorScheme ?: lightColorScheme()
        }
    }

    val additionalColors = remember(darkTheme, additionalLightColors, additionalDarkColors) {
        if (darkTheme) additionalDarkColors else additionalLightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = additionalColors?.topBar?.toArgb() ?: colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalAdditionalColors provides additionalColors,
        LocalAdditionalTypography provides additionalTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography ?: Typography(),
            content = content
        )
    }
}

