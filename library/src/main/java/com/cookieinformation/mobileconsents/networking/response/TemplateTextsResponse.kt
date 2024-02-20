package com.cookieinformation.mobileconsents.networking.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TemplateTextsResponse(
  @Json(name = "acceptAllButton") val acceptAllButton: List<TextTranslationResponse>,
  @Json(name = "rejectAllButton") val rejectAllButton: List<TextTranslationResponse>,
  @Json(name = "acceptSelectedButton") val acceptSelectedButton: List<TextTranslationResponse>,
  @Json(name = "savePreferencesButton") val savePreferencesButton: List<TextTranslationResponse>,
  @Json(name = "privacyCenterTitle") val privacyCenterTitle: List<TextTranslationResponse>,
  @Json(name = "privacyPreferencesTabLabel") val privacyPreferencesTabLabel: List<TextTranslationResponse>,
  @Json(name = "privacyCenterButton") val privacyCenterButton: List<TextTranslationResponse>,
  @Json(name = "poweredByCoiLabel") val poweredByCoiLabel: List<TextTranslationResponse>,
  @Json(name = "consentPreferencesLabel") val consentPreferencesLabel: List<TextTranslationResponse>,
  @Json(name = "requiredTableSectionHeader") val requiredTableSectionHeader: List<TextTranslationResponse>?,
  @Json(name = "optionalTableSectionHeader") val optionalTableSectionHeader: List<TextTranslationResponse>?,
  @Json(name = "readMoreScreenHeader") val readMoreScreenHeader: List<TextTranslationResponse>?
)
