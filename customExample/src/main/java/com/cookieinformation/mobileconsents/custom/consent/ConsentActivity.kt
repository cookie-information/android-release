package com.cookieinformation.mobileconsents.custom.consent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/** Hosts the consent screen. Launched through [CookieConsent.show] / [CookieConsent.showIfNeeded]. */
internal class ConsentActivity : ComponentActivity() {

    private val viewModel: ConsentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = intent.getStringExtra(EXTRA_USER_ID)
        val cookiePolicyUrl = intent.getStringExtra(EXTRA_COOKIE_POLICY_URL)
        val forcedDark = if (intent.hasExtra(EXTRA_DARK_THEME)) intent.getBooleanExtra(EXTRA_DARK_THEME, false) else null

        setContent {
            val state by viewModel.state.collectAsState()
            val darkTheme = forcedDark ?: isSystemInDarkTheme()
            var policyContent by rememberSaveable { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) { viewModel.load(userId) }
            LaunchedEffect(state.finished) { if (state.finished) finish() }

            MaterialTheme {
                val content = policyContent
                if (content != null) {
                    BackHandler { policyContent = null }
                    PolicyWebView(content = content)
                } else {
                    ConsentScreen(
                        isLoading = state.isLoading,
                        items = state.items,
                        onSave = { viewModel.save(userId, it) },
                        onOpenPolicy = { policyContent = it },
                        privacyPolicy = state.privacyPolicy,
                        cookiePolicyUrl = cookiePolicyUrl,
                        darkTheme = darkTheme,
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_USER_ID = "cookie_consent_user_id"
        const val EXTRA_COOKIE_POLICY_URL = "cookie_consent_cookie_policy_url"
        const val EXTRA_DARK_THEME = "cookie_consent_dark_theme"
    }
}
