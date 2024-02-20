package com.cookieinformation.mobileconsents

/**
 * Strings for UI fetched from CDN server.
 * @param privacyPreferencesTitle list of "Privacy Preferences" title translations
 * @param privacyPreferencesDescription list of "Privacy Preferences" description translations
 * @param privacyPreferencesTabLabel list of "Privacy Preferences" tab label translations
 * @param privacyCenterButton list of "Read More" Button's label translations
 * @param acceptAllButton list of "Accept All" Button's label translations
 * @param rejectAllButton list of "Reject All" Button's label translations
 * @param acceptSelectedButton list of "Accept Selected" Button's label translations
 * @param savePreferencesButton list of "Accept" Button's label translations
 * @param privacyCenterTitle list of "Privacy" Center's title translations
 * @param poweredByLabel list of "Powered by Cookie Information" label translations
 * @param consentPreferencesLabel list of "Consent Preferences" label translations
 */
public data class UiTexts(
  val privacyPreferencesTitle: List<TextTranslation>,
  val privacyPreferencesDescription: List<TextTranslation>,
  val privacyPreferencesTabLabel: List<TextTranslation>,

  val privacyCenterButton: List<TextTranslation>,
  val acceptAllButton: List<TextTranslation>,
  val rejectAllButton: List<TextTranslation>,
  val acceptSelectedButton: List<TextTranslation>,
  val savePreferencesButton: List<TextTranslation>,

  val privacyCenterTitle: List<TextTranslation>,

  val poweredByLabel: List<TextTranslation>,
  val consentPreferencesLabel: List<TextTranslation>,

  val requiredTableSectionHeader: List<TextTranslation>,
  val optionalTableSectionHeader: List<TextTranslation>,
  val readMoreScreenHeader: List<TextTranslation>,
)
