package com.cookieinformation.mobileconsents.storage

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

internal class ConsentPreferences(private val applicationContext: Context) {

  private fun consentsMetaPreferences(): SharedPreferences {
    return applicationContext.getSharedPreferences(CONSENT_META_PREFERENCES, Context.MODE_PRIVATE)
  }

  internal fun usersConsentsPreferences(): SharedPreferences {
    return applicationContext.getSharedPreferences(USER_CONSENT_PREFERENCES, Context.MODE_PRIVATE)
  }

  fun getLatestStoredConsentVersion() = consentsMetaPreferences().getString(LATEST_CONSENT_ID, "")

  fun saveLatestStoredConsentVersion(consentId: UUID) {
    consentsMetaPreferences().edit().putString(LATEST_CONSENT_ID, consentId.toString()).commit()
  }

  fun getAllConsentChoices(): Map<UUID, Boolean> {
    return usersConsentsPreferences().all.mapKeys {
      UUID.fromString(it.key)
    }.mapValues { it.value as? Boolean ?: false }
  }

  fun resetAllConsentChoices() {
    getAllConsentChoices().forEach {
      usersConsentsPreferences().edit().putBoolean(it.key.toString(), false).apply()
    }
  }

  fun resetConsentChoice(consentType: UUID) {
    usersConsentsPreferences().edit().putBoolean(consentType.toString(), false).apply()
  }

  companion object {
//    private const val CONSENT_PREFERENCES = "consent_preference"
    private const val USER_CONSENT_PREFERENCES = "user_consent_preference"
    private const val CONSENT_META_PREFERENCES = "consent_meta_preference"

    private const val LATEST_CONSENT_ID = "consent_preference_version_key"
  }
}