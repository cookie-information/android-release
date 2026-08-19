package com.cookieinformation.mobileconsents.sdk.ui

import android.util.Log
import androidx.compose.ui.graphics.Color
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

/**
 * Colors fetched from a remote theme endpoint.
 *
 * Expected payload:
 * {
 *   "version": 2,
 *   "updatedAt": "2026-08-05T09:12:44.031Z",
 *   "buttons": {
 *     "primary":   { "backgroundColor": "#0057B8", "textColor": "#FFFFFF" },
 *     "secondary": { "backgroundColor": "#FFB300", "textColor": "#1A1A1A" }
 *   },
 *   "screen": { "background": "…", "divider": "…" },
 *   "text":   { "primary": "…", "secondary": "…", "link": "…" },
 *   "toggle": { "color": "…" },
 *   "extras": { "buttonCornerRadius": 20 }
 * }
 *
 * Only the two buttons are required; everything else is optional so an older or trimmed
 * payload still works.
 */
internal data class RemoteButtonColors(
    val background: Color,
    val text: Color,
)

internal data class RemoteTheme(
    val version: Int,
    val updatedAt: String?,
    val primaryButton: RemoteButtonColors,
    val secondaryButton: RemoteButtonColors,
    val screenBackground: Color?,
    val divider: Color?,
    val textPrimary: Color?,
    val textSecondary: Color?,
    val link: Color?,
    val toggle: Color?,
    /** Corner radius of the bottom bar buttons in dp. */
    val buttonCornerRadius: Int?,
)

internal object RemoteThemeLoader {

    private const val TAG = "ConsentsRemoteTheme"
    private const val TIMEOUT_MS = 8_000

    /**
     * Blocking — call from a background dispatcher.
     *
     * Returns null on any failure (no network, bad status, malformed payload) so the
     * integrator's own color scheme stays in effect instead of the screen breaking.
     */
    fun fetch(url: String): RemoteTheme? = runCatching {
        val json = JSONObject(download(url))
        val buttons = json.getJSONObject("buttons")
        val screen = json.optJSONObject("screen")
        val text = json.optJSONObject("text")
        val toggle = json.optJSONObject("toggle")
        val extras = json.optJSONObject("extras")

        RemoteTheme(
            version = json.optInt("version", 1),
            updatedAt = json.optString("updatedAt").ifBlank { null },
            primaryButton = buttons.getJSONObject("primary").toButtonColors(),
            secondaryButton = buttons.getJSONObject("secondary").toButtonColors(),
            screenBackground = screen.optColor("background"),
            divider = screen.optColor("divider"),
            textPrimary = text.optColor("primary"),
            textSecondary = text.optColor("secondary"),
            link = text.optColor("link"),
            toggle = toggle.optColor("color"),
            buttonCornerRadius = extras?.optInt("buttonCornerRadius", -1)?.takeIf { it >= 0 },
        )
    }.onFailure {
        Log.w(TAG, "Could not load theme from $url, keeping the current color scheme", it)
    }.getOrNull()

    private fun download(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            setRequestProperty("Cache-Control", "no-cache")
            setRequestProperty("Accept", "application/json")
        }

        return try {
            val status = connection.responseCode
            check(status == HttpURLConnection.HTTP_OK) { "endpoint returned HTTP $status" }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun JSONObject.toButtonColors() = RemoteButtonColors(
        background = getString("backgroundColor").toColor(),
        text = getString("textColor").toColor(),
    )

    /** Null when the group or the key is missing, or when the value is not a "#RRGGBB" string. */
    private fun JSONObject?.optColor(key: String): Color? {
        val value = this?.optString(key)?.takeIf { it.isNotBlank() } ?: return null
        return runCatching { value.toColor() }.getOrNull()
    }

    /** Parses "#RRGGBB". Throws on anything else. */
    private fun String.toColor() = Color(android.graphics.Color.parseColor(this))
}
