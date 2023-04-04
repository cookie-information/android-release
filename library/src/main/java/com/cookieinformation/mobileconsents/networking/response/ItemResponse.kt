package com.cookieinformation.mobileconsents.networking.response

import android.util.Log
import com.cookieinformation.mobileconsents.ConsentItem
import com.cookieinformation.mobileconsents.ConsentItem.Type.Info
import com.cookieinformation.mobileconsents.ConsentItem.Type.Setting
import com.cookieinformation.mobileconsents.ConsentItem.Type.TypeFunctional
import com.cookieinformation.mobileconsents.ConsentItem.Type.TypeMarketing
import com.cookieinformation.mobileconsents.ConsentItem.Type.TypeNecessary
import com.cookieinformation.mobileconsents.ConsentItem.Type.TypeStatistical
import com.cookieinformation.mobileconsents.TextTranslation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Locale
import java.util.UUID


@JsonClass(generateAdapter = true)
internal data class ItemResponse(
  @Json(name = "translations") val translations: List<ConsentTranslationResponse>,
  @Json(name = "universalConsentItemId") val consentItemId: UUID,
  @Json(name = "required") val required: Boolean,
  @Json(name = "type") val type: String
)

internal fun ItemResponse.toDomain(): ConsentItem {
  val shortText = mutableListOf<TextTranslation>()
  val longText = mutableListOf<TextTranslation>()
  Log.d("TAG", "toDomain: storeConsentChoices: "+type.toDomainItemType().typeName)
  for (consentTranslation in translations) {
    shortText.add(TextTranslation(consentTranslation.language, consentTranslation.shortText))
    longText.add(TextTranslation(consentTranslation.language, consentTranslation.longText))
  }

  return ConsentItem(
    shortText = shortText,
    longText = longText,
    consentItemId = consentItemId,
    required = required,
    type = type.toDomainItemType()
  )
}

private fun String.toDomainItemType(): ConsentItem.Type =
  when (lowercase(Locale.getDefault())) {
    TypeNecessary.typeName-> TypeNecessary
    TypeMarketing.typeName -> TypeMarketing
    TypeStatistical.typeName -> TypeStatistical
    TypeFunctional.typeName -> TypeFunctional
    Info.typeName -> Info
    else -> Setting(this) // TODO Ensure if default value should be "SETTING"
  }
