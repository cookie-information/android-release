package com.cookieinformation.mobileconsents.sdk.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle


/**
 * Customizable typography
 *
 * Core Material3 mappings:
 * headlineSmall       - Privacy Policy headline
 * titleMedium         - Privacy Policy short, header section text, consent item title
 * bodyMedium          - Consent item body (when no HTML), Privacy Policy body (when no HTML)
 * labelLarge          - "Read more", Bottom bar button texts
 * labelSmall          - "Powered by Cookie Information", Device identifier
 *
 * Additional custom fields:
 *
 * readMore
 *     - Optional override for "Read more" button/label text style.
 *     - If provided, it replaces `bodyMedium` for "Read more" text.
 *
 * requiredTitle
 *     - Optional override for the "Required" .
 *     - If provided, it **replaces `titleMedium`**.
 *
 * requiredSectionTitle
 *     - Optional override for the header of the required consents section.
 *     - If provided, it **replaces `titleMedium`** in SectionHeader for required section.
 *
 * requiredSectionBody
 *     - Optional override for the body text of the required consents section.
 *     - If provided, it **replaces `bodyMedium`** for the section body.
 *
 * optionalTitle
 *     - Optional override for the "Optional".
 *     - If provided, it **replaces `titleMedium`** .
 *
 * optionalSectionTitle
 *     - Optional override for the header of the optional consents section.
 *     - If provided, it **replaces `titleMedium`** in SectionHeader for optional section.
 *
 * optionalSectionBody
 *     - Optional override for the body text of the optional consents section.
 *     - If provided, it **replaces `bodyMedium`** for the section body.
 */

data class CustomTypography(
    val headlineSmall: CustomTextStyle? = null,
    val titleMedium: CustomTextStyle? = null,
    val bodyMedium: CustomTextStyle? = null,
    val labelLarge: CustomTextStyle? = null,
    val labelSmall: CustomTextStyle? = null,
    //additional
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
