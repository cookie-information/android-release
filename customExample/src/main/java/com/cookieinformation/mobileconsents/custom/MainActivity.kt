package com.cookieinformation.mobileconsents.custom

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItem
import com.cookieinformation.mobileconsents.custom.consent.BrandBlue
import com.cookieinformation.mobileconsents.custom.consent.CookieConsent
import kotlinx.coroutines.launch

/**
 * Demo launcher. Shows how to use the copy-paste `consent` package:
 * configure once, then trigger the screen from anywhere.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CookieConsent.configure(
            clientId = CLIENT_ID,
            clientSecret = CLIENT_SECRET,
            solutionId = SOLUTION_ID,
            language = "EN",
            cookiePolicyUrl = "https://example.com/cookie-policy",
        )

        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            val deletedMessage = stringResource(R.string.msg_data_deleted)
            val deleteFailedMessage = stringResource(R.string.msg_delete_failed)
            var darkMode by rememberSaveable { mutableStateOf(false) }

            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = BrandBlue,
                    onPrimary = Color.White,
                    background = Color.White,
                    surface = Color.White,
                ),
            ) {
                HomeScreen(
                    darkMode = darkMode,
                    onDarkModeChange = { darkMode = it },
                    onShowConsents = {
                        scope.launch {
                            CookieConsent.show(this@MainActivity, darkTheme = darkMode).collect(::logConsents)
                        }
                    },
                    onShowIfNeeded = {
                        scope.launch { CookieConsent.showIfNeeded(this@MainActivity, darkTheme = darkMode).collect(::logConsents) }
                    },
                    onDeleteData = {
                        scope.launch {
                            val message = CookieConsent.deleteData(context).fold(onSuccess = { deletedMessage }, onFailure = { deleteFailedMessage })
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }
        }
    }

    /** Example of consuming the saved consents returned by the screen. */
    private fun logConsents(result: Result<List<ConsentItem>>) {
        result.fold(
            onSuccess = { items -> items.forEach { Log.d(TAG, "${it.title}: ${it.accepted}") } },
            onFailure = { Log.d(TAG, it.message ?: "no message") },
        )
    }

    private companion object {
        const val TAG = "CookieConsent"
        const val CLIENT_ID = "91cb67d4-9ebc-4c7e-974f-858d08b7ab7d"
        const val CLIENT_SECRET = "a4fe5c7763ef5d8e395c9ef8c358c1f67c0876b6e7465c59ce9421d6ca67d02ba3d3a37f0351f320a2216878e4ddc570bdf6092a351b24067d536ded36a75946"
        const val SOLUTION_ID = "a7a95d5e-e99b-44ff-8383-b7f6d1b720a8"
    }
}
