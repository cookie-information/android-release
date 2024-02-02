package com.cookieinformation.mobileconsents.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.models.SdkTextStyle
import com.cookieinformation.mobileconsents.ui.base.BaseConsentsView
import com.cookieinformation.mobileconsents.util.setTextFromHtml

/**
 * The Privacy view implementation. The view is used in [BasePrivacyFragment] and should not be used directly
 * (except for ex. capturing events for analytics by [PrivacyFragmentView.IntentListener]).
 */
public class PrivacyFragmentView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
  sdkColor: Int?,
  sdkTextStyle: SdkTextStyle?
) : BaseConsentsView(context, attrs, defStyleAttr, defStyleRes) {

  private val consentListAdapter = PrivacyFragmentListAdapter(::onChoiceChanged, sdkColor, sdkTextStyle)
  public var onReadMore: (info: String, poweredBy: String) -> Unit = { _, _ ->
  }

  private val contentView: View
  private val progressBar: View
  public var parsedColorToInt: Int? = sdkColor
  public var customTypeface: SdkTextStyle? = sdkTextStyle

  private lateinit var data: PrivacyFragmentViewData

  init {

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

    findViewById<LinearLayout>(R.id.mobileconsents_privacy_info_read_more_container).apply {
      setOnClickListener {
        onReadMoreClicked()
      }
    }
    findViewById<TextView>(R.id.mobileconsents_privacy_info_read_more).apply {
      parsedColorToInt?.let {
        setTextColor(it)
      }
      customTypeface?.subtitleStyle?.let {
        typeface = it.typeface
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

  override fun getConsentsLayout(): Int {
    return R.layout.mobileconsents_privacy
  }

  override fun getLayoutViewGroup(): ViewGroup {
    return this
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
      customTypeface?.titleStyle?.let {
        typeface = it.typeface
      }
    }
    findViewById<TextView>(R.id.mobileconsents_privacy_info_short_description).apply {
      text = data.privacyDescriptionShortText
      customTypeface?.subtitleStyle?.let {
        typeface = it.typeface
      }
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
}
