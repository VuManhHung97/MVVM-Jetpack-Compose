package com.vmh.mvvmjetpackcompose.core.local.datasourceimpl

import androidx.datastore.core.DataStore
import com.vmh.mvvmjetpackcompose.core.local.LocalFcmToken
import com.vmh.mvvmjetpackcompose.core.local.datasource.FcmTokenLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.mapper.LocalErrorMapper
import com.vmh.mvvmjetpackcompose.core.local.mapper.catchingLocalStorageException
import com.vmh.mvvmjetpackcompose.core.local.model.isNotDefault
import java.io.IOException
import javax.inject.Inject
import kotlin.takeIf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class FcmTokenLocalDataSourceImpl @Inject constructor(
  private val fcmTokenDataStore: DataStore<LocalFcmToken>,
  private val localErrorMapper: LocalErrorMapper,
) : FcmTokenLocalDataSource {

  override fun observeFcmToken() = fcmTokenDataStore
    .data
    .map { localFcmToken -> localFcmToken.takeIf { it.isNotDefault() } }
    .catch {
      // dataStore.data throws an IOException when an error is encountered when reading data
      if (it is IOException) {
        emit(null)
      } else {
        throw it
      }
    }
    .catchingLocalStorageException(localErrorMapper)
    .distinctUntilChanged()

  @Suppress("OptionalUnit") // False positive
  override suspend fun update(transform: (LocalFcmToken?) -> LocalFcmToken?) =
    catchingLocalStorageException(localErrorMapper) {
      fcmTokenDataStore.updateData { currentLocalFcmToken ->
        val updated = transform(currentLocalFcmToken.takeIf { it.isNotDefault() })
        updated ?: LocalFcmToken.getDefaultInstance()
      }
      Unit
    }
}
