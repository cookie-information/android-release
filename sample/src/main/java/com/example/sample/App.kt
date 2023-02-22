package com.example.sample

import android.app.Application
import android.graphics.Color
import com.cookieinformation.mobileconsents.Consentable
import com.cookieinformation.mobileconsents.MobileConsents
import com.cookieinformation.mobileconsents.MobileConsentSdk
import com.cookieinformation.mobileconsents.models.MobileConsentCredentials
import com.cookieinformation.mobileconsents.models.MobileConsentCustomUI
import java.util.Locale

class App : Application(), Consentable {

  override val sdk: MobileConsents by lazy { MobileConsents(provideConsentSdk()) }


  override fun provideConsentSdk() = MobileConsentSdk.Builder(this)
    .setClientCredentials(provideCredentials())
    .setMobileConsentCustomUI(MobileConsentCustomUI(Color.parseColor("#ff0000")))
    .setLanguages(listOf(Locale.FRANCE, Locale("da")))
    .build()

  override fun provideCredentials(): MobileConsentCredentials {
    return MobileConsentCredentials(
      clientId = "40dbe5a7-1c01-463a-bb08-a76970c0efa0",
      solutionId = "4113ab88-4980-4429-b2d1-3454cc81197b",
      clientSecret = "68cbf024407a20b8df4aecc3d9937f43c6e83169dafcb38b8d18296b515cc0d5f8bca8165d615caa4d12e236192851e9c5852a07319428562af8f920293bc1db"
    )
  }
}