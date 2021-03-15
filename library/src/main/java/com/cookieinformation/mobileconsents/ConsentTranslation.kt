package com.cookieinformation.mobileconsents

/**
 * Translation of [ConsentItem], with short and long text of consent (one of them cannot be empty).
 * @param languageCode code of transaction's language, e.g. "EN",
 * @param shortText consent's short explanation.
 * @param longText consent's detailed explanation.
 */
public data class ConsentTranslation(
  val languageCode: String,
  val longText: String,
  val shortText: String
)