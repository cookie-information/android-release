package com.cookieinformation.mobileconsents.sdk.ui

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.lifecycle.lifecycleScope
import com.cookieinformation.mobileconsents.core.ConsentSDK
import com.cookieinformation.mobileconsents.sdk.ui.ui.ConsentsActivity
import com.cookieinformation.mobileconsents.sdk.ui.ui.CustomUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object ConsentsUISDK {
    lateinit var clientID: String
    lateinit var clientSecret: String
    lateinit var solutionId: String
    lateinit var languageCode: String
    internal var isLocaleOverriden: Boolean = false


    fun init(
        clientID: String,
        clientSecret: String,
        solutionId: String,
        languageCode: String? = null,
        customLightColorScheme: CustomColorScheme? = null,
        customDarkColorScheme: CustomColorScheme? = null,
        customTypography: CustomTypography? = null,
        context: Context
    ) {
        this.clientID = clientID
        this.clientSecret = clientSecret
        this.solutionId = solutionId
        this.languageCode =
            if (languageCode != null) languageCode.uppercase()
            else context.resources.configuration.locales.get(0).language.uppercase()
        this.isLocaleOverriden = languageCode != null
        CustomUI.setCustomUi(
            customLightColorScheme?.toLightColorScheme(),
            customDarkColorScheme?.toDarkColorScheme(),
            customTypography?.toTypography()
        )
    }

    fun init(
        clientID: String,
        clientSecret: String,
        solutionId: String,
        languageCode: String? = null,
        customLightColorScheme: ColorScheme? = null,
        customDarkColorScheme: ColorScheme? = null,
        typography: Typography?,
        context: Context
    ) {
        this.clientID = clientID
        this.clientSecret = clientSecret
        this.solutionId = solutionId
        this.languageCode = if (languageCode != null) languageCode.uppercase() else context.resources.configuration.locales.get(0).language.uppercase()
        this.isLocaleOverriden = languageCode != null

        CustomUI.setCustomUi(customLightColorScheme, customDarkColorScheme, typography)
    }

    private var savedConsentItemsFlow: MutableSharedFlow<Result<List<UIConsentItem>>> =
        MutableSharedFlow()

    fun getSavedItemsAsFlow():MutableSharedFlow<Result<List<UIConsentItem>>>  {
        savedConsentItemsFlow = MutableSharedFlow()
        return savedConsentItemsFlow
    }

    internal suspend fun postConsents(items: Result<List<UIConsentItem>>) {
        savedConsentItemsFlow.emit(items)
    }

    private fun resendAllFailedSaveConsentsRequests(callingActivity: ComponentActivity) {
        callingActivity.lifecycleScope.launch(Dispatchers.IO) {
            val sdk = ConsentSDK(
                callingActivity,
                clientID,
                clientSecret,
                solutionId,
                languageCode
            )

            sdk.init().onSuccess {
                sdk.resendAllFailedSaveConsentsRequests()
            }

        }
    }

    private suspend fun cacheLatestConsentSolution(callingActivity: ComponentActivity, userId: String?):Result<Unit> {
        return runCatching {
            val sdk = ConsentSDK(
                callingActivity,
                clientID,
                clientSecret,
                solutionId,
                languageCode
            )

            sdk.init().getOrThrow()
            sdk.cacheLatestConsentSolution().getOrThrow()
            val consents = sdk.getLatestSavedUserConsents(userId).getOrThrow()
            if (consents.isEmpty()) {
                throw IllegalStateException("There are no consent items in the database")
            }
        }
    }

    private suspend fun showPrivacyPopupNoResending(callingActivity: ComponentActivity,
                                                    userId: String? = null) : Flow<Result<List<UIConsentItem>>> {
        return cacheLatestConsentSolution(callingActivity, userId).fold(
            onSuccess = {
                val intent = Intent(callingActivity, ConsentsActivity::class.java)
                intent.putExtra("userId", userId)
                callingActivity.startActivity(intent)
                getSavedItemsAsFlow()
            },
            onFailure = {err ->
                flow {
                    emit(Result.failure(err))
                }
            }
        )
    }

    suspend fun showPrivacyPopup(
        callingActivity: ComponentActivity,
        userId: String? = null
    ): Flow<Result<List<UIConsentItem>>> {
        resendAllFailedSaveConsentsRequests(callingActivity) // execute this on the background

        return showPrivacyPopupNoResending(callingActivity, userId)
    }

    suspend fun showPrivacyPopupIfNeeded(
        callingActivity: ComponentActivity,
        userId: String? = null
    ): Flow<Result<List<UIConsentItem>>> {
        resendAllFailedSaveConsentsRequests(callingActivity) // execute this on the background

        return cacheLatestConsentSolution(callingActivity, userId).fold(
            onSuccess = {
                shouldDisplayConsents(callingActivity, userId).fold(
                    onSuccess = {
                        if (it) {
                            showPrivacyPopupNoResending(callingActivity, userId)
                        } else {
                            val sdk = ConsentSDK(
                                callingActivity,
                                clientID,
                                clientSecret,
                                solutionId,
                                languageCode
                            )

                            sdk.init().fold(onSuccess = {
                                val items = sdk.getLatestSavedUserConsents(userId).map {
                                    it.map { item-> item.toUIConsentItem() }
                                }

                                flow {
                                    emit(items)
                                }
                            }, onFailure = { err ->
                                flow {
                                    emit(Result.failure(err))
                                }
                            })
                        }

                    }, onFailure = { err ->
                        flow {
                            emit(Result.failure(err))
                        }
                    }
                )
            },
            onFailure = {
                flow {
                    emit(Result.failure(it))
                }
            }
        )

    }


    fun deleteLocalConsentsData(context: Context, userId: String? = null):Result<Unit> {
        val sdk = ConsentSDK(
            context,
            clientID,
            clientSecret,
            solutionId,
            languageCode
        )

        return runCatching {
            sdk.init().getOrThrow()
            sdk.deleteUserData(userId).getOrThrow()
        }
    }

    /**
     * Should display consents
     *
     * @param userId the userid, that is checked to see if they need to consent again,
     * either if they have not consented or, the version has changed.
     * @return  Boolean true, if user needs to consent, false, if user has consented already.
     */
    internal fun shouldDisplayConsents(context: Context, userId: String?): Result<Boolean> {
        val sdk = ConsentSDK(
            context,
            clientID,
            clientSecret,
            solutionId,
            languageCode
        )
        return sdk.init().fold(onSuccess = {
            sdk.hasUserRespondedToLatestSavedSolution(userId).map { !it }
        }, onFailure = {
            Result.failure(it)
        })
    }


}