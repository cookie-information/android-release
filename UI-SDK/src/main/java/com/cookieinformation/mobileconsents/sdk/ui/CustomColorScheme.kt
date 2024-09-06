package com.cookieinformation.mobileconsents.sdk.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

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

data class CustomColorScheme(
    val primaryColorCode: Int? = null,
    val onPrimaryColorCode: Int? = null,
    val primaryContainerColorCode: Int? = null,
    val surfaceColorCode: Int? = null,
    val onSurfaceColorCode: Int? = null,
    val onSurfaceVariantColorCode: Int? = null,
    val surfaceVariantColorCode: Int? = null,
    val outlineColorCode: Int? = null,
    val outlineVariantColorCode: Int? = null
)

internal fun CustomColorScheme.toLightColorScheme(): ColorScheme {
    val lcm = lightColorScheme()

    val pc = if (primaryColorCode != null) Color(primaryColorCode) else lcm.primary
    val opc = if (onPrimaryColorCode != null) Color(onPrimaryColorCode) else lcm.onPrimary
    val pcc = if (primaryContainerColorCode != null) Color(primaryContainerColorCode) else lcm.primaryContainer
    val sc = if (surfaceColorCode != null) Color(surfaceColorCode) else lcm.surface
    val osc = if (onSurfaceColorCode != null) Color(onSurfaceColorCode) else lcm.onSurface
    val osvc = if (onSurfaceVariantColorCode != null) Color(onSurfaceVariantColorCode) else lcm.onSurfaceVariant
    val svc = if (surfaceVariantColorCode != null) Color(surfaceVariantColorCode) else lcm.surfaceVariant
    val oc = if (outlineColorCode != null) Color(outlineColorCode) else lcm.outline
    val ovc = if (outlineVariantColorCode != null) Color(outlineVariantColorCode) else lcm.outlineVariant

    return lightColorScheme(
        primary = pc,
        onPrimary = opc,
        primaryContainer = pcc,
        surface = sc,
        onSurface = osc,
        onSurfaceVariant = osvc,
        surfaceVariant = svc,
        outline = oc,
        outlineVariant = ovc
    )

}

internal fun CustomColorScheme.toDarkColorScheme(): ColorScheme {
    val dcm = darkColorScheme()

    val pc = if (primaryColorCode != null) Color(primaryColorCode) else dcm.primary
    val opc = if (onPrimaryColorCode != null) Color(onPrimaryColorCode) else dcm.onPrimary
    val pcc = if (primaryContainerColorCode != null) Color(primaryContainerColorCode) else dcm.primaryContainer
    val sc = if (surfaceColorCode != null) Color(surfaceColorCode) else dcm.surface
    val osc = if (onSurfaceColorCode != null) Color(onSurfaceColorCode) else dcm.onSurface
    val osvc = if (onSurfaceVariantColorCode != null) Color(onSurfaceVariantColorCode) else dcm.onSurfaceVariant
    val svc = if (surfaceVariantColorCode != null) Color(surfaceVariantColorCode) else dcm.surfaceVariant
    val oc = if (outlineColorCode != null) Color(outlineColorCode) else dcm.outline
    val ovc = if (outlineVariantColorCode != null) Color(outlineVariantColorCode) else dcm.outlineVariant

    return darkColorScheme(
        primary = pc,
        onPrimary = opc,
        primaryContainer = pcc,
        surface = sc,
        onSurface = osc,
        onSurfaceVariant = osvc,
        surfaceVariant = svc,
        outline = oc,
        outlineVariant = ovc
    )

}