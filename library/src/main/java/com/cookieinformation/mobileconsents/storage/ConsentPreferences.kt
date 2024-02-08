package com.cookieinformation.mobileconsents.storage

import android.content.Context
import android.content.SharedPreferences
import com.cookieinformation.mobileconsents.ConsentItem
import com.cookieinformation.mobileconsents.ConsentItem.Type
import java.util.UUID

internal class ConsentPreferences(private val applicationContext: Context) {

  fun sharedPreferences(): SharedPreferences {
    return applicationContext.getSharedPreferences(CONSENT_PREFERENCES, Context.MODE_PRIVATE)
  }

  private fun consentsMetaPreferences(): SharedPreferences {
    return applicationContext.getSharedPreferences(CONSENT_META_PREFERENCES, Context.MODE_PRIVATE)
  }

  internal fun usersConsentsPreferences(): SharedPreferences {
    return applicationContext.getSharedPreferences(USER_CONSENT_PREFERENCES, Context.MODE_PRIVATE)
  }

  internal fun consentsTypePreferences(): SharedPreferences {
    return applicationContext.getSharedPreferences(CONSENTS_TYPE_PREFERENCES, Context.MODE_PRIVATE)
  }

  fun getLatestStoredConsentVersion() = consentsMetaPreferences().getString(LATEST_CONSENT_ID, "")

  fun saveLatestStoredConsentVersion(consentId: UUID) {
    consentsMetaPreferences().edit().putString(LATEST_CONSENT_ID, consentId.toString()).commit()
  }

  fun getOldAllConsentChoices(): Map<Type, Boolean> {
    return sharedPreferences().all.mapKeys {
      Type.findTypeByValue(it.key)
    }.mapValues { it.value as? Boolean ?: false }
  }

  fun getAllConsentChoices(): Map<UUID, Boolean> {
    return usersConsentsPreferences().all.mapKeys {
      UUID.fromString(it.key)
    }.mapValues { it.value as? Boolean ?: false }
  }

  fun getAllConsentChoicesWithType(): Map<UUID, ConsentWithType> {
    return consentsTypePreferences().all.toList().associate {
      val consented: Boolean = usersConsentsPreferences().all.toList().firstOrNull { const-> const.first == it.first }?.second as? Boolean ?: false
      UUID.fromString(it.first) to
          ConsentWithType(Type.findTypeByValue(it.second as String), consented) }
  }

  fun resetAllConsentChoices() {
    val editor =  usersConsentsPreferences().edit()
    usersConsentsPreferences().all.toList().forEach {
      editor.putBoolean(it.first, false)
    }
    editor.commit()
  }

  fun resetConsentChoice(consentType: UUID) {
    usersConsentsPreferences().edit().putBoolean(consentType.toString(), false).apply()
  }

  companion object {
    private const val CONSENT_PREFERENCES = "consent_preference"
    private const val USER_CONSENT_PREFERENCES = "user_consent_preference"
    private const val CONSENTS_TYPE_PREFERENCES = "consent_type_preference"
    private const val CONSENT_META_PREFERENCES = "consent_meta_preference"

    private const val LATEST_CONSENT_ID = "consent_preference_version_key"
  }
}