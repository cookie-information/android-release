package com.example.sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.GetConsents

class MainActivity : AppCompatActivity() {

  private val listener = registerForActivityResult(
    GetConsents(this),
  ) {
    it.entries.forEach { entry ->
      Log.d("Show Entry", "${entry.key}: ${entry.value}")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.display_always).setOnClickListener {
      (applicationContext as App).sdk.displayConsents(listener)
    }

    findViewById<Button>(R.id.display_if_needed).setOnClickListener {
      (applicationContext as App).sdk.displayConsentsIfNeeded(listener)
    }

    findViewById<Button>(R.id.reset_all_consents).setOnClickListener {
      lifecycleScope.launchWhenCreated {
        (applicationContext as App).sdk.resetAllConsentChoices()
        (applicationContext as App).sdk.displayConsents(listener)
      }
    }

    findViewById<Button>(R.id.reset_specific_consent).setOnClickListener {
      lifecycleScope.launchWhenCreated {
        val storedConsents = (applicationContext as App).sdk.getMobileConsentSdk().getSavedConsents()
        storedConsents.entries.forEachIndexed { index, entry ->
          if (index == 1) {
            (applicationContext as App).sdk.resetConsentChoice(entry.key)
            (applicationContext as App).sdk.displayConsents(listener)
          }
        }
      }
    }
  }
}