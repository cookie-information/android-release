package com.cookieinformation.mobileconsents.sdk.ui.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.cookieinformation.mobileconsents.sdk.ui.CustomTypography
import com.cookieinformation.mobileconsents.sdk.ui.toTypography

internal val LocalAdditionalColors = staticCompositionLocalOf<MaterialColorSchemeWithCustom?> { null }
internal val LocalAdditionalTypography = staticCompositionLocalOf<CustomTypography?> { null }

data class MaterialColorSchemeWithCustom(
    val materialColorScheme: ColorScheme?,
    val primaryButton: Color? = null,
    val secondaryButton: Color? = null,
    val topBar: Color? = null,
    val divider: Color? = null,
    val checkbox: Color? = null,
    val readMore: Color? = null
)

object CustomUI {

    private var customLightColorScheme: MaterialColorSchemeWithCustom? = null
    private var customDarkColorScheme: MaterialColorSchemeWithCustom? = null
    private var baseTypography: Typography? = null
    private var customTypography: CustomTypography? = null


    fun setCustomUi(
        lightScheme: ColorScheme? = null,
        darkScheme: ColorScheme? = null,
        typography: Typography? = null
    ) {
        customLightColorScheme = MaterialColorSchemeWithCustom(materialColorScheme = lightScheme)
        customDarkColorScheme = MaterialColorSchemeWithCustom(materialColorScheme = darkScheme)
        baseTypography = typography
    }

    fun setCustomUi(
        lightScheme: MaterialColorSchemeWithCustom? = null,
        darkScheme: MaterialColorSchemeWithCustom? = null,
        typography: CustomTypography? = null
    ) {
        customLightColorScheme = lightScheme
        customDarkColorScheme = darkScheme
        baseTypography = typography?.toTypography()
        customTypography = typography
    }

    val lightColorScheme: ColorScheme?
        get() = customLightColorScheme?.materialColorScheme

    val darkColorScheme: ColorScheme?
        get() = customDarkColorScheme?.materialColorScheme

    val typography: Typography?
        get() = baseTypography

    val additionalTypography: CustomTypography?
        get() = customTypography

    val lightColorSchemeWithCustom: MaterialColorSchemeWithCustom?
        get() = customLightColorScheme

    val darkColorSchemeWithCustom: MaterialColorSchemeWithCustom?
        get() = customDarkColorScheme
}