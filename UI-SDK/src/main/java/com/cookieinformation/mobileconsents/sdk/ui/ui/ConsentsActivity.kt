package com.cookieinformation.mobileconsents.sdk.ui.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.cookieinformation.mobileconsents.sdk.ui.ConsentsUISDK
import com.cookieinformation.mobileconsents.sdk.ui.ui.theme.AndroidUiSDKTheme
import java.util.Locale

class ConsentsActivity : ComponentActivity() {
    private val userId: String? by lazy { intent.getStringExtra("userId") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AndroidUiSDKTheme(
                customerLightColorScheme = CustomUI.lightColorScheme,
                customerDarkColorScheme = CustomUI.darkColorScheme,
                typography = CustomUI.typography
            ) {
                ConsentsWrappingScreen(
                    viewModel = ConsentsViewModel(application),
                    userId = userId
                )
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (ConsentsUISDK.isLocaleOverriden) {
            newBase?.let {
                val newLocale = Locale(ConsentsUISDK.languageCode)
                val context = changeLocale(it, newLocale)
                super.attachBaseContext(context)
            } ?: super.attachBaseContext(newBase)
        }
        else {
            super.attachBaseContext(newBase)
        }
    }

    private fun changeLocale(context: Context, newLocale: Locale): Context {
        val res: Resources = context.resources
        val configuration = Configuration(res.configuration)

        configuration.setLocale(newLocale)
        val localeList = LocaleList(newLocale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
        return context.createConfigurationContext(configuration)
    }

}
