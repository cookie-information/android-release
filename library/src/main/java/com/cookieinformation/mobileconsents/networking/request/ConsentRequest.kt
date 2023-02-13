package com.cookieinformation.mobileconsents.networking.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
internal data class ConsentRequest(
  @Json(name = "userId") val userId: UUID,
  @Json(name = "universalConsentSolutionId") val consentSolutionId: UUID,
  @Json(name = "universalConsentSolutionVersionId") val consentSolutionVersionId: UUID,
//  @Json(name = "timestamp") val timestamp: String,
  @Json(name = "processingPurposes") val processingPurposes: List<ProcessingPurposeRequest>,
  @Json(name = "customData") val customData: List<CustomDataRequest>,
  @Json(name = "platformInformation") val applicationProperties: ApplicationPropertiesRequest,
)
