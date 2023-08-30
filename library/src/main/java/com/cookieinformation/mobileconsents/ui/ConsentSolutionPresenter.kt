package com.cookieinformation.mobileconsents.ui

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import com.cookieinformation.mobileconsents.Consent
import com.cookieinformation.mobileconsents.ConsentItem
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.ConsentSolution
import com.cookieinformation.mobileconsents.MobileConsentSdk
import com.cookieinformation.mobileconsents.ProcessingPurpose
import com.cookieinformation.mobileconsents.R
import com.cookieinformation.mobileconsents.TextTranslation
import com.cookieinformation.mobileconsents.storage.Preferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import java.util.UUID

internal typealias GivenConsent = Map<UUID, Pair<Boolean, String>>

/**
 * Base presenter for the view that displays [ConsentSolution] and allows the user to chose and save the consents.
 */
internal abstract class ConsentSolutionPresenter<ViewType, ViewDataType, ViewIntentListenerType>(
  dispatcher: CoroutineDispatcher
) where ViewType : ConsentSolutionView<ViewDataType, ViewIntentListenerType> {

  protected val cookieInformationUrl = "https://cookieinformation.com"

  protected sealed class ViewState {

    object Idle : ViewState()

    object Authenticating : ViewState()

    object AuthenticateError : ViewState()

    object Authenticated : ViewState()

    object Fetching : ViewState()

    object FetchError : ViewState()

    data class Fetched<VD>(
      val data: VD,
      val consentSolution: ConsentSolution,
    ) : ViewState()

    data class Sending<VD>(
      val data: VD,
      val consentSolution: ConsentSolution,
    ) : ViewState()

    data class SendError<VD>(
      val data: VD,
      val consentSolution: ConsentSolution,
    ) : ViewState()
  }

  private var view: ViewType? = null

  private lateinit var applicationContext: Context
  private lateinit var consentSdk: MobileConsentSdk
  private lateinit var localeProvider: LocaleProvider
  private lateinit var preferences: Preferences
  private val locales: List<Locale>
    get() = localeProvider.getLocales()

  protected var listener: ConsentSolutionListener? = null
    private set

  private val scope = CoroutineScope(dispatcher)

  @Suppress("UNCHECKED_CAST")
  protected var viewState: ViewState = ViewState.Idle
    set(value) {
      field = value
      view?.let { view ->
        when (value) {
          ViewState.Idle -> Unit
          ViewState.Authenticating, ViewState.Fetching -> handleLoading(view)
          ViewState.Authenticated -> handleAuthenticated(view)
          ViewState.AuthenticateError -> handleAuthenticateError(view)
          is ViewState.Fetched<*> -> handleFetched(view, value as ViewState.Fetched<ViewDataType>)
          ViewState.FetchError -> handleFetchError(view)
          is ViewState.Sending<*> -> handleSending(view, value as ViewState.Sending<ViewDataType>)
          is ViewState.SendError<*> -> handleSendError(view, value as ViewState.SendError<ViewDataType>)
        }
      }
    }

  private fun handleAuthenticated(view: ViewType) = with(view) {
    hideProgressBar()
  }

  private fun handleAuthenticateError(view: ViewType) = with(view) {
    hideProgressBar()
    showRetryDialog(
      onRetry = ::fetchToken,
      onDismiss = { listener?.onDismissed() },
      applicationContext.getString(R.string.mobileconsents_privacy_preferences_title_error_token),
      applicationContext.getString(R.string.mobileconsents_privacy_preferences_msg_error_token)
    )
  }

  private fun handleLoading(view: ViewType) = with(view) {
    showProgressBar()
  }

  private fun handleFetched(view: ViewType, state: ViewState.Fetched<ViewDataType>) = with(view) {
    hideProgressBar()
    showViewData(state.data)
  }

  private fun handleFetchError(view: ViewType) = with(view) {
    hideProgressBar()
    showRetryDialog(
      onRetry = ::fetchConsentSolution,
      onDismiss = { listener?.onDismissed() },
      applicationContext.getString(R.string.mobileconsents_privacy_preferences_title_error_fetch),
      applicationContext.getString(R.string.mobileconsents_privacy_preferences_msg_error_fetch)
    )
  }

  private fun handleSending(view: ViewType, state: ViewState.Sending<ViewDataType>) = with(view) {
    showViewData(state.data)
    showProgressBar()
  }

  private fun handleSendError(view: ViewType, state: ViewState.SendError<ViewDataType>) = with(view) {
    hideProgressBar()
    showViewData(state.data)
    showErrorDialog {
      val currentViewState = viewState
      require(currentViewState is ViewState.SendError<*>)
      viewState = ViewState.Fetched(currentViewState.data, currentViewState.consentSolution)
    }
  }

  /**
   * Attaches the view to the presenter.
   */
  @MainThread
  fun attachView(view: ViewType) {
    require(this.view == null)
    this.view = view
    view.addIntentListener(getViewIntentListener())
    viewState = viewState // force update view
  }

  /**
   * Detaches curentlly attached view from the presenter.
   */
  @MainThread
  fun detachView() {
    requireNotNull(view)
    view?.removeIntentListener(getViewIntentListener())
    view = null
  }

  /**
   * Initializes the presenter.
   *
   * @param consentSdk instance of the [MobileConsentSdk].
   * @param localeProvider implementation of the [LocaleProvider].
   * @param listener implementation of the presenters event handler - [ConsentSolutionListener].
   */
  @MainThread
  fun initialize(
    applicationContext: Context,
    consentSdk: MobileConsentSdk,
    localeProvider: LocaleProvider,
    listener: ConsentSolutionListener
  ) {
    this.applicationContext = applicationContext
    this.consentSdk = consentSdk
    this.localeProvider = localeProvider
    this.listener = listener
    preferences = Preferences(applicationContext)
    observeConsents()
  }

  private fun observeConsents() {
    consentSdk.saveConsentsFlow
      .onEach {
        // For the presenter, changes are only useful when data are shown. Loading states can be ignored
        when (val currentViewState = viewState) {
          is ViewState.Fetched<*> -> {
            onConsentsChangedWhileFetched(it)
            listener?.onConsentsChosen(currentViewState.consentSolution, it, true)
          }
          is ViewState.SendError<*> -> {
            onConsentsChangedWhileSendError(it)
            listener?.onConsentsChosen(currentViewState.consentSolution, it, true)
          }
          else -> Unit
        }
      }
      .launchIn(scope)
  }

  /**
   * Disposes the instance. After calling this method the instance can not be used again.
   */
  @MainThread
  fun dispose() {
    scope.cancel()
    listener = null
    view = null
  }

  /**
   * Authenticate the client id with secret key and solution id and save the access token.
   */
  fun authenticate() {
    // First check that client id, secret key and solution id are present
    if (consentSdk.getClientId().isBlank() || consentSdk.getSecretId().isBlank() || consentSdk.getConsentSolutionId()
        .isBlank()
    ) {
      throw RuntimeException("\nMobileConsentSdk.Builder is missing client id and/or client secret and/or solution id. Please add:\nsetClientId(XXX)\nsetClientSecret(XXX)\nsetSolutionId(XXX)")
    }

    preferences.getAccessToken()?.let {
      // We have a valid access token
     fetchConsentSolution()
    } ?: fetchToken()
  }

  /**
   * Fetches the access token from authentication server, and saves it in shared preferences.
   */
  private fun fetchToken() {
    scope.launch {
      try {
        viewState = ViewState.Authenticating
        val tokenResponse = consentSdk.fetchToken()
        preferences.setTokenResponse(tokenResponse)
        ViewState.Authenticated
        // We have a valid access token
        fetchConsentSolution()
      } catch (_: IOException) {
        viewState = ViewState.AuthenticateError
      }
    }
  }

  /**
   * Fetches the consent solution from server, reads saved choices and shows data if there is an attached view.
   */
  private fun fetchConsentSolution() {
    scope.launch {
      try {
        viewState = ViewState.Fetching
        val consentSolution = consentSdk.fetchConsentSolution()
        viewState = ViewState.Fetched(createViewData(consentSolution, consentSdk.getSavedConsents()), consentSolution)
      } catch (_: IOException) {
        viewState = ViewState.FetchError
      }
    }
  }

  /**
   * Sends the user choice to server and saves it locally.
   */
  fun sendConsent() {
    scope.launch {
      try {
        @Suppress("UNCHECKED_CAST")
        val currentViewState = viewState as? ViewState.Fetched<ViewDataType>?
        currentViewState?.let {
          viewState = ViewState.Sending(it.data, it.consentSolution)
          consentSdk.postConsent(createConsent())
          listener?.onConsentsChosen(it.consentSolution, consentSdk.getSavedConsents(), false)
        }
      } catch (_: IOException) {
        val currentViewState = viewState
        require(currentViewState is ViewState.Sending<*>)
        viewState = ViewState.SendError(currentViewState.data, currentViewState.consentSolution)
      }
    }
  }

  private fun createConsent(): Consent {
    @Suppress("UNCHECKED_CAST")
    val currentViewState = viewState as ViewState.Sending<ViewDataType>
    val consentSolution = currentViewState.consentSolution
    return Consent(
      consentSolutionId = consentSolution.consentSolutionId,
      consentSolutionVersionId = consentSolution.consentSolutionVersionId,
      processingPurposes = createProcessingPurposes(consentSolution, currentViewState.data),
      customData = emptyMap()
    )
  }

  private fun createProcessingPurposes(
    consentSolution: ConsentSolution,
    viewData: ViewDataType,
  ): List<ProcessingPurpose> {
    val givenConsents = getGivenConsents(viewData)
    return consentSolution.consentItems.map {
      val givenConsent = givenConsents[it.consentItemId] // not null => type == Setting
      it.toProcessingPurpose(givenConsent)
    }
  }

  private fun ConsentItem.toProcessingPurpose(givenConsent: Pair<Boolean, String>?) = ProcessingPurpose(
    consentItemId = consentItemId,
    consentGiven = givenConsent?.first ?: false,
    language = givenConsent?.second ?: TextTranslation.getTranslationFor(shortText, locales).languageCode,
    type = this.type
  )

  protected fun ConsentItem.toPrivacyPreferencesItem(savedConsents: Map<UUID, Boolean>): PrivacyPreferencesItem {
    val textTranslation = shortText.translate()

    return PrivacyPreferencesItem(
      id = consentItemId,
      required = required,
      accepted = savedConsents[consentItemId] ?: false || required,
      text = textTranslation.text,
      details = longText.translate().text,
      language = textTranslation.languageCode,
      type = this.type
    )
  }

  protected fun ConsentItem.toPrivacyInfoItem(): PrivacyInfoItem {
    val textTranslation = shortText.translate()

    return PrivacyInfoItem(
      text = textTranslation.text,
      details = longText.translate().text,
      language = textTranslation.languageCode,
      type = type
    )
  }

  protected fun List<TextTranslation>.translate(): TextTranslation =
    TextTranslation.getTranslationFor(this, locales)

  protected abstract fun getViewIntentListener(): ViewIntentListenerType

  protected abstract fun createViewData(
    consentSolution: ConsentSolution,
    savedConsents: Map<UUID, Boolean>
  ): ViewDataType

  protected abstract fun getGivenConsents(viewData: ViewDataType): GivenConsent

  protected abstract fun onConsentsChangedWhileFetched(consents: Map<UUID, Boolean>)

  protected abstract fun onConsentsChangedWhileSendError(consents: Map<UUID, Boolean>)
}
