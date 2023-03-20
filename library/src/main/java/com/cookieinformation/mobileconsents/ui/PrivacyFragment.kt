package com.cookieinformation.mobileconsents.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.ConsentSolution
import com.cookieinformation.mobileconsents.Consentable
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.models.SdkTextStyle
import com.cookieinformation.mobileconsents.ui.ConsentSolutionViewModel.Event
import com.cookieinformation.mobileconsents.ui.base.BaseConsentsView
import com.cookieinformation.mobileconsents.ui.base.BasePrivacyFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

/**
 * The base fragment for "Privacy Fragment" view.
 */
internal class PrivacyFragment : BasePrivacyFragment() {

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
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val app = requireContext().applicationContext as Consentable
    val mobileConsentCustomView = app.sdk.getMobileConsentSdk().getConsentsView()
    return mobileConsentCustomView ?: PrivacyFragmentView(requireContext(), sdkColor = getSdkSetColor(), sdkTextStyle = getCustomSetFont()).also {
      it.onReadMore = ::onReadMore
    }
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
    viewModel.attachView(view as BaseConsentsView)
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

  override fun onReadMore(info: String, poweredBy: String) {
    ReadMoreBottomSheet.newInstance(info, poweredBy, getSdkSetColor()).show(parentFragmentManager, "tag")
  }

  override fun onConsentsChosen(consentSolution: ConsentSolution, consents: Map<UUID, Boolean>, external: Boolean) {
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
