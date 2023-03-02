package com.cookieinformation.mobileconsents.networking.response

import android.util.Log
import com.cookieinformation.mobileconsents.ConsentSolution
import com.cookieinformation.mobileconsents.UiTexts
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
internal data class ConsentSolutionResponse(
  @Json(name = "universalConsentItems") val consentItems: List<ItemResponse>,
  @Json(name = "universalConsentSolutionId") val consentSolutionId: UUID,
  @Json(name = "universalConsentSolutionVersionId") val consentSolutionVersionId: UUID,
  @Json(name = "title") val title: List<TextTranslationResponse>, // Is empty in response
  @Json(name = "description") val description: List<TextTranslationResponse>, // Is empty in response
  @Json(name = "templateTexts") val templateTexts: TemplateTextsResponse,
)

internal fun ConsentSolutionResponse.toDomainUiTexts() =
  UiTexts(
    privacyPreferencesTitle = title.map(TextTranslationResponse::toDomain),
    privacyPreferencesDescription = description.map(TextTranslationResponse::toDomain),
    privacyPreferencesTabLabel = templateTexts.privacyPreferencesTabLabel.map(TextTranslationResponse::toDomain),
    privacyCenterButton = templateTexts.privacyCenterButton.map(TextTranslationResponse::toDomain),
    acceptAllButton = templateTexts.acceptAllButton.map(TextTranslationResponse::toDomain),
    rejectAllButton = templateTexts.rejectAllButton.map(TextTranslationResponse::toDomain),
    acceptSelectedButton = templateTexts.acceptSelectedButton.map(TextTranslationResponse::toDomain),
    savePreferencesButton = templateTexts.savePreferencesButton.map(TextTranslationResponse::toDomain),
    privacyCenterTitle = templateTexts.privacyCenterTitle.map(TextTranslationResponse::toDomain),
    poweredByLabel = templateTexts.poweredByCoiLabel.map(TextTranslationResponse::toDomain),
    consentPreferencesLabel = templateTexts.consentPreferencesLabel.map(TextTranslationResponse::toDomain),
  )

internal fun ConsentSolutionResponse.toDomain(): ConsentSolution {
  return ConsentSolution(
    consentItems = consentItems.map(ItemResponse::toDomain),
    consentSolutionId = consentSolutionId,
    consentSolutionVersionId = consentSolutionVersionId,
    uiTexts = toDomainUiTexts()
  )
}

