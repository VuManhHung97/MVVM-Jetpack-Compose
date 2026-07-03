package com.vmh.mvvmjetpackcompose.core.network.remote.datasource

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

interface FcmTokenRemoteDataSource {
  /** Registers [token] with the backend for the currently authenticated user. */
  suspend fun registerFcmToken(token: String): Result<Unit, AppError.ApiException>
}
