package com.cookieinformation.mobileconsents.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.ConsentSolution
import com.cookieinformation.mobileconsents.Consentable
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.models.SdkTextStyle
import com.cookieinformation.mobileconsents.ui.ConsentSolutionViewModel.Event
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

/**
 * The base fragment for "Privacy Fragment" view.
 */
internal class PrivacyFragment : Fragment(), ConsentSolutionListener {

  private val viewModel: PrivacyFragmentViewModel by viewModels {
    createViewModelFactory(bindConsentSolution(ConsentSolutionBinder.InternalBuilder(requireContext())))
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
  private fun bindConsentSolution(builder: ConsentSolutionBinder.Builder): ConsentSolutionBinder {
    val app = requireContext().applicationContext as Consentable
    val mobileConsentSdk = app.sdk
    return builder
      .setMobileConsentSdk(mobileConsentSdk.getMobileConsentSdk())
      .setLocaleProvider(mobileConsentSdk.getMobileConsentSdk().uiLanguageProvider())
      .create()
  }

  fun getSdkSetColor(): Int? {
    val app = requireContext().applicationContext as Consentable
    val mobileConsentSdk = app.sdk
    return mobileConsentSdk.getMobileConsentSdk().getUiComponentColor()?.primaryColor
  }

  fun getCustomSetFont(): SdkTextStyle? {
    val app = requireContext().applicationContext as Consentable
    val mobileConsentSdk = app.sdk
    return mobileConsentSdk.getMobileConsentSdk().getUiComponentColor()?.sdkTextStyle
  }

  /**
   * If method is overridden, the super must be called.
   */
  @CallSuper
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
    PrivacyFragmentView(requireContext(), sdkColor =  getSdkSetColor(), sdkTextStyle = getCustomSetFont()).also {
      it.onReadMore = ::onReadMore
    }

  /**
   * If method is overridden, the super must be called.
   */
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.events
      .onEach(::handleEvent)
      .launchIn(viewLifecycleOwner.lifecycleScope)
    viewModel.attachView(view as PrivacyFragmentView)
    val isTablet = resources.getBoolean(R.bool.isTablet)
    if (!isTablet) {
      // Force the fragments in portrait mode if app is running on a smartphone, since there is only one layout
      activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
  }

  /**
   * If method is overridden, the super must be called.
   */
  @CallSuper
  override fun onDestroyView() {
    // Set the orientation as unspecified no matter what, then fragment is destroyed
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    viewModel.detachView()
    super.onDestroyView()
  }

  private fun handleEvent(event: Event) =
    when (event) {
      is Event.ConsentsChosen -> with(event) { onConsentsChosen(consentSolution, consents, external) }
      is Event.ReadMore -> onReadMore(event.info, event.poweredBy)
      Event.Dismiss -> onDismissed()
    }

  private fun createViewModelFactory(binder: ConsentSolutionBinder) = PrivacyFragmentViewModel.Factory(binder)

  override fun onReadMore(info: String, poweredBy: String) {
    ReadMoreBottomSheet.newInstance(info, poweredBy, getSdkSetColor()).show(parentFragmentManager, "tag")
  }

  override fun onConsentsChosen(consentSolution: ConsentSolution, consents: Map<Type, Boolean>, external: Boolean) {
    requireActivity().onBackPressed()
  }

  override fun onDismissed() {
    requireActivity().onBackPressed()
  }

  companion object {

    @JvmStatic
    fun newInstance() = PrivacyFragment()
  }
}
