package com.cookieinformation.mobileconsents.sdk.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle


/**
 * Customizable typography for the consent screen.
 * All fields are optional — only override what you need, the rest stays default.
 *
 * Material3 styles:
 * - headlineSmall  — Privacy Policy headline
 * - titleMedium    — Privacy Policy short, section headers, consent item titles
 * - bodyMedium     — Consent item body, Privacy Policy body (when no HTML)
 * - labelLarge     — "Read more", bottom bar button texts
 * - labelSmall     — "Powered by Cookie Information", device identifier
 *
 * Per-component styles:
 * - readMore             — overrides "Read more" text style (replaces labelLarge)
 * - requiredTitle        — overrides required consent item title (replaces titleMedium)
 * - requiredSectionTitle — overrides required section header (replaces titleMedium)
 * - requiredSectionBody  — overrides required section body (replaces bodyMedium)
 * - optionalTitle        — overrides optional consent item title (replaces titleMedium)
 * - optionalSectionTitle — overrides optional section header (replaces titleMedium)
 * - optionalSectionBody  — overrides optional section body (replaces bodyMedium)
 */

data class CustomTypography(
    val headlineSmall: CustomTextStyle? = null,
    val titleMedium: CustomTextStyle? = null,
    val bodyMedium: CustomTextStyle? = null,
    val labelLarge: CustomTextStyle? = null,
    val labelSmall: CustomTextStyle? = null,
    val readMore: CustomTextStyle? = null,
    val requiredTitle: CustomTextStyle? = null,
    val requiredSectionTitle: CustomTextStyle? = null,
    val requiredSectionBody: CustomTextStyle? = null,
    val optionalTitle: CustomTextStyle? = null,
    val optionalSectionTitle: CustomTextStyle? = null,
    val optionalSectionBody: CustomTextStyle? = null
) {

    fun itemTitleStyle(isRequired: Boolean): TextStyle? =
        if (isRequired) requiredTitle?.toTextStyle()
        else optionalTitle?.toTextStyle()

    fun sectionHeaderStyle(isRequired: Boolean): TextStyle? =
        if (isRequired) requiredSectionTitle?.toTextStyle()
        else optionalSectionTitle?.toTextStyle()

    fun sectionBodyStyle(isRequired: Boolean): TextStyle? =
        if (isRequired) requiredSectionBody?.toTextStyle()
        else optionalSectionBody?.toTextStyle()

    fun readMoreStyle(): TextStyle? = readMore?.toTextStyle()
}

internal fun CustomTypography.toTypography(): Typography {
    val defaultTypography = Typography()

    val hs = headlineSmall?.toTextStyle() ?: defaultTypography.headlineSmall
    val tm = titleMedium?.toTextStyle() ?: defaultTypography.titleMedium
    val bm = bodyMedium?.toTextStyle() ?: defaultTypography.bodyMedium
    val ll = labelLarge?.toTextStyle() ?: defaultTypography.labelLarge
    val ls = labelSmall?.toTextStyle() ?: defaultTypography.labelSmall

    return Typography(
        headlineSmall = hs,
        titleMedium = tm,
        bodyMedium = bm,
        labelLarge = ll,
        labelSmall = ls
    )
}
