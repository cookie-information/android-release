package com.example.sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cookieinformation.mobileconsents.GetConsents

class MainActivity : AppCompatActivity() {

  private val listener = registerForActivityResult(
    GetConsents(this),
  ) {
    it.entries.forEach { entry ->
      Log.d("Show Entry", "${entry.key.name}: ${entry.value}")
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
  }
}