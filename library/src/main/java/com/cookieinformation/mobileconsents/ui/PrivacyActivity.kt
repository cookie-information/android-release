package com.cookieinformation.mobileconsents.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.Consentable
import com.cookieinformation.mobileconsents.R

internal class PrivacyActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_privacy)

    supportFragmentManager.commit {
      replace(R.id.fragment_container, PrivacyFragment.newInstance())
    }
  }

  override fun onBackPressed() {}
}