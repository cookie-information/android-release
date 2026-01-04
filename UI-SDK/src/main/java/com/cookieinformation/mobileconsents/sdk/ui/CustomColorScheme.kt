package com.cookieinformation.mobileconsents.sdk.ui

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
 * checkboxes
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
    val checkboxes: Int? = null,
    val readMore: Int? = null,
){
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
        checkboxes = checkbox?.toArgb(),
        readMore = readMore?.toArgb()
    )
}

internal fun CustomColorScheme.toLightColorScheme(): MaterialColorSchemeWithCustom {
    val lcm = lightColorScheme()

    val pc = primaryColorCode?.let { Color(it) } ?: lcm.primary
    val opc = onPrimaryColorCode?.let { Color(it) } ?: lcm.onPrimary
    val pcc = primaryContainerColorCode?.let { Color(it) } ?: lcm.primaryContainer
    val sc = surfaceColorCode?.let { Color(it) } ?: lcm.surface
    val osc = onSurfaceColorCode?.let { Color(it) } ?: lcm.onSurface
    val osvc = onSurfaceVariantColorCode?.let { Color(it) } ?: lcm.onSurfaceVariant
    val svc = surfaceVariantColorCode?.let { Color(it) } ?: lcm.surfaceVariant
    val oc = outlineColorCode?.let { Color(it) } ?: lcm.outline
    val ovc = outlineVariantColorCode?.let { Color(it) } ?: lcm.outlineVariant

    return MaterialColorSchemeWithCustom(
        materialColorScheme =
            lightColorScheme(
                primary = pc,
                onPrimary = opc,
                primaryContainer = pcc,
                surface = sc,
                onSurface = osc,
                onSurfaceVariant = osvc,
                surfaceVariant = svc,
                outline = oc,
                outlineVariant = ovc
            ),
        primaryButton = primaryButton?.let { Color(it) } ?: pc,
        secondaryButton = secondaryButton?.let { Color(it) } ?: pc,
        topBar = topBar?.let { Color(it) } ?: pc,
        divider = divider?.let { Color(it) } ?: oc,
        checkbox = checkboxes?.let { Color(it) } ?: pc,
        readMore = readMore?.let { Color(it) } ?: pc
    )


}

internal fun CustomColorScheme.toDarkColorScheme(): MaterialColorSchemeWithCustom {
    val dcm = darkColorScheme()

    val pc = primaryColorCode?.let { Color(it) } ?: dcm.primary
    val opc = onPrimaryColorCode?.let { Color(it) } ?: dcm.onPrimary
    val pcc = primaryContainerColorCode?.let { Color(it) } ?: dcm.primaryContainer
    val sc = surfaceColorCode?.let { Color(it) } ?: dcm.surface
    val osc = onSurfaceColorCode?.let { Color(it) } ?: dcm.onSurface
    val osvc = onSurfaceVariantColorCode?.let { Color(it) } ?: dcm.onSurfaceVariant
    val svc = surfaceVariantColorCode?.let { Color(it) } ?: dcm.surfaceVariant
    val oc = outlineColorCode?.let { Color(it) } ?: dcm.outline
    val ovc = outlineVariantColorCode?.let { Color(it) } ?: dcm.outlineVariant

    return MaterialColorSchemeWithCustom(
        materialColorScheme =
            darkColorScheme(
                primary = pc,
                onPrimary = opc,
                primaryContainer = pcc,
                surface = sc,
                onSurface = osc,
                onSurfaceVariant = osvc,
                surfaceVariant = svc,
                outline = oc,
                outlineVariant = ovc
            ),
        primaryButton = primaryButton?.let { Color(it) } ?: pc,
        secondaryButton = secondaryButton?.let { Color(it) } ?: pc,
        topBar = topBar?.let { Color(it) } ?: pc,
        divider = divider?.let { Color(it) } ?: oc,
        checkbox = checkboxes?.let { Color(it) } ?: pc,
        readMore = readMore?.let { Color(it) } ?: pc
    )

}