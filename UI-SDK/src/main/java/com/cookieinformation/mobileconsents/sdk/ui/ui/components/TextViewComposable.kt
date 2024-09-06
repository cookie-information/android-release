package com.cookieinformation.mobileconsents.sdk.ui.ui.components

import android.content.ActivityNotFoundException
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.cookieinformation.mobileconsents.sdk.ui.R

@Composable
fun TextViewComposable(description: String) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    AndroidView(
        factory = { context ->
            TextView(context).also {
                it.setTextColor(color)
                it.importantForAccessibility =
                    IMPORTANT_FOR_ACCESSIBILITY_NO//always flag as not important and handle accessibility from outside.
                it.setTextFromHtml(description, boldLinks = true, underline = true)
            }
        }
    )
}

fun TextView.setTextFromHtml(html: String, boldLinks: Boolean = true, underline: Boolean = false) {
    val stringBuilder =
        SpannableStringBuilder(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)).apply {
            changeLinksStyle(underline = underline, bold = boldLinks) {
                setTag(R.id.span_clicked_tag, true)
            }
        }
    linksClickable = true
    text = stringBuilder
    movementMethod = LinkMovementMethod.getInstance()
}


internal fun Spannable.changeLinksStyle(
    underline: Boolean = false,
    bold: Boolean = true,
    doOnClick: (() -> Unit)? = null
): Boolean {
    val spans = getSpans(0, length, URLSpan::class.java)
    spans.forEach {
        setSpan(
            object : URLSpan(it.url) {

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = underline
                    ds.isFakeBoldText = bold
                }

                override fun onClick(widget: View) {
                    try {
                        super.onClick(widget)
                    } catch (_: ActivityNotFoundException) {
                        // Ignore
                    }
                    doOnClick?.invoke()
                }
            },
            getSpanStart(it),
            getSpanEnd(it),
            0
        )
        removeSpan(it)
    }
    return spans.isNotEmpty()
}
