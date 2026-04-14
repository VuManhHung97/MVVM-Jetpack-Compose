package com.vmh.mvvmjetpackcompose.core.domain.repository

import com.github.michaelbull.result.Result
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

@Suppress("TooManyFunctions")
interface AuthRepository {
  suspend fun signUp(email: String, password: String): Result<Unit, AppError>

  suspend fun signIn(email: String, password: String): Result<Unit, AppError>
}
