package com.cookieinformation.mobileconsents.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cookieinformation.mobileconsents.Consentable
import com.cookieinformation.mobileconsents.ui.ConsentSolutionBinder
import com.cookieinformation.mobileconsents.ui.ConsentSolutionBinder.Builder
import com.cookieinformation.mobileconsents.ui.ConsentSolutionBinder.InternalBuilder
import com.cookieinformation.mobileconsents.ui.ConsentSolutionListener
import com.cookieinformation.mobileconsents.ui.PrivacyFragmentViewModel
import com.cookieinformation.mobileconsents.ui.PrivacyFragmentViewModel.Factory

internal abstract class BasePrivacyFragment : Fragment(), ConsentSolutionListener {

  protected val viewModel: PrivacyFragmentViewModel by viewModels {
    createViewModelFactory(bindConsentSolution(InternalBuilder(requireContext())))
  }

  /**
   * The developer must implement this method to bind the fragment with the instance of
   * [com.cookieinformation.mobileconsents.MobileConsentSdk] and [UUID] of the consent solution.
   *
   * The developer should provide the same instance of
   * [com.cookieinformation.mobileconsents.MobileConsentSdk] after configuration has changed.
   *
   * @param builder fluent builder that allows the developer to set up an instance of
   * [com.cookieinformation.mobileconsents.MobileConsentSdk], [UUID] of the consent solution and
   * optionally [LocaleProvider].
   */
  private fun bindConsentSolution(builder: Builder): ConsentSolutionBinder {
    val app = requireContext().applicationContext as Consentable
    val mobileConsentSdk = app.sdk
    return builder
      .setMobileConsentSdk(mobileConsentSdk.getMobileConsentSdk())
      .setLocaleProvider(mobileConsentSdk.getMobileConsentSdk().uiLanguageProvider())
      .create()
  }

  private fun createViewModelFactory(binder: ConsentSolutionBinder) = Factory(binder)

}