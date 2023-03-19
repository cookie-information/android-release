package com.cookieinformation.mobileconsents.ui

import androidx.lifecycle.ViewModel
import com.cookieinformation.mobileconsents.ui.base.BaseConsentsView

/**
 * The view model class for the [BasePrivacyFragment].
 */
internal class PrivacyFragmentViewModel(binder: ConsentSolutionBinder) :
  ConsentSolutionViewModel<BaseConsentsView, PrivacyFragmentPresenter>(PrivacyFragmentPresenter(), binder) {

  class Factory(private val binder: ConsentSolutionBinder) : androidx.lifecycle.ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return PrivacyFragmentViewModel(binder) as T
    }
  }
}
