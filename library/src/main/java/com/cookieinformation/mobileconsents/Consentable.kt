package com.cookieinformation.mobileconsents

import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.models.MobileConsentCredentials

public interface Consentable {
  public val sdk: MobileConsents
  public fun provideConsentSdk(): MobileConsentSdk
  public fun provideCredentials(): MobileConsentCredentials
  public suspend fun getSavedConsents(): Map<Type, Boolean> {
    return sdk.getMobileConsentSdk().getSavedConsents()
  }
}