package com.vmh.mvvmjetpackcompose.core.local.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.local.LocalFcmToken
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Persists the device's FCM registration state ([LocalFcmToken]) locally.
 *
 * Kept device-scoped and independent of [AuthLocalDataSource]: the token is issued before sign-in
 * and must outlive logout. Interface is `public`; the implementation is `internal`.
 */
interface FcmTokenLocalDataSource {

  fun observeFcmToken(): Flow<Result<LocalFcmToken?, AppError.LocalStorageException>>

  suspend fun readFcmToken(): Result<LocalFcmToken?, AppError.LocalStorageException> = observeFcmToken().first()

  suspend fun update(transform: (LocalFcmToken?) -> LocalFcmToken?): Result<Unit, AppError.LocalStorageException>
}
