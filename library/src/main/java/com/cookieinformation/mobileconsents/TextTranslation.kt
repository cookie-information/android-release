package com.cookieinformation.mobileconsents

import java.util.Locale

/**
 * Translation of [ConsentItem], with short and long text of consent (one of them cannot be empty).
 * @param languageCode code of transaction's language, e.g. "EN",
 * @param text text in a given language.
 */
public data class TextTranslation(
  val languageCode: String,
  val text: String
) {

  public companion object {

    @JvmField
    internal val DefaultLocale = Locale("en")

    /**
     * Finds the best matching translation for given list of locales.
     *
     * The function returns:
     * - First matching translation for locale's language if there is one.
     * - English translation if available.
     * - An empty translation otherwise.
     *
     * @param translations List of [TextTranslation].
     * @param preferredLocales List of preferred locales. The first one has the biggest priority.
     * @return The best matching translation
     */
    @JvmStatic
    public fun getTranslationFor(
      translations: List<TextTranslation>,
      preferredLocales: List<Locale>
    ): TextTranslation {
      if (translations.isEmpty()) return TextTranslation("", "")

      var translation: TextTranslation? = null
      for (locale in preferredLocales) {
        val languageCode = locale.language
        translation = translations.firstOrNull {
          Locale(it.languageCode).language.equals(languageCode, ignoreCase = true)
        }
        if (translation != null) break
      }
      if (translation == null) {
        translation =
          translations.firstOrNull {
            Locale(it.languageCode).language.equals(DefaultLocale.language, ignoreCase = true)
          }

        if (translation == null && BuildConfig.DEBUG){
          throw Exception("You must make sure the sdk language supports the same languages that the consentns are in")
        }
      }
      return translation ?: TextTranslation("", "")
    }
  }
}
