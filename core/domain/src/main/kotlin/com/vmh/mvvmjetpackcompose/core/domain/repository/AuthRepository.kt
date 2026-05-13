package com.vmh.mvvmjetpackcompose.core.domain.repository

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.auth.AuthenticationState
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlinx.coroutines.flow.Flow

@Suppress("TooManyFunctions")
interface AuthRepository {
  suspend fun signUp(email: String, password: String): Result<Unit, AppError>

  suspend fun signIn(email: String, password: String): Result<Unit, AppError>

  fun observeAuthenticationState(): Flow<Result<AuthenticationState, AppError.LocalStorageException>>

  suspend fun refreshCurrentUser(): Result<Unit, AppError>

  suspend fun logout(): Result<Unit, AppError>
}
