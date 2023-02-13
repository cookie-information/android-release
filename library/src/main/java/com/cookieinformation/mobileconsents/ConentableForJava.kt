package com.cookieinformation.mobileconsents

import com.cookieinformation.mobileconsents.models.MobileConsentCredentials

public interface ConentableForJava {
  public val sdk: MobileConsents
  public fun provideConsentSdk(): MobileConsentSdk
  public fun provideCredentials(): MobileConsentCredentials
}