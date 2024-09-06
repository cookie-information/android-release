package com.cookieinformation.mobileconsents.sdk.ui

import android.content.Context
import androidx.annotation.FontRes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

data class CustomTextStyle(
    @FontRes val fontResId: Int,
    val fontSize: Int,
)

internal fun CustomTextStyle.toTextStyle(): TextStyle {
    return TextStyle(fontFamily = fontResourceToFontFamily(), fontSize = fontSize.sp)
}

internal fun CustomTextStyle.fontResourceToFontFamily(): FontFamily {
    return FontFamily(Font(fontResId))
}