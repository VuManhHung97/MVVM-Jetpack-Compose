package com.vmh.mvvmjetpackcompose.core.local.datasourceimpl

import androidx.datastore.core.DataStore
import com.vmh.mvvmjetpackcompose.core.local.LocalUser
import com.vmh.mvvmjetpackcompose.core.local.datasource.AuthLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.mapper.LocalErrorMapper
import com.vmh.mvvmjetpackcompose.core.local.mapper.catchingLocalStorageException
import com.vmh.mvvmjetpackcompose.core.local.model.isNotDefault
import java.io.IOException
import javax.inject.Inject
import kotlin.takeIf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class AuthLocalDataSourceImpl @Inject constructor(
  private val localUserDataStore: DataStore<LocalUser>,
  private val localErrorMapper: LocalErrorMapper,
) : AuthLocalDataSource {

  override fun observeLocalUser() = localUserDataStore
    .data
    .map { localUser -> localUser.takeIf { it.isNotDefault() } }
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
  override suspend fun update(transform: (LocalUser?) -> LocalUser?) =
    catchingLocalStorageException(localErrorMapper) {
      localUserDataStore.updateData { currentLocalUser ->
        val updated = transform(currentLocalUser.takeIf { it.isNotDefault() })
        updated ?: LocalUser.getDefaultInstance()
      }
      Unit
    }
}
