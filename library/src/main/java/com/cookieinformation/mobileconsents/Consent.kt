package com.cookieinformation.mobileconsents

import com.cookieinformation.mobileconsents.networking.request.ConsentRequest
import com.cookieinformation.mobileconsents.networking.request.CustomDataRequest
import com.cookieinformation.mobileconsents.system.ApplicationProperties
import com.cookieinformation.mobileconsents.system.toRequest
import java.util.UUID

/**
 * Consent sent to partner's server after user interaction.
 * @param consentSolutionId UUID of consent solution.
 * @param consentSolutionVersionId UUID of consent solution version.
 * @param processingPurposes list of all consents given (or denied) by user.
 * @param customData custom data provided by developer, can be any map of Strings, e.g. {"email":"example@example.com"}
 */
public data class Consent(
  val consentSolutionId: UUID,
  val consentSolutionVersionId: UUID,
  val processingPurposes: List<ProcessingPurpose>,
  val customData: Map<String, String>,
)

internal fun Consent.toRequest(
  userId: UUID,
  timestamp: String,
  applicationProperties: ApplicationProperties
) = ConsentRequest(
  userId = userId,
  consentSolutionId = consentSolutionId,
  consentSolutionVersionId = consentSolutionVersionId,
//  timestamp = timestamp,
  processingPurposes = processingPurposes.map(ProcessingPurpose::toRequest),
  customData = customData.map { CustomDataRequest(it.key, it.value) },
  applicationProperties = applicationProperties.toRequest()
)
