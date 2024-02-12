package com.example.sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.GetConsents

class MainActivity : AppCompatActivity() {

  private val listener = registerForActivityResult(
    GetConsents(this),
  ) {
    it.entries.forEach { entry ->
      Log.d("Show Entry", "${entry.key}: ${entry.value.type}: ${entry.value.consented}")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.display_always).setOnClickListener {
      (applicationContext as App).sdk.displayConsents(listener)
    }

    findViewById<Button>(R.id.display_if_needed).setOnClickListener {
      (applicationContext as App).sdk.displayConsentsIfNeeded(listener) {
        Toast.makeText(this, it.message.orEmpty(), Toast.LENGTH_SHORT).show()
      }
    }

    findViewById<Button>(R.id.reset_all_consents).setOnClickListener {
      lifecycleScope.launchWhenCreated {
        (applicationContext as App).sdk.resetAllConsentChoices()
      }
    }

    findViewById<Button>(R.id.get_with_type).setOnClickListener {
      lifecycleScope.launchWhenCreated {
        val storedConsents = (applicationContext as App).sdk.getMobileConsentSdk().getSavedConsentsWithType()
        storedConsents.entries.forEachIndexed { index, entry ->
          Log.d("TAG", "onSuccess: " + entry.key +"  "+entry.value)
        }
      }
    }
  }
}