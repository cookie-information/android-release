package com.cookieinformation.mobileconsents

import com.cookieinformation.mobileconsents.models.MobileConsentCredentials
import com.cookieinformation.mobileconsents.storage.ConsentWithType
import java.io.IOException
import java.util.UUID

public interface Consentable {
  public val sdk: MobileConsents
  public fun provideConsentSdk(): MobileConsentSdk
  public fun provideCredentials(): MobileConsentCredentials

  /**
   * This function (or initSDK) initializes the sdk and performs necessary migrations. It must be called (and completed) before
   * any other function that utilizes the sdk functionality, like showing the consent popup or
   * retrieving the saved consents.
   */
  public fun initSDKWithCompletion(onComplete: (success: Boolean, exception: IOException?) -> Unit) {
    return sdk.initSDKWithCompletion(onComplete)
  }

  public suspend fun getSavedConsents(): Map<UUID, Boolean> {
    return sdk.getMobileConsentSdk().getSavedConsents()
  }
  public suspend fun getSavedConsentsWithType(): Map<UUID, ConsentWithType> {
    return sdk.getMobileConsentSdk().getSavedConsentsWithType()
  }
}