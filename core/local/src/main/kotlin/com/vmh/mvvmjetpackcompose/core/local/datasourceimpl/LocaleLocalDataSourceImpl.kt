package com.vmh.mvvmjetpackcompose.core.local.datasourceimpl

import androidx.datastore.core.DataStore
import com.vmh.mvvmjetpackcompose.core.local.LocalLocale
import com.vmh.mvvmjetpackcompose.core.local.datasource.LocaleLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.mapper.LocalErrorMapper
import com.vmh.mvvmjetpackcompose.core.local.mapper.catchingLocalStorageException
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged

internal class LocaleLocalDataSourceImpl @Inject constructor(
  private val localLocaleDataStore: DataStore<LocalLocale>,
  private val localErrorMapper: LocalErrorMapper,
) : LocaleLocalDataSource {

  override fun observe() = localLocaleDataStore
    .data
    .catch {
      if (it is IOException) {
        emit(LocalLocale.getDefaultInstance())
      } else {
        throw it
      }
    }
    .catchingLocalStorageException(localErrorMapper)
    .distinctUntilChanged()

  @Suppress("OptionalUnit")
  override suspend fun update(localLocale: LocalLocale) = catchingLocalStorageException(localErrorMapper) {
    localLocaleDataStore.updateData { localLocale }
    Unit
  }
}
