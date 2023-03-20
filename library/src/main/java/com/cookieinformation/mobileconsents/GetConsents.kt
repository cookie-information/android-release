package com.cookieinformation.mobileconsents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.cookieinformation.mobileconsents.storage.ConsentPreferences
import com.cookieinformation.mobileconsents.ui.PrivacyActivity
import java.util.UUID

public class GetConsents(private val applicationContext: Context) :
  ActivityResultContract<Bundle?, Map<UUID, Boolean>>() {
  public override fun createIntent(context: Context, input: Bundle?): Intent =
    Intent(context, PrivacyActivity::class.java)

  override fun parseResult(resultCode: Int, intent: Intent?): Map<UUID, Boolean> {
    return ConsentPreferences(applicationContext).getAllConsentChoices()
  }
}