package com.example.examplexml

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.sdk.ui.ConsentsUISDK
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val TAG = "Cookie Example"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConsentsUISDK.init(
            clientID = "91cb67d4-9ebc-4c7e-974f-858d08b7ab7d",
            clientSecret = "a4fe5c7763ef5d8e395c9ef8c358c1f67c0876b6e7465c59ce9421d6ca67d02ba3d3a37f0351f320a2216878e4ddc570bdf6092a351b24067d536ded36a75946",
            solutionId = "a7a95d5e-e99b-44ff-8383-b7f6d1b720a8",
            languageCode = "FR",
            typography = null,
            context = this@MainActivity
        )
        findViewById<Button>(R.id.button_display_always).setOnClickListener {
            displayAlways()
        }

        findViewById<Button>(R.id.button_display_if_needed).setOnClickListener {
            displayIfNeeded()
        }

        findViewById<Button>(R.id.button_delete_data).setOnClickListener {
            deleteData()
        }

    }

    private fun displayAlways() {
        lifecycleScope.launch {
            ConsentsUISDK.showPrivacyPopup(this@MainActivity).collect { result ->
                result.fold(
                    onSuccess = { consentItems ->
                        consentItems.forEach { consentItem ->
                            Log.d(TAG, "${consentItem.title}, ${consentItem.accepted}")
                        }
                    },
                    onFailure = { throwable ->
                        Log.d(TAG, throwable.message ?: "no message")
                    }
                )
            }
        }
    }

    private fun displayIfNeeded() {
        lifecycleScope.launch {
            ConsentsUISDK.showPrivacyPopupIfNeeded(this@MainActivity).collect { result ->
                result.fold(
                    onSuccess = { consentItems ->
                        consentItems.forEach { consentItem ->
                            Log.d(TAG, "${consentItem.title}, ${consentItem.accepted}")
                        }
                    },
                    onFailure = { throwable ->
                        Log.d(TAG, throwable.message ?: "no message")
                    }
                )
            }
        }
    }

    private fun deleteData() {
        lifecycleScope.launch {
            ConsentsUISDK.deleteLocalConsentsData(this@MainActivity).fold(
                onSuccess = {
                    Log.d(TAG, "Delete data success")
                },
                onFailure = { throwable ->
                    Log.d(TAG, throwable.message ?: "no message")
                }
            )
        }
    }
}
