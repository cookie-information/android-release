package com.cookieinformation.mobileconsents.ui

import android.content.Context
import com.cookieinformation.mobileconsents.MobileConsentSdk

/**
 * The class binds [BasePrivacyFragment] with the instance of
 * [Context] of the application and the [MobileConsentSdk]. Optionally [LocaleProvider]
 * can be set up, by default [DefaultLocaleProvider] is used.
 */
public class ConsentSolutionBinder internal constructor(
  public val applicationContext: Context,
  public val mobileConsentSdk: MobileConsentSdk,
  public val localeProvider: LocaleProvider
) {

  public interface Builder {
    /**
     * Sets the [MobileConsentSdk] instance.
     */
    public fun setMobileConsentSdk(mobileConsentSdk: MobileConsentSdk): BuilderLocaleProvider
  }

  public interface BuilderLocaleProvider : BuilderCreate {
    /**
     * Sets the custom [LocaleProvider].
     */
    public fun setLocaleProvider(localeProvider: LocaleProvider): BuilderCreate
  }

  public interface BuilderCreate {
    /**
     * Creates the instance of [ConsentSolutionBinder]
     */
    public fun create(): ConsentSolutionBinder
  }

  internal class InternalBuilder(context: Context) : Builder, BuilderLocaleProvider {

    private val applicationContext = context.applicationContext
    private lateinit var mobileConsentSdk: MobileConsentSdk
    private var localeProvider: LocaleProvider? = null

    override fun setMobileConsentSdk(mobileConsentSdk: MobileConsentSdk): BuilderLocaleProvider {
      this.mobileConsentSdk = mobileConsentSdk
      return this
    }

    override fun setLocaleProvider(localeProvider: LocaleProvider): BuilderCreate {
      this.localeProvider = localeProvider
      return this
    }

    override fun create(): ConsentSolutionBinder = ConsentSolutionBinder(
      applicationContext = applicationContext,
      mobileConsentSdk = mobileConsentSdk,
      localeProvider = localeProvider ?: DefaultLocaleProvider(applicationContext)
    )
  }
}
