package com.cookieinformation.mobileconsents.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.MainThread
import com.cookieinformation.mobileconsents.ui.ConsentSolutionView
import com.cookieinformation.mobileconsents.ui.PrivacyFragmentViewData
import com.cookieinformation.mobileconsents.ui.createErrorDialog
import com.cookieinformation.mobileconsents.ui.createRetryDialog
import java.util.UUID

public abstract class BaseConsentsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
  ConsentSolutionView<PrivacyFragmentViewData, IntentListener> {

  init {
    inflate(context, getConsentsLayout(), getLayoutViewGroup())
  }

  protected var activityCxt: Context= context

  public abstract fun getConsentsLayout(): Int
  public abstract fun getLayoutViewGroup(): ViewGroup
  public fun setActivityContext(ctx: Context){
    this.activityCxt = ctx
  }

  private val intentListeners = mutableSetOf<IntentListener>()

  protected fun onAcceptSelectedClicked() {
    for (listener in intentListeners) {
      listener.onPrivacyAcceptSelectedClicked()
    }
  }

  protected fun onAcceptAllClicked() {
    for (listener in intentListeners) {
      listener.onPrivacyAcceptAllClicked()
    }
  }

  protected fun onChoiceChanged(uuid: UUID, accepted: Boolean) {
    for (listener in intentListeners) {
      listener.onPrivacyChoiceChanged(uuid, accepted)
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

  override fun showRetryDialog(onRetry: () -> Unit, onDismiss: () -> Unit, title: String, message: String) {
    // postDelayed is workaround for: If view is embedded in a DialogFragment, the below dialog is shown under the DialogFragment.
    postDelayed({ createRetryDialog(activityCxt, onRetry, onDismiss, title, message).show() }, 0)
  }

  override fun showErrorDialog(onDismiss: () -> Unit) {
    // postDelayed is workaround for: If view is embedded in a DialogFragment, the below dialog is shown under the DialogFragment.
    postDelayed({ createErrorDialog(activityCxt, onDismiss).show() }, 0)
  }
}


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
  public fun onPrivacyChoiceChanged(uuid: UUID, accepted: Boolean)

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