package com.cookieinformation.mobileconsents.custom.consent

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.core.ConsentSDK
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Drop-in custom consent screen backed by the Cookie Information CORE SDK.
 *
 * Integration:
 *   1. Add: implementation("com.cookieinformation:core:1.0.0")
 *   2. Copy this `consent` package into your project.
 *   3. Register the screen in your AndroidManifest, inside <application>:
 *        <activity android:name=".consent.ConsentActivity" android:exported="false" />
 *   4. Call [configure] once, then [show] / [showIfNeeded] and collect the returned flow
 *      to receive the user's saved consents.
 */
object CookieConsent {

    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var solutionId: String
    private var language: String? = null

    // Emits the saved consents (or an error) once the screen closes.
    private var resultFlow = MutableSharedFlow<Result<List<ConsentItem>>>(extraBufferCapacity = 1)

    fun configure(
        clientId: String,
        clientSecret: String,
        solutionId: String,
        language: String? = null,
    ) {
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.solutionId = solutionId
        this.language = language
    }

    /**
     * Show the consent screen. Collect the returned flow to receive the saved consents:
     *
     *     CookieConsent.show(activity).collect { result ->
     *         result.fold(onSuccess = { … }, onFailure = { … })
     *     }
     */
    fun show(
        activity: ComponentActivity,
        userId: String? = null,
        darkTheme: Boolean? = null,
    ): SharedFlow<Result<List<ConsentItem>>> {
        val flow = newResultFlow()
        activity.startActivity(intent(activity, userId, darkTheme))
        return flow
    }

    /**
     * Show the consent screen only if the user has not responded to the latest solution.
     * If it isn't needed, the flow emits the already-saved consents. The check runs before
     * the screen is launched, so nothing appears when it isn't needed.
     */
    fun showIfNeeded(
        activity: ComponentActivity,
        userId: String? = null,
        darkTheme: Boolean? = null,
    ): SharedFlow<Result<List<ConsentItem>>> {
        val flow = newResultFlow()
        activity.lifecycleScope.launch {
            if (isConsentNeeded(activity, userId)) {
                activity.startActivity(intent(activity, userId, darkTheme))
            } else {
                resultFlow.tryEmit(latestSavedConsents(activity, userId))
            }
        }
        return flow
    }

    /** Remove all locally stored consent data for the user. */
    suspend fun deleteData(context: Context, userId: String? = null): Result<Unit> =
        withContext(Dispatchers.IO) {
            val sdk = newSdk(context)
            runCatching {
                sdk.init().getOrThrow()
                sdk.deleteUserData(userId).getOrThrow()
                Unit
            }
        }

    internal fun newSdk(context: Context): ConsentSDK {
        val resolvedLanguage = language ?: context.resources.configuration.locales.get(0).language.uppercase()
        return ConsentSDK(context, clientId, clientSecret, solutionId, resolvedLanguage)
    }

    /** Called by the screen after a save; delivers the result to the current flow. */
    internal fun publishResult(result: Result<List<ConsentItem>>) {
        resultFlow.tryEmit(result)
    }

    private fun newResultFlow(): SharedFlow<Result<List<ConsentItem>>> {
        resultFlow = MutableSharedFlow(extraBufferCapacity = 1)
        return resultFlow.asSharedFlow()
    }

    private suspend fun isConsentNeeded(context: Context, userId: String?): Boolean =
        withContext(Dispatchers.IO) {
            val sdk = newSdk(context)
            runCatching {
                sdk.init().getOrThrow()
                sdk.cacheLatestConsentSolution().getOrThrow()
                !sdk.hasUserRespondedToLatestSavedSolution(userId).getOrThrow()
            }.getOrDefault(true)
        }

    private suspend fun latestSavedConsents(context: Context, userId: String?): Result<List<ConsentItem>> =
        withContext(Dispatchers.IO) {
            val sdk = newSdk(context)
            runCatching {
                sdk.init().getOrThrow()
                sdk.getLatestSavedUserConsents(userId).getOrThrow()
            }
        }

    private fun intent(context: Context, userId: String?, darkTheme: Boolean?) =
        Intent(context, ConsentActivity::class.java).apply {
            putExtra(ConsentActivity.EXTRA_USER_ID, userId)
            if (darkTheme != null) putExtra(ConsentActivity.EXTRA_DARK_THEME, darkTheme)
        }
}
