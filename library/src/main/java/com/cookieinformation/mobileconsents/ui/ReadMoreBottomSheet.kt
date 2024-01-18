package com.cookieinformation.mobileconsents.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import com.cookieinformation.mobileconsents.Consentable
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.util.setTextFromHtml
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class ReadMoreBottomSheet :
  BottomSheetDialogFragment(R.layout.read_more_bottom_sheet_layout) {

  val info by lazy {
    requireArguments().getString("info")
  }
  val poweredBy by lazy {
    requireArguments().getString("poweredBy").orEmpty()
  }
  val sdkColor: Int? by lazy {
    requireArguments().getInt("sdkColor")
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = BottomSheetDialog(requireContext(), theme)
    dialog.setOnShowListener {

      val bottomSheetDialog = it as BottomSheetDialog
      val parentLayout =
        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
      parentLayout?.let { it ->
        val behaviour = BottomSheetBehavior.from(it)
        behaviour.skipCollapsed = true
        setupFullHeight(it)
        behaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
      }
    }
    return dialog
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<TextView>(R.id.mobileconsents_privacy_info_read_more).apply {
      val app = requireContext().applicationContext as Consentable
      val style = app.sdk.getMobileConsentSdk().getUiComponentColor()?.sdkTextStyle
      style?.bodyStyle?.typeface?.let {
        typeface = it
      }
      text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(info, Html.FROM_HTML_MODE_COMPACT)
      } else {
        Html.fromHtml(info)
      }
    }

    view.findViewById<TextView>(R.id.powered_by_label).apply {
      setTextFromHtml(poweredBy, boldLinks = false, underline = true)
    }

    view.findViewById<Toolbar>(R.id.mobileconsents_privacy_toolbar).apply {
      navigationContentDescription = getString(R.string.navigation_content_description)
      sdkColor?.let {
        setBackgroundColor(it)
      }
      setNavigationOnClickListener {
        dismiss()
      }
    }
  }

  private fun setupFullHeight(bottomSheet: View) {
    val layoutParams = bottomSheet.layoutParams
    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    bottomSheet.layoutParams = layoutParams
  }

  companion object {
    fun newInstance(info: String, poweredBy: String, sdkColor: Int?) = ReadMoreBottomSheet().apply {
      arguments = bundleOf("info" to info, "poweredBy" to poweredBy, "sdkColor" to sdkColor)
    }
  }
}