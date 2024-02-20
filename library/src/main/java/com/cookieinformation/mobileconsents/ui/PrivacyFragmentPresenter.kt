package com.cookieinformation.mobileconsents.ui

import com.cookieinformation.mobileconsents.ConsentItem.Type.Info
import com.cookieinformation.mobileconsents.ConsentSolution
import com.cookieinformation.mobileconsents.UiTexts
import com.cookieinformation.mobileconsents.ui.base.BaseConsentsView
import com.cookieinformation.mobileconsents.ui.base.IntentListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.UUID

/**
 * The presenter for the [PrivacyFragmentView] view.
 */
internal class PrivacyFragmentPresenter(
  dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ConsentSolutionPresenter<BaseConsentsView, PrivacyFragmentViewData, IntentListener>(dispatcher),
  IntentListener {

  private val preferencesItemId = UUID(0, 0)

  private lateinit var preferencesItem: PrivacyFragmentPreferencesItem
  private lateinit var infoItem: PrivacyInfoItem

  override fun getViewIntentListener(): IntentListener = this

  override fun createViewData(
    consentSolution: ConsentSolution,
    savedConsents: Map<UUID, Boolean>
  ): PrivacyFragmentViewData {
    preferencesItem = createPreferencesItem(consentSolution, savedConsents)
    infoItem = createInfoItem(consentSolution)
    return createPrivacyFragmentViewData(consentSolution.uiTexts)
  }

  override fun getGivenConsents(viewData: PrivacyFragmentViewData): GivenConsent =
    preferencesItem.items.map { it.id to Pair(it.accepted, it.language) }.toMap()

  override fun onConsentsChangedWhileFetched(consents: Map<UUID, Boolean>) {
    @Suppress("UNCHECKED_CAST")
    val currentViewState = viewState as ViewState.Fetched<PrivacyFragmentViewData>

    if (currentViewState.data.items.last() is PrivacyFragmentPreferencesItem) {
      viewState = currentViewState.copy(
        data = newViewData(
          currentViewState.data,
          newPreferencesItems(preferencesItem.items, consents)
        )
      )
    } else {
      preferencesItem = preferencesItem.copy(items = newPreferencesItems(preferencesItem.items, consents))
    }
  }

  override fun onConsentsChangedWhileSendError(consents: Map<UUID, Boolean>) {
    @Suppress("UNCHECKED_CAST")
    val currentViewState = viewState as ViewState.SendError<PrivacyFragmentViewData>

    if (currentViewState.data.items.last() is PrivacyFragmentPreferencesItem) {
      viewState = currentViewState.copy(
        data = newViewData(
          currentViewState.data,
          newPreferencesItems(preferencesItem.items, consents)
        )
      )
    } else {
      preferencesItem = preferencesItem.copy(items = newPreferencesItems(preferencesItem.items, consents))
    }
  }

  private fun newPreferencesItems(
    items: List<PrivacyPreferencesItem>,
    consents: Map<UUID, Boolean>
  ): List<PrivacyPreferencesItem> =
    items.map {
      val accepted = consents[it.id] ?: false || it.required
      if (it.accepted != accepted) it.copy(accepted = accepted) else it
    }

  override fun onPrivacyChoiceChanged(id: UUID, accepted: Boolean) {
    @Suppress("UNCHECKED_CAST")
    val currentViewState = viewState as? ViewState.Fetched<PrivacyFragmentViewData> ?: return
    require(currentViewState.data.items.last() is PrivacyFragmentPreferencesItem)

    val preferenceItems = preferencesItem.items.map { if (it.id == id) it.copy(accepted = accepted) else it }
    viewState = currentViewState.copy(data = newViewData(currentViewState.data, preferenceItems))
  }

  private fun newViewData(
    viewData: PrivacyFragmentViewData,
    preferenceItems: List<PrivacyPreferencesItem>
  ): PrivacyFragmentViewData {
    preferencesItem = preferencesItem.copy(items = preferenceItems)
    val newItems = mutableListOf<PrivacyFragmentPreferencesItem>().apply {
      addAll(viewData.items)
      removeLast()
      add(preferencesItem)
    }

    return viewData.copy(
      items = newItems,
      acceptSelectedButtonEnabled = areAllRequiredAccepted(preferenceItems)
    )
  }

  override fun onPrivacyAcceptSelectedClicked() {
    require(areAllRequiredAccepted(preferencesItem.items))
    sendConsent()
  }

  override fun onPrivacyAcceptAllClicked() {
    acceptAll()
    sendConsent()
  }

  override fun onPrivacyCenterDismissRequest() {
    listener?.onDismissed()
  }

  private fun createPreferencesItem(
    consentSolution: ConsentSolution,
    savedConsents: Map<UUID, Boolean>
  ): PrivacyFragmentPreferencesItem {
    val items = consentSolution.consentItems
//      .filter { it.type.isSetting }
      .map { it.toPrivacyPreferencesItem(savedConsents) }

    val titleTranslation = consentSolution.uiTexts.consentPreferencesLabel.translate()
    val subTitleTranslation = consentSolution.uiTexts.poweredByLabel.translate()

    return PrivacyFragmentPreferencesItem(
      id = preferencesItemId,
      title = titleTranslation.text,
      subTitle = "<a href=\"$cookieInformationUrl\">${subTitleTranslation.text}</a>",
      items = items,
    )
  }

  private fun createInfoItem(
    consentSolution: ConsentSolution
  ): PrivacyInfoItem {
    val item = consentSolution.consentItems.first { it.type == Info }.toPrivacyInfoItem()

    return PrivacyInfoItem(
      text = item.text,
      details = item.details,
      language = item.language,
      type = item.type
    )
  }

  private fun createPrivacyFragmentViewData(
    uiTexts: UiTexts
  ): PrivacyFragmentViewData {
    val items = mutableListOf<PrivacyFragmentPreferencesItem>()
    items.add(preferencesItem)

    val poweredByLabelTranslation = uiTexts.poweredByLabel.translate()
    return PrivacyFragmentViewData(
      privacyTitleText = uiTexts.privacyCenterTitle.translate().text,
      privacyDescriptionShortText = infoItem.text,
      privacyDescriptionLongText = infoItem.details,
      privacyReadMoreText = uiTexts.privacyCenterButton.translate().text,
      acceptSelectedButtonText = uiTexts.acceptSelectedButton.translate().text,
      acceptSelectedButtonEnabled = areAllRequiredAccepted(preferencesItem.items),
      acceptAllButtonText = uiTexts.acceptAllButton.translate().text,
      poweredByLabelText = "<a href=\"$cookieInformationUrl\">${poweredByLabelTranslation.text}</a>",
      requiredTableSectionHeader = uiTexts.requiredTableSectionHeader.translate().text,
      optionalTableSectionHeader = uiTexts.optionalTableSectionHeader.translate().text,
      readMoreScreenHeader = uiTexts.readMoreScreenHeader.translate().text,
      items = items
    )
  }

  private fun areAllRequiredAccepted(items: List<PrivacyPreferencesItem>): Boolean =
    items.firstOrNull { it.required && !it.accepted } == null

  private fun acceptAll() {
    @Suppress("UNCHECKED_CAST")
    val currentViewState = viewState as? ViewState.Fetched<PrivacyFragmentViewData> ?: return
    val preferenceItems = preferencesItem.items.map { it.copy(accepted = true) }
    viewState = currentViewState.copy(data = newViewData(currentViewState.data, preferenceItems))
  }
}
