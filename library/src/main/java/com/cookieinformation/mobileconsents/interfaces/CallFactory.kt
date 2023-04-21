package com.cookieinformation.mobileconsents.interfaces

import com.cookieinformation.mobileconsents.MobileConsentSdk
import com.cookieinformation.mobileconsents.models.MobileConsentCredentials
import com.cookieinformation.mobileconsents.models.MobileConsentCustomUI
import com.cookieinformation.mobileconsents.ui.base.BaseConsentsView
import java.util.Locale

/**
 * Fluent Builder [MobileConsentSdkBuilder] interface.
 */
public interface CallFactory {
  public fun setClientCredentials(credentials: MobileConsentCredentials): CallFactory
  public fun setMobileConsentCustomUI(customUI: MobileConsentCustomUI): CallFactory
  public fun setLanguages(locales: List<Locale> = listOf( Locale.getDefault())): CallFactory
  public fun setCustomConsentView(view: BaseConsentsView?): CallFactory
  public fun build(): MobileConsentSdk
}