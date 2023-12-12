package com.cookieinformation.mobileconsents.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.models.SdkTextStyle
import com.cookieinformation.mobileconsents.models.SubtitleStyle
import java.util.UUID

/**
 * The RecyclerView's adapter for [PrivacyPreferencesItem] item model.
 */
internal class PrivacyPreferencesListAdapter(
  private val onConsentItemChoiceChanged: (UUID, Boolean) -> Unit,
  private val sdkColor: Int?,
  private val sdkTextStyle: SdkTextStyle?
) :
  ListAdapter<ItemizedPreference, BindableViewHolder<ItemizedPreference>>(AdapterConsentItemDiffCallback()) {

  private val PREFERENCE_ITEM = 0
  private val PREFERENCE_TYPE_HEADER = 1

  override fun getItemViewType(position: Int): Int {
    return when (currentList[position]) {
      is PrivacyPreferencesItem -> PREFERENCE_ITEM
      is PrivacyPreferencesItemHeader -> PREFERENCE_TYPE_HEADER
      else -> PREFERENCE_ITEM
    }
  }

  fun ViewGroup.inflate(layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder<ItemizedPreference> {
    return when (viewType) {
      PREFERENCE_TYPE_HEADER -> HeaderItemViewHolder(
        parent.inflate(R.layout.mobileconsents_privacy_item_preferences_item_header),
        sdkTextStyle?.subtitleStyle
      ) as BindableViewHolder<ItemizedPreference>
      PREFERENCE_ITEM -> PreferenceItemViewHolder(
        parent.inflate(R.layout.mobileconsents_privacy_item_preferences_item),
        sdkColor,
        onConsentItemChoiceChanged, sdkTextStyle
      ) as BindableViewHolder<ItemizedPreference>
      else -> PreferenceItemViewHolder(
        parent.inflate(R.layout.mobileconsents_privacy_item_preferences_item),
        sdkColor,
        onConsentItemChoiceChanged,
        sdkTextStyle
      ) as BindableViewHolder<ItemizedPreference>
    }
  }

  override fun onBindViewHolder(holder: BindableViewHolder<ItemizedPreference>, position: Int) {
    (currentList[position] as? PrivacyPreferencesItem)?.let {
      holder.bind(it)
    }

    (currentList[position] as? PrivacyPreferencesItemHeader)?.let {
      holder.bind(it)
    }
  }

  class AdapterConsentItemDiffCallback : DiffUtil.ItemCallback<ItemizedPreference>() {

    override fun areItemsTheSame(oldItem: ItemizedPreference, newItem: ItemizedPreference): Boolean {
      return if (oldItem is PrivacyPreferencesItemHeader && newItem is PrivacyPreferencesItemHeader) true
      else oldItem is PrivacyPreferencesItem && newItem is PrivacyPreferencesItem
    }

    override fun areContentsTheSame(oldItem: ItemizedPreference, newItem: ItemizedPreference): Boolean {
      return (oldItem is PrivacyPreferencesItemHeader && newItem is PrivacyPreferencesItemHeader && oldItem.title == newItem.title) || oldItem is PrivacyPreferencesItem && newItem is PrivacyPreferencesItem && oldItem == newItem
    }
  }
}

private val requireIndicator: String = "<a fo href=\"\">*</a>"

public abstract class BindableViewHolder<T>(itemView: View) : ViewHolder(itemView) {
  public abstract fun bind(data: T)
}

public class PreferenceItemViewHolder(
  itemView: View,
  private val sdkColor: Int?,
  public val onConsentItemChanged: (UUID, Boolean) -> Unit,
  public val sdkTextStyle: SdkTextStyle?
) : BindableViewHolder<PrivacyPreferencesItem>(itemView) {

  @SuppressLint("UseSwitchCompatOrMaterialCode")
  private val consentSwitch =
    itemView.findViewById<Switch>(R.id.mobileconsents_privacy_preferences_item_checkbox).apply {
      sdkColor?.let { color ->
        ColorStateList(
          arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled)
          ), intArrayOf(color, Color.LTGRAY)
        ).also { states ->
          thumbDrawable.setTintList(states)
          trackDrawable.setTintList(states)
        }
      }
    }

  private val consentText =
    itemView.findViewById<TextView>(R.id.mobileconsents_privacy_preferences_item_text)

  private val consentDetails =
    itemView.findViewById<TextView?>(R.id.mobileconsents_privacy_preferences_item_details)

  override fun bind(
    data: PrivacyPreferencesItem,
  ) {
    consentText.apply {
      text = data.text
      sdkTextStyle?.subtitleStyle?.let {
        typeface = it.typeface
      }
    }
    consentSwitch.apply {
      isChecked = data.accepted || data.required
      if (data.required) {
        consentSwitch.contentDescription = itemView.context.getString(R.string.switch_btn_is_disabled)
        onConsentItemChanged(data.id, isChecked)
      } else{
        consentSwitch.contentDescription = null//itemView.context.getString(R.string.switch_btn_is_disabled)
      }
      isClickable = !data.required
      setOnCheckedChangeListener { buttonView, isChecked ->
        if (buttonView.isPressed) {
          // Detect only user action
          onConsentItemChanged(data.id, isChecked)
        }
      }
    }
    consentDetails?.apply {
      //this is to support clients that would like to html the content of the consents
      text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(data.details, Html.FROM_HTML_MODE_LEGACY).toString()
      } else {
        Html.fromHtml(data.details).toString()
      }

      sdkTextStyle?.bodyStyle?.let {
        typeface = it.typeface
      }
      visibility = if (data.details.isBlank()) View.GONE else View.VISIBLE
    }
  }
}

public class HeaderItemViewHolder(itemView: View, private val style: SubtitleStyle?) :
  BindableViewHolder<PrivacyPreferencesItemHeader>(itemView) {

  private val header =
    itemView.findViewById<TextView?>(R.id.header)

  override fun bind(
    data: PrivacyPreferencesItemHeader,
  ) {
    header.apply {
      text = data.title
      style?.typeface?.let {
        typeface = it
      }
    }
  }
}
