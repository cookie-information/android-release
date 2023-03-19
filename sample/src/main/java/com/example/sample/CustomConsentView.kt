package com.example.sample

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cookieinformation.mobileconsents.ConsentItem
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.ui.base.BaseConsentsView
import com.cookieinformation.mobileconsents.ui.PrivacyFragmentViewData
import com.cookieinformation.mobileconsents.ui.PrivacyPreferencesItem
import com.example.sample.CustomConsentsAdapter.VH

class CustomConsentView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
) : BaseConsentsView(context, attrs, defStyleAttr, defStyleRes) {

  override fun getConsentsLayout(): Int {
    return R.layout.custom_consent_layout
  }

  init {
    findViewById<Button>(R.id.accept_all).setOnClickListener {
      onAcceptAllClicked()
    }
    findViewById<Button>(R.id.accept_selected).setOnClickListener {
      onAcceptSelectedClicked()
    }
  }

  override fun getLayoutViewGroup(): ViewGroup = this

  override fun showProgressBar() {

  }

  override fun hideProgressBar() {

  }

  override fun showViewData(data: PrivacyFragmentViewData) {
    val adapter = CustomConsentsAdapter(onChoiceChanged = ::onChoiceChanged)
    findViewById<RecyclerView>(R.id.consents_list).adapter = adapter

    adapter.submitList(data.items[0].items)

  }

  override fun showRetryDialog(onRetry: () -> Unit, onDismiss: () -> Unit, title: String, message: String) {

  }

  override fun showErrorDialog(onDismiss: () -> Unit) {

  }
}

class CustomConsentsAdapter(val onChoiceChanged: (type: Type, accepted: Boolean) -> Unit) :
  ListAdapter<PrivacyPreferencesItem, VH>(diffUtil) {

  companion object {
    val diffUtil = object : DiffUtil.ItemCallback<PrivacyPreferencesItem>() {
      override fun areItemsTheSame(oldItem: PrivacyPreferencesItem, newItem: PrivacyPreferencesItem): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: PrivacyPreferencesItem, newItem: PrivacyPreferencesItem): Boolean {
        return oldItem.type == newItem.type
      }
    }
  }

  class VH(view: View) : RecyclerView.ViewHolder(view) {

    val consentTitle = view.findViewById<TextView>(R.id.consent_title)
    val consentState = view.findViewById<Switch>(R.id.consent_state)

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.single_consent_item, parent, false)
    return VH(view)
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    val data = currentList[position]
    holder.consentTitle.text = data.text
    holder.consentState.isChecked = data.accepted || data.required
    holder.consentState.setOnCheckedChangeListener { compoundButton, b ->
      onChoiceChanged(data.type, b)
    }
  }
}