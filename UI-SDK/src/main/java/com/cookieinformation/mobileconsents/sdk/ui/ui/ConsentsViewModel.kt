package com.cookieinformation.mobileconsents.sdk.ui.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cookieinformation.mobileconsents.core.ConsentSDK
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItemOption
import com.cookieinformation.mobileconsents.sdk.ui.ConsentsUISDK
import com.cookieinformation.mobileconsents.sdk.ui.toUIConsentItem
import com.cookieinformation.mobileconsents.sdk.ui.ui.data.ConsentsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [ConsentsViewModel] holds information about the consents related to the currently selected user..
 */
class ConsentsViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ConsentsUiState())
    val uiState: StateFlow<ConsentsUiState> = _uiState.asStateFlow()

    suspend fun canNavigateBack(userName: String?):Result<Boolean> {
        val sdk = ConsentSDK(
            getApplication(),
            ConsentsUISDK.clientID,
            ConsentsUISDK.clientSecret,
            ConsentsUISDK.solutionId,
            ConsentsUISDK.languageCode
        )

        return runCatching {
            try {
                sdk.init().getOrThrow()
                val canNavigateBack = sdk.hasUserRespondedToLatestSavedSolution(userName).getOrThrow()
                val consents = sdk.getLatestSavedUserConsents(userName).getOrThrow()
                if (consents.isEmpty()) {
                    throw IllegalStateException("There are no consent items in the database")
                }

                if (canNavigateBack) {
                    ConsentsUISDK.postConsents(Result.success(consents.map { it.toUIConsentItem() }))
                    true
                }
                else {
                    false
                }
            }
            catch (e: Exception) {
                ConsentsUISDK.postConsents(Result.failure(e))

                throw e
            }

        }
    }

    suspend fun getConsents(userName: String?):Result<Unit> {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val sdk = ConsentSDK(
            getApplication(),
            ConsentsUISDK.clientID,
            ConsentsUISDK.clientSecret,
            ConsentsUISDK.solutionId,
            ConsentsUISDK.languageCode
        )

        return runCatching {
            try {
                sdk.init().getOrThrow()
                val consents = sdk.getLatestSavedUserConsents(userName).getOrThrow()
                if (consents.isEmpty()) {
                    throw IllegalStateException("There are no consent items in the database")
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    consents = consents
                )

            }
            catch (e: Exception) {
                ConsentsUISDK.postConsents(Result.failure(e))

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    consents = emptyList()
                )

                throw e
            }
        }
    }

    suspend fun saveConsents(userName: String?, items: Map<Long, Boolean>): Result<Unit> {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val consents = _uiState.value.consents.map {
            it.copy(accepted = items[it.id] ?: false)
        }
        val mappedConsents = consents.map { ConsentItemOption(it.id, it.accepted) }
        val sdk = ConsentSDK(
            getApplication(),
            ConsentsUISDK.clientID,
            ConsentsUISDK.clientSecret,
            ConsentsUISDK.solutionId,
            ConsentsUISDK.languageCode
        )

        return runCatching {
            try {

                sdk.init().getOrThrow()
                val savedConsents = sdk.saveConsents(
                    userName,
                    mappedConsents
                ).getOrThrow()

                ConsentsUISDK.postConsents(Result.success(savedConsents.map { it.toUIConsentItem() }))

                _uiState.value = _uiState.value.copy(isLoading = false)
            }
            catch (e: Exception) {
                ConsentsUISDK.postConsents(Result.failure(e))

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                )

                throw e
            }
        }
    }
}
