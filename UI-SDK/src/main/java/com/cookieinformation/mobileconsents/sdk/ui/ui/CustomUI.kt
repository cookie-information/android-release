package com.cookieinformation.mobileconsents.sdk.ui.ui

import androidx.compose.material3.ColorScheme

object CustomUI {

    private var customLightColorScheme: ColorScheme? = null
    private var customDarkColorScheme: ColorScheme? = null
    private var customTypography: androidx.compose.material3.Typography? = null

    fun setCustomUi(
        lightScheme: ColorScheme? = null,
        darkScheme: ColorScheme? = null,
        typography: androidx.compose.material3.Typography? = null
    ) {
        customLightColorScheme = lightScheme
        customDarkColorScheme = darkScheme
        customTypography = typography
    }

    val lightColorScheme: ColorScheme?
        get() = customLightColorScheme


    val darkColorScheme: ColorScheme?
        get() = customDarkColorScheme

    val typography: androidx.compose.material3.Typography?
        get() = customTypography
}