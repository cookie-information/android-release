package com.cookieinformation.mobileconsents

import android.content.Context
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.adapter.extension.parseFromResponseBody
import com.cookieinformation.mobileconsents.adapter.moshi
import com.cookieinformation.mobileconsents.interfaces.CallFactory
import com.cookieinformation.mobileconsents.models.MobileConsentCustomUI
import com.cookieinformation.mobileconsents.networking.ConsentClient
import com.cookieinformation.mobileconsents.networking.extension.closeQuietly
import com.cookieinformation.mobileconsents.networking.extension.enqueueSuspending
import com.cookieinformation.mobileconsents.networking.response.ConsentSolutionResponseJsonAdapter
import com.cookieinformation.mobileconsents.networking.response.TokenResponse
import com.cookieinformation.mobileconsents.networking.response.TokenResponseJsonAdapter
import com.cookieinformation.mobileconsents.networking.response.toDomain
import com.cookieinformation.mobileconsents.storage.ConsentStorage
import com.cookieinformation.mobileconsents.system.ApplicationProperties
import com.cookieinformation.mobileconsents.ui.base.BaseConsentsView
import com.cookieinformation.mobileconsents.ui.LocaleProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/**
 * SDK allowing easy management of user consents.
 * @param consentClient wrapper over [OkHttpClient], enabling for fetching and posting consents.
 * @param consentStorage storage saving all consent choices to local file.
 * @param applicationProperties information about app using SDK, required for sending consent to server.
 * @param dispatcher dispatcher for executing background jobs.
 * @param saveConsentsFlow SharedFlow for observing "save consents" events.
 *
 * For SDK instantiating use [MobileConsentSdk.Builder] static function.
 */
@Suppress("UnusedPrivateMember", "LongParameterList")
public class MobileConsentSdk internal constructor(
  private val consentClient: ConsentClient,
  private val consentStorage: ConsentStorage,
  private val applicationProperties: ApplicationProperties,
  private val dispatcher: CoroutineDispatcher,
  public val saveConsentsFlow: SharedFlow<Map<UUID, Boolean>>,
  private val uiComponentColor: MobileConsentCustomUI?,
  private val uiLanguageCode: LocaleProvider,
  private val consentsView: BaseConsentsView?
) {

  public fun getClientId(): String = consentClient.clientId
  public fun getSecretId(): String = consentClient.clientSecret
  public fun getConsentSolutionId(): String = consentClient.consentSolutionId.toString()
  public fun getUiComponentColor(): MobileConsentCustomUI? = uiComponentColor
  public fun uiLanguageProvider(): LocaleProvider = uiLanguageCode
  public fun getConsentsView(): BaseConsentsView? = consentsView

  /**
   * Obtain [TokenResponse] from authentication server.
   * @returns [TokenResponse] obtained access token from authentication.
   * @throws [IOException] in case of any error.
   */
  public suspend fun fetchToken(): TokenResponse = withContext(dispatcher) {
    val call = consentClient.getToken()
    val responseBody = call.enqueueSuspending()
    val adapter = TokenResponseJsonAdapter(moshi)
    adapter.parseFromResponseBody(responseBody)
  }

  /**
   * Obtain [ConsentSolution] from CDN server.
   * @returns [ConsentSolution] obtained CDN from server.
   * @throws [IOException] in case of any error.
   */
  public suspend fun fetchConsentSolution(): ConsentSolution = withContext(dispatcher) {
    val call = consentClient.getConsentSolution()
    val responseBody = call.enqueueSuspending()
    val adapter = ConsentSolutionResponseJsonAdapter(moshi)
    adapter.parseFromResponseBody(responseBody).toDomain().also {
      consentStorage.saveConsentId(it.consentSolutionVersionId)
    }
  }

  /**
   * Post [Consent] to server specified by url in SDK's builder.
   * @param consent consent object.
   * @throws [IOException] in case of any error.
   */
  public suspend fun postConsent(consent: Consent): Unit = withContext(dispatcher) {
//    fetch a new token if the current token doesn't exist, or has expired.
    val token = consentStorage.tokenPreferences.getAccessToken()
    if (token == null) {
      val updateToken = fetchToken()
      consentStorage.tokenPreferences.setTokenResponse(updateToken)
    }
    val userId = consentStorage.getUserId()
    val call = consentClient.postConsent(consent, userId, applicationProperties)
    call.enqueueSuspending().closeQuietly()
    consentStorage.storeConsentChoices(consent.processingPurposes)
    consentStorage.saveConsentId(consent.consentSolutionVersionId)
  }

  public fun getLatestStoredConsentVersion(): UUID {
    return consentStorage.getLatestStoredConsentVersion()
  }

  /**
   * Obtain past consent choices stored on device memory.
   * @return returns Map of ConsentItem id and choice in a form of Boolean
   * @throws [IOExcepti\on] in case of any error.
   */
  public suspend fun getSavedConsents(): Map<UUID, Boolean> = withContext(dispatcher) {
    consentStorage.getAllConsentChoices()
  }

  /**
   * Reset past consent choices stored on device memory.
   */
  public suspend fun resetAllConsentChoices(): Unit = withContext(dispatcher) {
    consentStorage.resetAllConsentChoices()
  }

  /**
   * Reset past consent choices stored on device memory.
   */
  public suspend fun resetConsentChoice(choice: UUID): Unit = withContext(dispatcher) {
    consentStorage.resetAllConsentChoices(choice)
  }

  /**
   * Obtain specific Consent choice stored on device memory.
   * @return a value of user choice. If choice is not stored in memory, this will return `false`.
   * @throws [IOException] in case of any error.
   */
  public suspend fun getSavedConsent(consentItemId: UUID): Boolean = withContext(dispatcher) {
    consentStorage.getConsentChoice(consentItemId)
  }

  public companion object {
    /**
     * Use to instantiate SDK with all necessary parameters.
     * @return SDK builder instance.
     */
    @Suppress("FunctionNaming")
    @JvmStatic
    public fun Builder(context: Context): CallFactory = MobileConsentSdkBuilder(context)
  }
}
