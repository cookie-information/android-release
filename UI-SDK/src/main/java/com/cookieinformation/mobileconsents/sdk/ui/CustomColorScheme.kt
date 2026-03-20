package com.cookieinformation.mobileconsents.sdk.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
 *
//// Additional  ////
 * primaryButton
 * secondaryButton
 * topBar
 * divider
 * checkbox
 * readMore
 */

data class CustomColorScheme(
    val primaryColorCode: Int? = null,
    val onPrimaryColorCode: Int? = null,
    val primaryContainerColorCode: Int? = null,
    val surfaceColorCode: Int? = null,
    val onSurfaceColorCode: Int? = null,
    val onSurfaceVariantColorCode: Int? = null,
    val surfaceVariantColorCode: Int? = null,
    val outlineColorCode: Int? = null,
    val outlineVariantColorCode: Int? = null,
    val primaryButton: Int? = null,
    val secondaryButton: Int? = null,
    val topBar: Int? = null,
    val divider: Int? = null,
    val checkbox: Int? = null,
    val readMore: Int? = null,
) {
    constructor(
        primaryColor: Color? = null,
        onPrimaryColor: Color? = null,
        primaryContainerColor: Color? = null,
        surfaceColor: Color? = null,
        onSurfaceColor: Color? = null,
        onSurfaceVariantColor: Color? = null,
        surfaceVariantColor: Color? = null,
        outlineColor: Color? = null,
        outlineVariantColor: Color? = null,
        primaryButton: Color? = null,
        secondaryButton: Color? = null,
        topBar: Color? = null,
        divider: Color? = null,
        checkbox: Color? = null,
        readMore: Color? = null
    ) : this(
        primaryColorCode = primaryColor?.toArgb(),
        onPrimaryColorCode = onPrimaryColor?.toArgb(),
        primaryContainerColorCode = primaryContainerColor?.toArgb(),
        surfaceColorCode = surfaceColor?.toArgb(),
        onSurfaceColorCode = onSurfaceColor?.toArgb(),
        onSurfaceVariantColorCode = onSurfaceVariantColor?.toArgb(),
        surfaceVariantColorCode = surfaceVariantColor?.toArgb(),
        outlineColorCode = outlineColor?.toArgb(),
        outlineVariantColorCode = outlineVariantColor?.toArgb(),
        primaryButton = primaryButton?.toArgb(),
        secondaryButton = secondaryButton?.toArgb(),
        topBar = topBar?.toArgb(),
        divider = divider?.toArgb(),
        checkbox = checkbox?.toArgb(),
        readMore = readMore?.toArgb()
    )
}

private fun CustomColorScheme.toMaterialColorScheme(baseScheme: ColorScheme): MaterialColorSchemeWithCustom {
    val primary = primaryColorCode.toColorOrDefault(baseScheme.primary)
    val onPrimary = onPrimaryColorCode.toColorOrDefault(baseScheme.onPrimary)
    val primaryContainer = primaryContainerColorCode.toColorOrDefault(baseScheme.primaryContainer)
    val surface = surfaceColorCode.toColorOrDefault(baseScheme.surface)
    val onSurface = onSurfaceColorCode.toColorOrDefault(baseScheme.onSurface)
    val onSurfaceVariant = onSurfaceVariantColorCode.toColorOrDefault(baseScheme.onSurfaceVariant)
    val surfaceVariant = surfaceVariantColorCode.toColorOrDefault(baseScheme.surfaceVariant)
    val outline = outlineColorCode.toColorOrDefault(baseScheme.outline)
    val outlineVariant = outlineVariantColorCode.toColorOrDefault(baseScheme.outlineVariant)

    return MaterialColorSchemeWithCustom(
        materialColorScheme = baseScheme.copy(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            surface = surface,
            onSurface = onSurface,
            onSurfaceVariant = onSurfaceVariant,
            surfaceVariant = surfaceVariant,
            outline = outline,
            outlineVariant = outlineVariant
        ),
        primaryButton = primaryButton.toColorOrDefault(primary),
        secondaryButton = secondaryButton.toColorOrDefault(primary),
        topBar = topBar.toColorOrDefault(primary),
        divider = divider.toColorOrDefault(outline),
        checkbox = checkbox.toColorOrDefault(primary),
        readMore = readMore.toColorOrDefault(primary)
    )
}

internal fun CustomColorScheme.toLightColorScheme() = toMaterialColorScheme(lightColorScheme())
internal fun CustomColorScheme.toDarkColorScheme() = toMaterialColorScheme(darkColorScheme())
internal fun Int?.toColorOrDefault(default: Color) = this?.let { Color(it) } ?: default

