package com.cookieinformation.mobileconsents.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.models.SdkTextStyle
import java.util.UUID

/**
 * RecyclerView's adapter for [PrivacyFragmentPreferencesItem] item model.
 */
internal class PrivacyFragmentListAdapter(
  private val onConsentItemChoiceToggle: (UUID, Boolean) -> Unit,
  private val sdkColor: Int?,
  private val sdkTextStyle: SdkTextStyle?
) :
  ListAdapter<PrivacyFragmentPreferencesItem, PrivacyFragmentListAdapter.ItemViewHolder>(AdapterConsentItemDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    PreferencesItemViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.mobileconsents_privacy_item_preferences, parent, false),
      onConsentItemChoiceToggle, sdkColor, sdkTextStyle
    )

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(getItem(position))

  abstract class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: PrivacyFragmentPreferencesItem)
  }

  class PreferencesItemViewHolder(itemView: View, onConsentItemChoiceToggle: (UUID, Boolean) -> Unit,
    sdkColor: Int?,
    sdkTextStyle: SdkTextStyle?,
  ) :
    ItemViewHolder(itemView) {

    private val preferencesAdapter = PrivacyPreferencesListAdapter(
      onConsentItemChoiceToggle, sdkColor, sdkTextStyle
    )

    init {
      itemView.findViewById<RecyclerView>(R.id.mobileconsents_privacy_preferences_list).apply {
        adapter = preferencesAdapter
      }
    }

    override fun bind(item: PrivacyFragmentPreferencesItem) {
      val groups = item.items.filter { it.type!= Type.Info }.groupBy { it.required }
      val finalList = mutableListOf<ItemizedPreference>().apply {
        groups[true]?.let {
          add(
            0,
            PrivacyPreferencesItemHeader(itemView.context.getString(R.string.mobileconsents_required_privacy_consents_title))
          )
          addAll(it)
        }
        groups[false]?.let {
          add(PrivacyPreferencesItemHeader(itemView.context.getString(R.string.mobileconsents_optional_privacy_consents_title)))
          addAll(it)
        }
      }
      preferencesAdapter.submitList(finalList)
    }
  }

  class AdapterConsentItemDiffCallback : DiffUtil.ItemCallback<PrivacyFragmentPreferencesItem>() {

    override fun areItemsTheSame(oldItem: PrivacyFragmentPreferencesItem, newItem: PrivacyFragmentPreferencesItem) =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PrivacyFragmentPreferencesItem, newItem: PrivacyFragmentPreferencesItem) =
      oldItem.items == newItem.items
  }
}
