package com.cookieinformation.mobileconsents.custom.consent

import androidx.compose.ui.graphics.Color

// Brand color
val BrandBlue = Color(0xFF093A5C)

// Light palette
private val LightBackground = Color.White
private val LightDescription = Color(0xFF444444)
private val LightTrackOff = Color(0xFFDCE1E6)
private val LightDivider = Color(0xFFEDEDED)

// Dark palette
private val DarkBackground = Color(0xFF303336)
private val DarkTitle = Color.White
private val DarkDescription = Color(0xFFC7C7C7)
private val DarkTrackOff = Color(0xFF5C6166)
private val DarkDivider = Color(0xFF4A4E52)

/** Every color the consent screen uses, for a single theme mode. */
class ConsentColors(
    val background: Color,
    val title: Color,
    val description: Color,
    val accent: Color,      // toggle-on track + primary "Save choices" button
    val onAccent: Color,    // text on the accent
    val secondary: Color,   // outlined "Only Necessary" button
    val trackOff: Color,    // toggle-off track
    val divider: Color,
)

/** Light and dark palettes. Dark mode uses grey tones; the accent stays brand blue. */
fun consentColors(darkTheme: Boolean): ConsentColors = if (darkTheme) {
    ConsentColors(
        background = DarkBackground,
        title = DarkTitle,
        description = DarkDescription,
        accent = BrandBlue,
        onAccent = Color.White,
        secondary = Color.White,
        trackOff = DarkTrackOff,
        divider = DarkDivider,
    )
} else {
    ConsentColors(
        background = LightBackground,
        title = BrandBlue,
        description = LightDescription,
        accent = BrandBlue,
        onAccent = Color.White,
        secondary = BrandBlue,
        trackOff = LightTrackOff,
        divider = LightDivider,
    )
}
