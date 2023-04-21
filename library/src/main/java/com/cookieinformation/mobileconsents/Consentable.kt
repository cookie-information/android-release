package com.cookieinformation.mobileconsents

import com.cookieinformation.mobileconsents.models.MobileConsentCredentials
import com.cookieinformation.mobileconsents.storage.ConsentWithType
import java.util.UUID

public interface Consentable {
  public val sdk: MobileConsents
  public fun provideConsentSdk(): MobileConsentSdk
  public fun provideCredentials(): MobileConsentCredentials
  public suspend fun getSavedConsents(): Map<UUID, Boolean> {
    return sdk.getMobileConsentSdk().getSavedConsents()
  }
  public suspend fun getSavedConsentsWithType(): Map<UUID, ConsentWithType> {
    return sdk.getMobileConsentSdk().getSavedConsentsWithType()
  }
}