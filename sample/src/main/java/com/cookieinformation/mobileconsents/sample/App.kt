package com.cookieinformation.mobileconsents.sample

import android.app.Application
import com.cookieinformation.mobileconsents.CallbackMobileConsentSdk
import com.cookieinformation.mobileconsents.MobileConsentSdk

class App : Application() {

  internal lateinit var sdk: CallbackMobileConsentSdk

  override fun onCreate() {
    super.onCreate()
    sdk = CallbackMobileConsentSdk.from(
      MobileConsentSdk.Builder(this)
        .partnerUrl(getString(R.string.sample_partner_url))
        .callFactory(getOkHttpClient(this))
        .build()
    )
  }
}
