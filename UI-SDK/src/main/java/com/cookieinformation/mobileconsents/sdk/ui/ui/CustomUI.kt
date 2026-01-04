package com.cookieinformation.mobileconsents.sdk.ui.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color


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
    private var customTypography: Typography? = null


    fun setCustomUi(
        lightScheme: ColorScheme? = null,
        darkScheme: ColorScheme? = null,
        typography: Typography? = null
    ) {
        customLightColorScheme = MaterialColorSchemeWithCustom(materialColorScheme = lightScheme)
        customDarkColorScheme = MaterialColorSchemeWithCustom(materialColorScheme = darkScheme)
        customTypography = typography
    }

    fun setCustomUi(
        lightScheme: MaterialColorSchemeWithCustom? = null,
        darkScheme: MaterialColorSchemeWithCustom? = null,
        typography: Typography? = null
    ) {
        customLightColorScheme = lightScheme
        customDarkColorScheme = darkScheme
        customTypography = typography
    }

    val lightColorScheme: ColorScheme?
        get() = customLightColorScheme?.materialColorScheme

    val darkColorScheme: ColorScheme?
        get() = customDarkColorScheme?.materialColorScheme

    val typography: Typography?
        get() = customTypography

    val lightColorSchemeWithCustom: MaterialColorSchemeWithCustom?
        get() = customLightColorScheme

    val darkColorSchemeWithCustom: MaterialColorSchemeWithCustom?
        get() = customDarkColorScheme
}