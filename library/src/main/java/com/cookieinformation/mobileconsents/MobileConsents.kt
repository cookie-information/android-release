package com.cookieinformation.mobileconsents

import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import com.cookieinformation.mobileconsents.ConsentItem.Type
import com.cookieinformation.mobileconsents.storage.ConsentWithType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
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
    listener.launch(null)//nothing to pass the intent
  }

  public fun displayConsentsIfNeeded(
    listener: ActivityResultLauncher<Bundle?>,
    onError: (e: IOException) -> Unit
  ) {
    scope.launch {
      try {
        if (shouldDisplayConsents()) {
          displayConsents(listener)
        }
      } catch (e: IOException) {
        onError(e)
      }
    }
  }

  public suspend fun shouldDisplayConsents(): Boolean {
    val solution = getMobileConsentSdk().fetchConsentSolution().consentSolutionVersionId
    val hasVersionUpdated = getMobileConsentSdk().getLatestStoredConsentVersion().toString() != solution.toString()
    return !getConsents().containsValue(true) || hasVersionUpdated
  }

  public suspend fun getConsents(): Map<UUID, Boolean> {
    return getMobileConsentSdk().getSavedConsents()
  }

  public suspend fun haveConsentsBeenAccepted(): Boolean {
    val consents = getMobileConsentSdk().getSavedConsents()
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
