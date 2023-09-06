package com.cookieinformation.mobileconsents

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.cookieinformation.mobileconsents.adapter.extension.logException
import com.cookieinformation.mobileconsents.storage.ConsentWithType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID
import java.util.concurrent.CopyOnWriteArraySet

/**
 * SDK allowing easy management of user consents.
 * @param mobileConsentSdk instance of coroutine version of the SDK
 * @param dispatcher used for all async operations.
 *
 * For SDK instantiating use [MobileConsents.from] static function.
 */
public class MobileConsents constructor(
  private val mobileConsentSdk: MobileConsentSdk,
  dispatcher: CoroutineDispatcher = Dispatchers.Main
) {

  private val scope = CoroutineScope(dispatcher)
  private val saveConsentsObservers = CopyOnWriteArraySet<SaveConsentsObserver>()
  private var saveConsentsFlowCollectJob: Job? = null

  /**
   * This function (or initSDK) initializes the sdk and performs necessary migrations. It must be called (and completed) before
   * any other function that utilizes the sdk functionality, like showing the consent popup or
   * retrieving the saved consents.
   */
  public fun initSDKWithCompletion(onComplete: (success: Boolean, exception: IOException?) -> Unit) {
    // For now the only thing we have to do here is to perform a migration from the version that introduced
    // a problem consent types
    scope.launch {
      try {
        mobileConsentSdk.init()
        onComplete(true, null)
      } catch (e: IOException) {
        e.printStackTrace()
        onComplete(false, e)
      }
    }
  }

  /**
   * Returns associated instance of MobileConsentSdk.
   *
   * @return associated instance of MobileConsentSdk.
   */
  public fun getMobileConsentSdk(): MobileConsentSdk = mobileConsentSdk

  /**
   * Obtain [ConsentSolution] from CDN server.
   * @param listener listener for success/failure of operation.
   * @returns [Subscription] an object allowing for call cancellation.
   */
  public fun fetchConsentSolution(listener: CallListener<ConsentSolution>): Subscription {
    val job = scope.launch {
      try {
        val result = mobileConsentSdk.fetchConsentSolution()
        listener.onSuccess(result)
      } catch (error: IOException) {
        listener.onFailure(error)
      }
    }

    return JobSubscription(job)
  }

  /**
   * Post [Consent] to server specified by url in SDK's builder.
   * @param consent consent object.
   * @param listener listener for success/failure of operation.
   * @returns [Subscription] object allowing for call cancellation.
   */
  public fun postConsent(consent: Consent, listener: CallListener<Unit>): Subscription {
    val job = scope.launch {
      try {
        mobileConsentSdk.postConsent(consent)
        listener.onSuccess(Unit)
      } catch (e: IOException) {
        listener.onFailure(e)
      }
    }

    return JobSubscription(job)
  }

  /**
   * Obtain past consent choices stored on device memory. Returns Map of ConsentItem id and choice in a form of Boolean.
   * @param listener listener for success/failure of operation.
   * @return [Subscription] object allowing for call cancellation.
   */
  public fun getSavedConsents(listener: CallListener<Map<UUID, Boolean>>): Subscription {
    val job = scope.launch {
      try {
        listener.onSuccess(mobileConsentSdk.getSavedConsents())
      } catch (e: IOException) {
        listener.onFailure(e)
      }
    }

    return JobSubscription(job)
  }

  /**
   * Obtain past consent choices stored on device memory. Returns Map of ConsentItem id and choice in a form of Boolean.
   * @param listener listener for success/failure of operation.
   * @return [Subscription] object allowing for call cancellation.
   */
  public fun getSavedConsentsWithType(listener: CallListener<Map<UUID, ConsentWithType>>): Subscription {
    val job = scope.launch {
      try {
        listener.onSuccess(mobileConsentSdk.getSavedConsentsWithType())
      } catch (e: IOException) {
        listener.onFailure(e)
      }
    }

    return JobSubscription(job)
  }

  /**
   * Obtain specific Consent choice stored on device memory. If choice is not stored in memory, this will return `false`.
   * @param listener listener for success/failure of operation.
   * @return [Subscription] object allowing for call cancellation.
   */
  public fun getSavedConsent(consentItemId: UUID, listener: CallListener<Boolean>): Subscription {
    val job = scope.launch {
      try {
        listener.onSuccess(mobileConsentSdk.getSavedConsent(consentItemId))
      } catch (e: IOException) {
        listener.onFailure(e)
      }
    }

    return JobSubscription(job)
  }

  private class JobSubscription(val job: Job) : Subscription {
    override fun cancel() = job.cancel()
  }

  /**
   * Registers the instance of [SaveConsentsObserver]. This method must be called from UI thread.
   */
  public fun registerSaveConsentsObserver(observer: SaveConsentsObserver) {
    saveConsentsObservers.add(observer)
    if (saveConsentsFlowCollectJob == null) {
      saveConsentsFlowCollectJob = mobileConsentSdk.saveConsentsFlow
        .onEach { consents ->
          saveConsentsObservers.forEach { it.onConsentsSaved(consents) }
        }
        .launchIn(scope)
    }
  }

  /**
   * Unregisters the instance of [SaveConsentsObserver]. This method must be called from UI thread.
   */
  public fun unregisterSaveConsentsObserver(observer: SaveConsentsObserver) {
    saveConsentsObservers.remove(observer)
    if (saveConsentsObservers.isEmpty()) {
      saveConsentsFlowCollectJob?.cancel()
      saveConsentsFlowCollectJob = null
    }
  }

  public fun displayConsents(
    listener: ActivityResultLauncher<Bundle?>,
  ) {
    try {
      listener.launch(null)//nothing to pass the intent
    } catch (e: IllegalStateException) {
      //avoid crash
    } catch (e: ActivityNotFoundException) {
      //avoid crash
    }
  }

  public fun displayConsentsIfNeeded(
    listener: ActivityResultLauncher<Bundle?>,
    onError: (e: Exception) -> Unit
  ) {
    scope.launch {
      try {
        if (shouldDisplayConsents()) {
          displayConsents(listener)
        }
      } catch (e: IllegalStateException) {
        //avoid crash
        logException(e)
      } catch (e: ActivityNotFoundException) {
        //avoid crash
        logException(e)
      } catch (e: UnknownHostException) {
        //avoid crash
        logException(e)
      }catch (e: Exception) {
        onError(e)
      }
    }
  }

  public suspend fun shouldDisplayConsents(): Boolean {
    // get current local solution version before fetching solution from the server
    // because the local version gets overwritten in fetchConsentSolution()
    val currentLocalVersion = getMobileConsentSdk().getLatestStoredConsentVersion()
    val solution = getMobileConsentSdk().fetchConsentSolution().consentSolutionVersionId
    val hasVersionUpdated = currentLocalVersion.toString() != solution.toString()

    return (!getMobileConsentSdk().getOldSavedConsents()
      .containsValue(true) && !getConsents().containsValue(true)) || hasVersionUpdated
  }

  public suspend fun getConsents(): Map<UUID, Boolean> {
    return getMobileConsentSdk().getSavedConsents()
  }

  public suspend fun hasUserInteractedWithConsents(): Boolean {
    val consents: Map<UUID, Boolean> = getMobileConsentSdk().getSavedConsents()
    return consents.containsValue(true)
  }

  public suspend fun resetAllConsentChoices() {
    return getMobileConsentSdk().resetAllConsentChoices()
  }

  public suspend fun resetConsentChoice(consentKey: UUID) {
    return getMobileConsentSdk().resetConsentChoice(consentKey)
  }
}

/**
 * Interface returned from every async operation of SDK, use it for cancellation of background operations.
 */
public interface Subscription {
  /**
   * Cancel ongoing background operation.
   */
  public fun cancel()
}

/**
 * Callback used for all async operations in SDK.
 */
public interface CallListener<in T> {
  /**
   * Retrieves successful result of async operation.
   * @return result of async operation.
   */
  public fun onSuccess(result: T)

  /**
   * Retrieves failure of async operation.
   * @return [IOException]
   */
  public fun onFailure(error: IOException)
}

/**
 * The Observer interface for monitoring every "contents save" event.
 */
public interface SaveConsentsObserver {
  /**
   * Called every time when consents are saved
   */
  public fun onConsentsSaved(consents: Map<UUID, Boolean>)
}
