package com.example.sample

import android.app.Application
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.cookieinformation.mobileconsents.Consentable
import com.cookieinformation.mobileconsents.MobileConsentSdk
import com.cookieinformation.mobileconsents.MobileConsents
import com.cookieinformation.mobileconsents.models.BodyStyle
import com.cookieinformation.mobileconsents.models.MobileConsentCredentials
import com.cookieinformation.mobileconsents.models.MobileConsentCustomUI
import com.cookieinformation.mobileconsents.models.SdkTextStyle
import com.cookieinformation.mobileconsents.models.SubtitleStyle
import com.cookieinformation.mobileconsents.models.TitleStyle
import java.util.Locale

class App : Application(), Consentable {

  override val sdk: MobileConsents by lazy { MobileConsents(provideConsentSdk()) }

  override fun onCreate() {
    super.onCreate()
    sdk.initSDKWithCompletion { success, exception ->

    }
  }

  override fun provideConsentSdk() = MobileConsentSdk.Builder(this)
    .setClientCredentials(provideCredentials())
    .setMobileConsentCustomUI(
      MobileConsentCustomUI(
        Color.parseColor("#ff0000"),
        SdkTextStyle(
          TitleStyle(ResourcesCompat.getFont(applicationContext, R.font.coiny)),
          SubtitleStyle(ResourcesCompat.getFont(applicationContext, R.font.shantellsans_light)),
          BodyStyle(Typeface.MONOSPACE),
        )
      )
    )
//    .setCustomConsentView(CustomConsentView(applicationContext))
    .setLanguages(listOf(Locale.FRANCE, Locale("da")))
    .build()

  override fun provideCredentials(): MobileConsentCredentials {
    return MobileConsentCredentials(
      clientId = "40dbe5a7-1c01-463a-bb08-a76970c0efa0",
      solutionId = "75713910-47e1-4568-b6d6-f6338c63d18b",
      clientSecret = "68cbf024407a20b8df4aecc3d9937f43c6e83169dafcb38b8d18296b515cc0d5f8bca8165d615caa4d12e236192851e9c5852a07319428562af8f920293bc1db"
    )
  }
}