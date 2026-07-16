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
        val finished: Boolean = false,
    )

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
                    sdk.getLatestSavedUserConsents(userId).getOrThrow()
                }
            }
            _state.value = result.fold(
                // The privacy policy is a separate entry, not a toggleable category.
                onSuccess = { items ->
                    State(isLoading = false, items = items.filter { it.type != ConsentType.PRIVACY_POLICY })
                },
                onFailure = { State(isLoading = false) },
            )
        }
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
