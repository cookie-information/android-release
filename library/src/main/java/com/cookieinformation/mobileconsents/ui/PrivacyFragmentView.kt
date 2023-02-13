package com.cookieinformation.mobileconsents.ui

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.ui.PrivacyFragmentView.IntentListener
import com.cookieinformation.mobileconsents.util.setTextFromHtml
import java.util.UUID

/**
 * The Privacy view implementation. The view is used in [BasePrivacyFragment] and should not be used directly
 * (except for ex. capturing events for analytics by [PrivacyFragmentView.IntentListener]).
 */
public class PrivacyFragmentView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
  sdkColor: Int?
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
  ConsentSolutionView<PrivacyFragmentViewData, IntentListener> {

  /**
   * A listener for events that can be triggered by the user.
   */
  @MainThread
  public interface IntentListener {

    /**
     * Called when the user wants to change the choice for the consent.
     *
     * @param id [UUID] of the consents.
     * @param accepted user's choice.
     */
    public fun onPrivacyChoiceChanged(id: Type, accepted: Boolean)

    /**
     * Called when the user accepts selected consents. It is called only if all required consents are chosen by the user.
     */
    public fun onPrivacyAcceptSelectedClicked()

    /**
     * Called when the user accepts all consents.
     */
    public fun onPrivacyAcceptAllClicked()

    /**
     * Called when the user wants to close the view.
     */
    public fun onPrivacyCenterDismissRequest()
  }

  private val intentListeners = mutableSetOf<IntentListener>()
  private val consentListAdapter = PrivacyFragmentListAdapter(::onChoiceChanged, sdkColor)
  public var onReadMore: (info: String, poweredBy: String) -> Unit = { _, _ ->
  }

  private val contentView: View
  private val progressBar: View
  public var parsedColorToInt: Int? = sdkColor//Color.parseColor("#FFE91E63")// = attrs?.getAttributeResourceValue(0, 0)

  private lateinit var data: PrivacyFragmentViewData

  init {
    inflate(context, R.layout.mobileconsents_privacy, this)
    contentView = findViewById(R.id.mobileconsents_privacy_layout)
    contentView.visibility = View.GONE

    inflate(context, R.layout.mobileconsents_progressbar, this)
    progressBar = findViewById(R.id.mobileconsents_progressbar_layout)
    progressBar.visibility = View.VISIBLE

    parsedColorToInt?.let {
      progressBar.findViewById<ProgressBar>(R.id.mobileconsents_progressbar).indeterminateDrawable.setColorFilter(
        it, PorterDuff.Mode.SRC_IN
      )
    }

    contentView.findViewById<RecyclerView>(R.id.mobileconsents_privacy_list).apply {
      setHasFixedSize(true)
      (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
      adapter = consentListAdapter
    }

    contentView.findViewById<Toolbar>(R.id.mobileconsents_privacy_toolbar).apply {
      parsedColorToInt?.let {
        setBackgroundColor(it)
      }
      setNavigationOnClickListener {
        onDismissRequest()
      }
    }

    findViewById<TextView>(R.id.mobileconsents_privacy_info_read_more).apply {
      parsedColorToInt?.let {
        setTextColor(it)
      }
      setOnClickListener {
        onReadMoreClicked()
      }
    }
    findViewById<ImageView>(R.id.mobileconsents_privacy_info_read_more_arrow).apply {
      parsedColorToInt?.let {
        setColorFilter(it)
      }
    }

    findViewById<Button>(R.id.mobileconsents_privacy_accept_selected_button).apply {
      parsedColorToInt?.let {
        setBackgroundColor(it)
      }
      setOnClickListener { onAcceptSelectedClicked() }
    }

    findViewById<Button>(R.id.mobileconsents_privacy_accept_all_button).apply {
      parsedColorToInt?.let {
        setBackgroundColor(it)
      }
      setOnClickListener { onAcceptAllClicked() }
    }
  }

  private fun onReadMoreClicked() {
    onReadMore(data.privacyDescriptionLongText, data.poweredByLabelText)
  }

  private fun onAcceptSelectedClicked() {
    for (listener in intentListeners) {
      listener.onPrivacyAcceptSelectedClicked()
    }
  }

  private fun onAcceptAllClicked() {
    for (listener in intentListeners) {
      listener.onPrivacyAcceptAllClicked()
    }
  }

  private fun onChoiceChanged(id: Type, accepted: Boolean) {
    for (listener in intentListeners) {
      listener.onPrivacyChoiceChanged(id, accepted)
    }
  }

  private fun onDismissRequest() {
    for (listener in intentListeners) {
      listener.onPrivacyCenterDismissRequest()
    }
  }

  public override fun addIntentListener(listener: IntentListener) {
    require(!intentListeners.contains(listener))
    intentListeners.add(listener)
  }

  public override fun removeIntentListener(listener: IntentListener) {
    require(intentListeners.contains(listener))
    intentListeners.remove(listener)
  }

  override fun showProgressBar() {
    progressBar.visibility = View.VISIBLE
  }

  override fun hideProgressBar() {
    progressBar.visibility = View.GONE
  }

  override fun showViewData(data: PrivacyFragmentViewData) {
    this.data = data
    findViewById<TextView>(R.id.mobileconsents_privacy_info_title).apply {
      text = data.privacyTitleText
    }
    findViewById<TextView>(R.id.mobileconsents_privacy_info_short_description).apply {
      text = data.privacyDescriptionShortText
    }
    findViewById<TextView>(R.id.mobileconsents_privacy_info_read_more).apply {
      text = data.privacyReadMoreText
    }

    findViewById<Button>(R.id.mobileconsents_privacy_accept_selected_button).apply {
      text = data.acceptSelectedButtonText
      isEnabled = data.acceptSelectedButtonEnabled
    }
    findViewById<Button>(R.id.mobileconsents_privacy_accept_all_button).apply {
      text = data.acceptAllButtonText
    }
    contentView.findViewById<TextView>(R.id.powered_by_label).apply {
      setTextFromHtml(data.poweredByLabelText, boldLinks = false, underline = true)
    }

    consentListAdapter.submitList(data.items)
    showContentViewData()
  }

  private fun showContentViewData() {
    contentView.visibility = View.VISIBLE
  }

  override fun showRetryDialog(onRetry: () -> Unit, onDismiss: () -> Unit, title: String, message: String) {
    // postDelayed is workaround for: If view is embedded in a DialogFragment, the below dialog is shown under the DialogFragment.
    postDelayed({ createRetryDialog(context, onRetry, onDismiss, title, message).show() }, 0)
  }

  override fun showErrorDialog(onDismiss: () -> Unit) {
    // postDelayed is workaround for: If view is embedded in a DialogFragment, the below dialog is shown under the DialogFragment.
    postDelayed({ createErrorDialog(context, onDismiss).show() }, 0)
  }
}
