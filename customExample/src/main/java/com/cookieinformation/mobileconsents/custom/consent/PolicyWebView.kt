package com.cookieinformation.mobileconsents.custom.consent

import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

/**
 * In-app WebView for a policy, shown as a second screen within the consent flow — the same approach
 * the built-in UI uses (works without a browser app installed). [content] may be a URL or raw HTML.
 */
@Composable
fun PolicyWebView(content: String) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                if (URLUtil.isValidUrl(content)) {
                    loadUrl(content)
                } else {
                    loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
                }
            }
        },
    )
}
