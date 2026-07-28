package com.cookieinformation.mobileconsents.custom.consent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItem
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItemOption
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Loads consents from CORE and saves the user's choices back to it. */
internal class ConsentViewModel(application: Application) : AndroidViewModel(application) {

    data class State(
        val isLoading: Boolean = true,
        val items: List<ConsentItem> = emptyList(),
        val privacyPolicy: String? = null,
        val finished: Boolean = false,
    )

    private data class Loaded(val categories: List<ConsentItem>, val privacyPolicy: String?)

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun load(userId: String?) {
        viewModelScope.launch {
            _state.value = State(isLoading = true)
            val result = withContext(Dispatchers.IO) {
                val sdk = CookieConsent.newSdk(getApplication())
                runCatching {
                    sdk.init().getOrThrow()
                    sdk.resendAllFailedSaveConsentsRequests()
                    sdk.cacheLatestConsentSolution().getOrThrow()
                    val all = sdk.getLatestSavedUserConsents(userId).getOrThrow()
                    // Categories, ordered Necessary, Functional, Statistical, Marketing, then the rest.
                    val categories = all
                        .filter { it.type != ConsentType.PRIVACY_POLICY }
                        .sortedBy { categoryOrder(it.type) }
                    check(categories.isNotEmpty()) { "No consent categories available" }
                    // Privacy policy content comes from the panel (same source the built-in UI uses):
                    // the description of the PRIVACY_POLICY entry (a URL or HTML). It is opened as-is
                    // in the in-app WebView.
                    val privacyPolicy = all
                        .firstOrNull { it.type == ConsentType.PRIVACY_POLICY }
                        ?.description
                        ?.takeIf { it.isNotBlank() }
                    Loaded(categories, privacyPolicy)
                }
            }
            result.fold(
                onSuccess = {
                    _state.value = State(isLoading = false, items = it.categories, privacyPolicy = it.privacyPolicy)
                },
                onFailure = { error ->
                    // Nothing to show: report the error to the caller and close, like the built-in UI.
                    CookieConsent.publishResult(Result.failure(error))
                    _state.value = _state.value.copy(isLoading = false, finished = true)
                },
            )
        }
    }

    private fun categoryOrder(type: ConsentType): Int = when (type) {
        ConsentType.NECESSARY -> 0
        ConsentType.FUNCTIONAL -> 1
        ConsentType.STATISTICS -> 2
        ConsentType.MARKETING -> 3
        else -> 4
    }

    fun save(userId: String?, options: List<ConsentItemOption>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = withContext(Dispatchers.IO) {
                val sdk = CookieConsent.newSdk(getApplication())
                runCatching {
                    sdk.init().getOrThrow()
                    sdk.saveConsents(userId, options).getOrThrow()
                }
            }
            _state.value = _state.value.copy(isLoading = false, finished = true)
            CookieConsent.publishResult(result) // hand the saved consents back to the caller
        }
    }
}
