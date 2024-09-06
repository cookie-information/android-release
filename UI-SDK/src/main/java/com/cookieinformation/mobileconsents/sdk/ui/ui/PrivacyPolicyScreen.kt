package com.cookieinformation.mobileconsents.sdk.ui.ui

import android.os.Build
import android.provider.Settings
import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookieinformation.mobileconsents.sdk.ui.R
import com.cookieinformation.mobileconsents.sdk.ui.ui.components.TextViewComposable
import com.cookieinformation.mobileconsents.sdk.ui.ui.components.WebViewComposable
import java.util.UUID

@Composable
fun PrivacyPolicyScreen(
    policy: String
) {
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = true)
        ) {
            val isUrl = URLUtil.isValidUrl(policy)
            if (isUrl) {
                WebViewComposable(policy)
            } else if (containsHtmlTags(policy)) {
                TextViewComposable(policy)
            }
            else {
                Text(
                    text = policy,
                    modifier = Modifier.clearAndSetSemantics {},
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        val copyConfirmText = stringResource(id = R.string.identifier_copied)
        val context = LocalContext.current
        val identifier =
            UUID.nameUUIDFromBytes(Settings.Secure.ANDROID_ID.encodeToByteArray())
                .toString()
        val clipboardManager: ClipboardManager = LocalClipboardManager.current
        Divider()
        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
                .clickable {
                    clipboardManager.setText(AnnotatedString((identifier)))
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                        Toast
                            .makeText(context, copyConfirmText, Toast.LENGTH_SHORT)
                            .show()
                    }
                },
            text = "${stringResource(id = R.string.device_identifier)}\n$identifier",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

