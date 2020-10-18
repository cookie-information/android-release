package com.cookieinformation.mobileconsents.storage

import com.cookieinformation.mobileconsents.ProcessingPurpose
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.util.UUID

private const val userIdKey = "user_id_key"
private const val scratchFileSuffix = ".tmp"

/**
 * Storage responsible for saving generated [UUID] of user and all consent choices.
 * Data is stored in text file inside of App internal memory. All data is stored as Map<String, String> and
 * serialized to JSON. For serialization Moshi library is being used [MoshiFileHandler]
 * @param mutex used for synchronization of write operations.
 * @param file storage's text file.
 * @param fileHandler used for serialization.
 * @param dispatcher coroutine IO dispatcher.
 */
internal class ConsentStorage(
  private val mutex: Mutex,
  private val file: File,
  private val fileHandler: MoshiFileHandler,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

  init {
    createStorageFile(file)
  }

  /**
   * Store list of consent choices ([ProcessingPurpose]) as Map of UUIDs and Booleans.
   */
  suspend fun storeConsentChoices(purposes: List<ProcessingPurpose>) =
    writeValues(purposes.associate { it.consentItemId.toString() to it.consentGiven.toString() })

  /**
   * Obtain user id necessary for posting consents. If there is no user id stored it is generated, it should only happen
   * once per App's installation.
   */
  suspend fun getUserId(): UUID {
    val userId = readValue(userIdKey)
    return if (userId == null) {
      val newUserId = UUID.randomUUID()
      writeValues(mapOf(userIdKey to newUserId.toString()))
      newUserId
    } else {
      UUID.fromString(userId)
    }
  }

  suspend fun getConsentChoice(consentId: UUID): Boolean {
    val value = readValue(consentId.toString())

    return value.toBoolean()
  }

  /**
   * Get all of stored consent choices. User id is filtered out.
   */
  suspend fun getAllConsentChoices(): Map<UUID, Boolean> =
    readAll()
      .filterKeys { it != userIdKey }
      .entries.associate { UUID.fromString(it.key) to it.value.toBoolean() }

  /**
   * Write new values to storage. Old data is copied from storage file to scratch file, along with new data.
   * If any of new data has same key as old one, the old one will be overwritten with new value. After successful
   * copying the scratch file is renamed to the storage file. Function is synchronized via mutex shared across all
   * SDK instances.
   */
  private suspend fun writeValues(values: Map<String, String>) = withContext(dispatcher) {
    mutex.withLock {
      val scratchFile = File(file.path + scratchFileSuffix)
      try {
        val data = readAll() + values

        scratchFile.sink().use { sink ->
          fileHandler.writeTo(sink, data)
        }

        if (!scratchFile.renameTo(file)) {
          throw IOException("$scratchFile could not be renamed to $file")
        }
      } catch (e: IOException) {
        scratchFile.delete()
        throw e
      }
    }
  }

  private suspend fun readValue(key: String): String? {
    val data = readAll()
    return data[key]
  }

  private suspend fun readAll(): Map<String, String> = withContext(dispatcher) {
    file.source().use(fileHandler::readFrom)
  }

  /**
   * Create storage file and its parent directories if needed.
   */
  private fun createStorageFile(file: File) {
    file.parentFile?.mkdirs()
    if (!file.exists()) {
      file.createNewFile()
    }
  }
}