package com.cookieinformation.mobileconsents.sdk.ui

import android.content.Context
import androidx.annotation.FontRes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

/**
 * Customizable typography
 *
 * headlineSmall (Privacy policy headline)
 * titleMedium (Privacy policy short, header section text, consent item tile)
 * bodyMedium (consent item body (when no html), privacy policy body (when no html))
 * labelLarge - Read more, Bottom bar button texts
 * labelSmall - Powered by Cookie Information, Device identifier
 */

data class CustomTypography(
    val headlineSmall: CustomTextStyle? = null,
    val titleMedium: CustomTextStyle? = null,
    val bodyMedium: CustomTextStyle? = null,
    val labelLarge: CustomTextStyle? = null,
    val labelSmall: CustomTextStyle? = null
)

internal fun CustomTypography.toTypography(): Typography {
    val defaultTypography = Typography()

    val hs = headlineSmall?.toTextStyle() ?: defaultTypography.headlineSmall
    val tm = titleMedium?.toTextStyle() ?: defaultTypography.titleMedium
    val bm = bodyMedium?.toTextStyle() ?: defaultTypography.bodyMedium
    val ll = labelLarge?.toTextStyle() ?: defaultTypography.labelLarge
    val ls = labelSmall?.toTextStyle() ?: defaultTypography.labelSmall

    return Typography(
        headlineSmall = hs,
        titleMedium =  tm,
        bodyMedium = bm,
        labelLarge = ll,
        labelSmall = ls
    )
}
