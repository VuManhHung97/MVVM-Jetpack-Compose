package com.vmh.mvvmjetpackcompose.core.local.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.local.LocalLocale
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlinx.coroutines.flow.Flow

interface LocaleLocalDataSource {
  fun observe(): Flow<Result<LocalLocale, AppError.LocalStorageException>>
  suspend fun update(localLocale: LocalLocale): Result<Unit, AppError.LocalStorageException>
}
