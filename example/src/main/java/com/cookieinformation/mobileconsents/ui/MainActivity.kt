package com.cookieinformation.mobileconsents.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cookieinformation.mobileconsents.sdk.ui.ConsentsUISDK
import com.cookieinformation.mobileconsents.ui.ui.theme.SampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "Cookie Example"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ConsentsUISDK.init(
            clientID = "91cb67d4-9ebc-4c7e-974f-858d08b7ab7d",
            clientSecret = "a4fe5c7763ef5d8e395c9ef8c358c1f67c0876b6e7465c59ce9421d6ca67d02ba3d3a37f0351f320a2216878e4ddc570bdf6092a351b24067d536ded36a75946",
            solutionId = "a7a95d5e-e99b-44ff-8383-b7f6d1b720a8",
            context = this@MainActivity,
            languageCode = "en"
        )

        setContent {
            SampleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val coroutineScope = rememberCoroutineScope()

                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(16.dp)
                    ) {
                        Action(name = stringResource(R.string.display_always)) {
                            displayAlways(coroutineScope)
                        }

                        Action(name = stringResource(R.string.display_if_needed)) {
                            displayIfNeeded(coroutineScope)
                        }

                        Action(name = stringResource(R.string.delete_data)) {
                            deleteData(coroutineScope)
                        }
                    }
                }
            }
        }
    }

    private fun displayAlways(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            ConsentsUISDK.showPrivacyPopup(this@MainActivity).collect {
                it.fold(
                    onSuccess = { consentItems ->
                        consentItems.forEachIndexed { index, consentItem ->
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

    private fun displayIfNeeded(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            ConsentsUISDK.showPrivacyPopupIfNeeded(this@MainActivity).collect {
                it.fold(
                    onSuccess = { consentItems ->
                        consentItems.forEachIndexed { index, consentItem ->
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

    private fun deleteData(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
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

@Composable
fun Action(
    name: String, onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .height(70.dp)
            .padding(16.dp),
        onClick = { onClick() }
    ) {
        Text(text = name)
    }
}