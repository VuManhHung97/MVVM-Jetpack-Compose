package com.vmh.mvvmjetpackcompose.core.local.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.local.LocalUser
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface AuthLocalDataSource {

  fun observeLocalUser(): Flow<Result<LocalUser?, AppError.LocalStorageException>>

  suspend fun readLocalUser(): Result<LocalUser?, AppError.LocalStorageException> = observeLocalUser().first()

  suspend fun update(transform: (LocalUser?) -> LocalUser?): Result<Unit, AppError.LocalStorageException>
}
